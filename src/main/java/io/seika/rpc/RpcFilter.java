package io.seika.rpc;

import io.seika.transport.Message;

public interface RpcFilter { 
	/**
	 * 
	 * @param request
	 * @param response
	 * @param exception exception trigger before filter
	 * @return true if continue to handle request response
	 */
	boolean doFilter(Message request, Message response, Throwable exception);
}
