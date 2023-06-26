package io.seika.rpc.filter;

import io.seika.rpc.annotation.FilterDef;
import io.seika.rpc.annotation.FilterType;
import io.seika.transport.Message;
import io.seika.rpc.RpcFilter;

@FilterDef(type= FilterType.Exception)
public class GlobalExceptionFilter implements RpcFilter {

	@Override
	public boolean doFilter(Message request, Message response, Throwable exception) {
		exception.printStackTrace();
		response.setStatus(500);
		response.setBody("global exception filter: " + exception.getMessage());
		return false;
	} 
}
