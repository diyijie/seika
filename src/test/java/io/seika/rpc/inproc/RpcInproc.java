package io.seika.rpc.inproc;

import java.util.concurrent.atomic.AtomicInteger;

import io.seika.transport.Message;
import io.seika.mq.MqServer;
import io.seika.rpc.RpcProcessor;
import io.seika.rpc.biz.InterfaceExampleImpl;
import io.seika.transport.inproc.InprocClient;

public class RpcInproc {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		RpcProcessor p = new RpcProcessor();
		p.mount("/example", InterfaceExampleImpl.class);
		 
		MqServer server = new MqServer();
		server.setRpcProcessor(p);
		
		InprocClient rpc = new InprocClient(server.getServerAdaptor());
		
		AtomicInteger count = new AtomicInteger(0);  
		for (int i = 0; i < 1000000; i++) {
			Message req = new Message();
			req.setUrl("/example/getOrder"); 
			
			rpc.invoke(req, res->{
				int c = count.getAndIncrement();
				if(c % 10000 == 0) {
					System.out.println(c + ": " + res);
				}
			});
		}
		//rpc.close();
	}
}
