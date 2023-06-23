package io.zbus.mq;

import io.zbus.ZbusSeikaClient;
import io.zbus.auth.DefaultSign;
import io.zbus.auth.RequestSign;
import io.zbus.transport.Message;

import java.util.concurrent.TimeUnit;

public class Sub {
	public static void main(String[] args) throws Exception {
		final String mq = "MyMQQ", channel = "000", mqType = Protocol.MEMORY;

		ZbusSeikaClient dd = new ZbusSeikaClient("ws://127.0.0.1:15555", "", "");

		dd.Sub(mq, channel, data -> {
	System.out.println(data.getBody());
		});
	}
		@SuppressWarnings("resource")
	public static void main22(String[] args) throws Exception {
		final String mq = "MyMQQ", channel = "000", mqType = Protocol.MEMORY;



		MqClient client = new MqClient("ws://localhost:15555");

		client.heartbeat(30, TimeUnit.SECONDS);


		client.addMqHandler(mq, channel, data->{
			System.out.println(data);
		});

		client.onOpen(()->{
//			Message req = new Message();
//			req.setHeader("cmd", "create"); //create MQ/Channel
//			req.setHeader("mq", mq);
//			req.setHeader("mqType", mqType);
//			req.setHeader("channel", channel);
//
//
		String apiKey = "2ba912a8-4a8d-49d2-1a22-198fd285cb06";
			String secretKey = "461277322-943d-4b2f-b9b6-3f860d746ffd";
 		RequestSign sign = new DefaultSign();
//
//			sign.sign(req, apiKey, secretKey);
//
//			Message res = client.invoke(req);
//			System.out.println(res);

			Message sub = new Message();
			sub.setHeader("cmd", "sub"); //Subscribe on MQ/Channel
			sub.setHeader("mq", mq);
			sub.setHeader("channel", channel);
			//sub.setHeader("window", 1);
			//sub.setHeader("filter", "abc").
			//
			// +++-;



			sign.sign(sub, apiKey, secretKey);

			client.invoke(sub, data->{
				System.out.println(data);
			});
		});

		client.connect();
	} 
}
