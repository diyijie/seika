package io.seika.net.ssl;

import java.io.IOException;

import io.netty.handler.ssl.SslContext;
import io.seika.transport.Message;
import io.seika.transport.ServerAdaptor;
import io.seika.transport.Ssl;
import io.seika.transport.Server;
import io.seika.transport.Session;
import io.seika.transport.http.HttpWsServer;

public class SslServerExample {

	@SuppressWarnings("resource")
	public static void main(String[] args) { 
		
		SslContext context = Ssl.buildServerSsl("ssl/zbus.crt", "ssl/zbus.key");
		
		Server server = new HttpWsServer();   
		ServerAdaptor adaptor = new ServerAdaptor() {
			@Override
			public void onMessage(Object msg, Session sess) throws IOException { 
				Message res = new Message();
				res.setStatus(200);
				res.setHeader("content-type", "text/html; charset=utf8"); 
				res.setBody("<h1>hello world</h1>");  
				
				sess.write(res);
			}
		}; 
		server.start(8080, adaptor, context);  
	} 
}
