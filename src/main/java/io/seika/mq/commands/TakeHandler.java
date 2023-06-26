package io.seika.mq.commands;

import java.io.IOException;

import io.seika.mq.MessageDispatcher;
import io.seika.mq.MqManager;
import io.seika.mq.Protocol;
import io.seika.mq.model.MessageQueue;
import io.seika.transport.Message;
import io.seika.transport.Session;

public class TakeHandler implements CommandHandler { 
	private final MessageDispatcher messageDispatcher;
	private final MqManager mqManager;
	
	public TakeHandler(MessageDispatcher messageDispatcher, MqManager mqManager) {
		this.messageDispatcher = messageDispatcher;
		this.mqManager = mqManager; 
	}
	
	@Override
	public void handle(Message req, Session sess) throws IOException {
		if(!MsgKit.validateRequest(mqManager, req, sess)) return;
		
		String mqName = (String)req.getHeader(Protocol.MQ);
		String channelName = (String)req.getHeader(Protocol.CHANNEL); 
		Integer window = req.getHeaderInt(Protocol.WINDOW); 
		String msgId = (String)req.getHeader(Protocol.ID);
		MessageQueue mq = mqManager.get(mqName);
		if(window == null) window = 1; 
		
	    messageDispatcher.take(mq, channelName, window, msgId, sess); 
	} 
}
