package io.seika.mq;

import io.seika.ZbusSeikaMq;

public class Pub {

	public static MqClient buildInproClient() {
		MqServer server = new MqServer(new MqServerConfig());
		return new MqClient(server);
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		ZbusSeikaMq dd =new ZbusSeikaMq("ws://127.0.0.1:15555","","");
		//dd =new MqSpringClient(new MqServerConfig("./conf/zbus.xml"));
		for (int i = 0; i < 1000000; i++) {
			dd.Pub("MyMQQ",i,"000",null);
			//Thread.sleep(300);
		}
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
