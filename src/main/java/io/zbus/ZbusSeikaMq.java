package io.zbus;

import io.zbus.mq.MqClient;
import io.zbus.mq.MqServer;
import io.zbus.mq.MqServerConfig;
import io.zbus.mq.Protocol;
import io.zbus.transport.DataHandler;
import io.zbus.transport.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ZbusSeikaMq {
	private MqClient client ;
	private MqClient clientSub ;
	private static Map<String, Boolean> created = new ConcurrentHashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(ZbusSeikaMq.class);

	public ZbusSeikaMq(MqServerConfig config ){
		MqServer server = new MqServer(config);
 		client= new MqClient(server);
		clientSub = new MqClient(server);
	}
	private MqClient newc(String address,String apiKey,String secretKey){
		MqClient client = new MqClient(address);
		client.heartbeat(30, TimeUnit.SECONDS);
//		apiKey = "2ba912a8-4a8d-49d2-1a22-198fd285cb06";
//		secretKey = "461277322-943d-4b2f-b9b6-3f860d746ffd";
		if (apiKey!=null && secretKey!=null && !"".equals(apiKey) && !"".equals(secretKey)){
			client.setAuthEnabled(true);
			client.setApiKey(apiKey);
			client.setSecretKey(secretKey);
		}
		client.heartbeat(30, TimeUnit.SECONDS);

		return client ;
	}
	public ZbusSeikaMq(String address, String apiKey, String secretKey){
		client = newc(address,apiKey,secretKey);
		clientSub = newc(address,apiKey,secretKey);
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
//			if (channel!=null && !channel.equals("")){
//			req.setHeader("channel", "channel");
//			}
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
		if (channel!=null && !channel.equals("")){
			msg.setHeader("channel", channel);
		}
		msg.setBody(body);    //set business data in body
		client.invoke(msg, res->{ //async call
			if (dataHandler!=null){
				dataHandler.handle(res);
			}
		});
	}
	public void  Sub(String mq,String channel,DataHandler<Message> dataHandler) throws IOException, InterruptedException {
		this.create(mq,channel);

		Message sub = new Message();
		sub.setHeader("cmd", "sub"); //Subscribe on MQ/Channel
		sub.setHeader("mq", mq);
		sub.setHeader("channel", channel);
		//sub.setHeader("window", 1);
		//sub.setHeader("filter", "abc").
		//
		// +++-;
		clientSub.addMqHandler(mq, channel, dataHandler);

		clientSub.invoke(sub, data->{
			if (200==data.getStatus()){
				logger.debug("%s",data);
			}else{
				logger.warn("%s",data);
			}
 		});

	}

}
