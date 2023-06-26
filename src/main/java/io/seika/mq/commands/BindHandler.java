package io.seika.mq.commands;

import java.io.IOException;

import io.seika.mq.NotifyManager;
import io.seika.transport.Message;
import io.seika.transport.Session;

public class BindHandler implements CommandHandler {  
	protected final NotifyManager notifyManager;
	
	public BindHandler(NotifyManager notifyManager) { 
		this.notifyManager = notifyManager;
	}
	
	@Override
	public void handle(Message req, Session sess) throws IOException {
		 
	} 
}
