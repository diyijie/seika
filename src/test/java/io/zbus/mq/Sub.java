package io.zbus.mq;

import io.zbus.auth.DefaultSign;
import io.zbus.auth.RequestSign;
import io.zbus.transport.Message;

import java.util.concurrent.TimeUnit;

public class Sub {  
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception { 
		MqClient client = new MqClient("ws://localhost:15555");
		
		client.heartbeat(30, TimeUnit.SECONDS);
		
		final String mq = "MyMQ", channel = "MyChannel", mqType = Protocol.MEMORY;

		client.addMqHandler(mq, channel, data->{  
			System.out.println(data);  
		});  
		
		client.onOpen(()->{
			Message req = new Message();
			req.setHeader("cmd", "create"); //create MQ/Channel
			req.setHeader("mq", mq);
			req.setHeader("mqType", mqType);
			req.setHeader("channel", channel);


			String apiKey = "2ba912a8-4a8d-49d2-1a22-198fd285cb06";
			String secretKey = "461277322-943d-4b2f-b9b6-3f860d746ffd";
			RequestSign sign = new DefaultSign();

			sign.sign(req, apiKey, secretKey);

			Message res = client.invoke(req);
			System.out.println(res);
			
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
