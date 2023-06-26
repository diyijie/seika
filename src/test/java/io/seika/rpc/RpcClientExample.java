package io.seika.rpc;

import io.seika.transport.Message;

public class RpcClientExample { 
	
	public static void main(String[] args) throws Exception {
		RpcClient rpc = new RpcClient("localhost:15555");

		for(int i=0;i<10;i++) {

			Message req = new Message();
			req.setUrl("/plus");
			req.setBody(new Object[] {1,i}); //body as parameter array
			
			Message res = rpc.invoke(req); //同步调用
			System.out.println(res);
			
			System.out.println(">>>>>" + i);
		}
		rpc.close();

	}
}
