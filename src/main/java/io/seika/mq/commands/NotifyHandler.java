package io.seika.mq.commands;

import java.io.IOException;

import io.seika.kit.JsonKit;
import io.seika.mq.NotifyManager;
import io.seika.transport.Message;
import io.seika.transport.Session;

public class NotifyHandler implements CommandHandler {  
	private final NotifyManager notifyManager;
	
	public NotifyHandler(NotifyManager notifyManager) { 
		this.notifyManager = notifyManager;
	}
	
	@Override
	public void handle(Message req, Session sess) throws IOException {
		 NotifyManager.NotifyTarget target = JsonKit.convert(req.getBody(), NotifyManager.NotifyTarget.class);
		 notifyManager.addNotifyTarget(target.port, target.urlPrefix, sess);
	} 
}
