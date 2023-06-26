package io.seika.mq.commands;

import java.io.IOException;

import io.seika.mq.MqManager;
import io.seika.mq.Protocol;
import io.seika.transport.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.seika.transport.Session;

public class RemoveHandler implements CommandHandler { 
	private static final Logger logger = LoggerFactory.getLogger(RemoveHandler.class);  
	private final MqManager mqManager;
	
	public RemoveHandler(MqManager mqManager) { 
		this.mqManager = mqManager;
	}
	
	@Override
	public void handle(Message req, Session sess) throws IOException {
		String mqName = (String)req.getHeader(Protocol.MQ);
		if(mqName == null) {
			MsgKit.reply(req, 400, "remove command, missing mq field", sess);
			return;
		}
		String channel = (String)req.getHeader(Protocol.CHANNEL);
		try {
			mqManager.removeQueue(mqName, channel);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			MsgKit.reply(req, 500, e.getMessage(), sess);
			return;
		}
		String msg = String.format("OK, REMOVE (mq=%s,channel=%s)", mqName, channel); 
		if(channel == null) {
			msg = String.format("OK, REMOVE (mq=%s)", mqName); 
		}
		MsgKit.reply(req, 200, msg, sess);
	} 
}
