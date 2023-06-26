package io.seika.mq.plugin;

import io.seika.mq.MqServerAdaptor;
import io.seika.transport.Message;

public interface Filter {
	
	void init(MqServerAdaptor mqServerAdaptor);
	
	/** 
	 * @param req
	 * @param resp
	 * @return true if next filter execution required
	 */
	boolean doFilter(Message req, Message resp);
}
