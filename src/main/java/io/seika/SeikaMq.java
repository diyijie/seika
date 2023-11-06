package io.seika;

import io.seika.kit.JsonKit;
import io.seika.mq.MqClient;
import io.seika.mq.MqServer;
import io.seika.mq.MqServerConfig;
import io.seika.mq.Protocol;
import io.seika.transport.DataHandler;
import io.seika.transport.EventHandler;
import io.seika.transport.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class SeikaMq implements EventHandler {
	private MqClient client ;
	private MqClient clientSub ;
	private static Map<String, Boolean> created = new ConcurrentHashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(SeikaMq.class);

	private String address ;

	public String getAddress() {
		return address;
	}
 

	public SeikaMq(MqServerConfig config ){
		MqServer server = new MqServer(config);
		address = config.publicServer.getAddress();
 		client= newmq(server);
		clientSub =newmq(server);
	}
	private MqClient newmq(MqServer server){
		MqClient client= new MqClient(server);
		client.heartbeat(30, TimeUnit.SECONDS);

		client.onOpen(this);
		//重连周期
		client.setReconnectDelay(10000);
		return client;
	}
	private MqClient newc(String address,String apiKey,String secretKey){
		MqClient client = new MqClient(address);

 //		apiKey = "2ba912a8-4a8d-49d2-1a22-198fd285cb06";
//		secretKey = "461277322-943d-4b2f-b9b6-3f860d746ffd";
		if (apiKey!=null && secretKey!=null && !"".equals(apiKey) && !"".equals(secretKey)){
			client.setAuthEnabled(true);
			client.setApiKey(apiKey);
			client.setSecretKey(secretKey);
		}
		client.heartbeat(30, TimeUnit.SECONDS);
		client.onOpen(this);
		this.address = address;
		return client ;
	}
	private ExecutorService rePubExecutorService=  Executors.newSingleThreadExecutor();
	//private MpscArrayQueue<Pair<Message, DataHandler>> queue= new MpscArrayQueue<>(1000);

	public SeikaMq(String address, String apiKey, String secretKey){
		clientSub = newc(address,apiKey,secretKey);
		client = newc(address,apiKey,secretKey);
		//实际上没有用到 因为errhanderl 不会被执行 内部如果没有发送出去的会缓存到list 然后有连接了再发送走
//		rePubExecutorService.submit((Runnable) () -> {
//			while(true){
//				Pair<Message, DataHandler> obj = queue.relaxedPoll();
//
//				if(obj!=null)	{
//					client.invoke(obj.first, res->{ //async call
//						if (obj.second!=null){
//							obj.second.handle(res);
//						}
//					}, (e) -> {
//						queue.add(Pair.pair(obj.first, obj.second));
//					});
//				}
//			}
//		});

	}
	private void create(String mq,String channel)  throws IOException, InterruptedException {
		String key=mq+"."+channel;
		if (!created.containsKey(key)){
			final String  mqType = Protocol.DISK;

			//1) Create MQ if necessary
			Message req = new Message();
			req.setHeader("cmd", "create");  //Create
			req.setHeader("mq", mq);
			req.setHeader("mqType", mqType); //disk|memory|db
			if (channel!=null && !channel.equals("")){
			req.setHeader("channel", channel);
			}
			//sign.sign(req, apiKey, secretKey);
			client.invoke(req);
			created.put(key,true);
		}

	}

	public void  Pub(String mq, Object body,String channel,DataHandler<Message> dataHandler) throws IOException, InterruptedException {
		this.create(mq,channel);

		Message msg = new Message();
		msg.setHeader("cmd", "pub");  //Publish
		msg.setHeader("mq", mq);
		msg.setHeader("_Class_", body.getClass().getCanonicalName());
		if (channel!=null && !channel.equals("")){
			msg.setHeader("channel", channel);
		}
		msg.setBody(body);    //set business data in body
		client.invoke(msg, res->{ //async call
			if (dataHandler!=null){
				dataHandler.handle(res);
			}
		},e->{
			logger.warn("{}",e);
		//	queue.add(Pair.pair(msg,dataHandler));
		});

	}
	public <T> void  Sub(String mq, String channel, Consumer<T> c) throws IOException, InterruptedException {
		this.Sub(mq, channel, (DataHandler<Message>) data -> c.accept(data.getContext()));
	}

	public void  Sub(String mq,String channel,DataHandler<Message> dataHandler) throws IOException, InterruptedException {
		try {
			this.create(mq,channel);
		}catch (Exception e){
			throw e;
		}

		Message sub = new Message();
		sub.setHeader("cmd", "sub"); //Subscribe on MQ/Channel
		sub.setHeader("mq", mq);
		sub.setHeader("channel", channel);
		//sub.setHeader("window", 1);
		//sub.setHeader("filter", "abc").
		//
		// +++-;
		clientSub.addMqHandler(mq, channel, new DataHandler<Message>() {
			@Override
			public void handle(Message data) throws Exception {
				String cz = data.getHeader("_Class_");
				if (cz!=null && !"".equals(cz)){
					try {
						Object json = JsonKit.convert(data.getBody(), Class.forName(cz));
						data.setContext(json);
					}catch (Exception e){

					}
				}
				dataHandler.handle(data);
			}
		});

		clientSub.invoke(sub, data->{
			if (200==data.getStatus()){
				logger.debug("{}",data.getBody());
			}else{
				logger.warn("{}",data.getBody());
			}
 		},e -> {
			logger.error("sub fail",e);
		});

	}

	@Override
	public void handle() throws Exception {
// 如果不重新订阅那么 mq服务器重启后 会收不到消息了
		clientSub.getHandlers().forEach(h->{
			try {
				this.create(h.mq,h.channel);
			}  catch (IOException ex) {
					throw new RuntimeException(ex);
			} catch (InterruptedException ex) {
					throw new RuntimeException(ex);
			}

			Message sub = new Message();
			sub.setHeader("cmd", "sub"); //Subscribe on MQ/Channel
			sub.setHeader("mq", h.mq);
			sub.setHeader("channel", h.channel);
			clientSub.invoke(sub, data->{
			});
		});
	}
	//auto refresh ,if need des !
	public void des(){
		try {
			this.clientSub.close();
			this.client.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
