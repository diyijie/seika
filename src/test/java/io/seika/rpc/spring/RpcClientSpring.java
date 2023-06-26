package io.seika.rpc.spring;

import io.seika.rpc.RpcClient;
import io.seika.rpc.biz.InterfaceExample;

public class RpcClientSpring { 
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {  
		
//		ClassPathXmlApplicationContext ioc = new ClassPathXmlApplicationContext("rpc/spring-client.xml");
//		InterfaceExample example = ioc.getBean(InterfaceExample.class);
//		int c = example.plus(1, 2);
//		System.out.println(c);

		RpcClient rpc = new RpcClient("localhost:15555");
		rpc.setApiKey("xxx");
		rpc.setSecretKey("xxx");
		rpc.setAuthEnabled(true);
		InterfaceExample  example=rpc.createProxy("/example", InterfaceExample.class);
		int c = example.plus(4, 2);
		System.out.println(c);

	}
}
