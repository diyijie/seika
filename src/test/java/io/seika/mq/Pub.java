package io.seika.mq;

import io.seika.ZbusSeikaMq;
import io.seika.kit.JsonKit;
import io.seika.transport.DataHandler;
import io.seika.transport.Message;

import java.io.IOException;
class A{
	String a ;
	Integer b ;
	long c 		;

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public Integer getB() {
		return b;
	}

	public void setB(Integer b) {
		this.b = b;
	}

	public long getC() {
		return c;
	}

	public void setC(long c) {
		this.c = c;
	}
}
public class Pub {

	public static MqClient buildInproClient() {
		MqServer server = new MqServer(new MqServerConfig());
		return new MqClient(server);
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		ZbusSeikaMq dd =new ZbusSeikaMq("ws://127.0.0.1:15555","","");
		//dd =new MqSpringClient(new MqServerConfig("./conf/zbus.xml"));
		long st = System.currentTimeMillis();
		int nnnn=300;
		int msgn=100;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					for (int i = 1; i <= nnnn; i++) {
						String ss = "xxx" + i;
						A aaa = new A();
						aaa.a = i + "xxxx";
						aaa.c = System.currentTimeMillis()
						;
						aaa.b = i;


						int finalI2 = i;

						for (int j = 1; j <= msgn; j++) {
							aaa.c = j;
							int finalJ = j;
							int finalI = finalI2;
							try {
								dd.Pub("MyMQQ", aaa, "000" + finalI, dh -> {
									if (finalJ == msgn) {

									//	System.out.println(dh.getBody().toString()+ finalI +"--"+ finalJ);
										if (finalI == nnnn) {
											System.out.println("发送完成");
										}
									}
								});
							} catch (IOException e) {
								throw new RuntimeException(e);
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							}
						}
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}

				}




			}
		}
		);//.start(); ;
		ZbusSeikaMq dd2 =new ZbusSeikaMq("ws://127.0.0.1:15555","","");

		new Thread(() -> {
			final long[] lastn = {0};

			for (int i = 0; i <=nnnn; i++) {
				try {
					dd.Sub("MyMQQ", "000"+i, new DataHandler<Message>() {
						@Override
						public void handle(Message data) throws Exception {


							A aa = JsonKit.parseObject(data.getBody().toString(), A.class);
							  System.out.println(data.getBody());

							if (aa.b==nnnn && aa.c==msgn){

								long st2= System.currentTimeMillis();
								//300000消息用时162秒 3000 channel
								//300000消息用时25秒 300 channel
								//300000消息用时29秒 30 channel

								System.out.printf("M1收到消息，%d消息用时%d秒\n",nnnn*msgn,(st2-st- lastn[0])/1000);
								lastn[0] = st2-st;
							}
						}
					});
				} catch (IOException e) {
					throw new RuntimeException(e);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				//Thread.sleep(300);
			}
		}).start();
		new Thread(() -> {
			final long[] lastn = {0};

			for (int i = 0; i <=nnnn; i++) {
				try {
					dd2.Sub("MyMQQ", "000"+i, new DataHandler<Message>() {
						@Override
						public void handle(Message data) throws Exception {


							A aa = JsonKit.parseObject(data.getBody().toString(), A.class);

							if (aa.b==nnnn && aa.c==msgn){
								// System.out.println(data.getBody());

								long st2= System.currentTimeMillis();
								//300000消息用时162秒 3000 channel
								//300000消息用时25秒 300 channel
								//300000消息用时29秒 30 channel

								System.out.printf("M2收到消息，%d消息用时%d秒\n",nnnn*msgn,(st2-st- lastn[0])/1000);
								lastn[0] = st2-st;
							}
						}
					});
				} catch (IOException e) {
					throw new RuntimeException(e);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				//Thread.sleep(300);
			}
		});//.start();
 		Thread.sleep(15000);
		System.out.println("----end ");
//		MqClient client = new MqClient("ws://127.0.0.1:15555");
//
//		//MqClient client = buildInproClient();
//
//		client.heartbeat(30, TimeUnit.SECONDS);
//		final String mq = "MyMQ", mqType = Protocol.DISK;
//
//		//1) Create MQ if necessary
//		Message req = new Message();
//		req.setHeader("cmd", "create");  //Create
//		req.setHeader("mq", mq);
//		req.setHeader("mqType", mqType); //disk|memory|db
//
//		String apiKey = "2ba912a8-4a8d-49d2-1a22-198fd285cb06";
//		String secretKey = "461277322-943d-4b2f-b9b6-3f860d746ffd";
//		RequestSign sign = new DefaultSign();
//
//		MqClient client = new MqClient("ws://127.0.0.1:15555");
//
//		//MqClient client = buildInproClient();
//
//		client.heartbeat(30, TimeUnit.SECONDS);
//		final String mq = "MyMQ", mqType = Protocol.DISK;
//
//		//1) Create MQ if necessary
//		Message req = new Message();
//		req.setHeader("cmd", "create");  //Create
//		req.setHeader("mq", mq);
//		req.setHeader("mqType", mqType); //disk|memory|db
//
//		String apiKey = "2ba912a8-4a8d-49d2-1a22-198fd285cb06";
//		String secretKey = "461277322-943d-4b2f-b9b6-3f860d746ffd";
//		RequestSign sign = new DefaultSign();
//
//		sign.sign(req, apiKey, secretKey);
//
//		client.invoke(req);
//
//		AtomicInteger count = new AtomicInteger(0);
//		for (int i = 0; i < 200; i++) {
//			//2) Publish Message
//			Message msg = new Message();
//			msg.setHeader("cmd", "pub");  //Publish
//			msg.setHeader("mq", mq);
//			msg.setBody(i);    //set business data in body
//
//
//
//			sign.sign(msg, apiKey, secretKey);
//
//			client.invoke(msg, res->{ //async call
//				if(count.getAndIncrement() % 10000 == 0) {
//					System.out.println(res);
//				}
//			});
//			//	Thread.sleep(1000);
//		}

	}
}
