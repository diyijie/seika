package io.seika.rpc.filter;

import io.seika.rpc.annotation.FilterDef;
import io.seika.transport.Message;
import io.seika.rpc.RpcFilter;

@FilterDef("admin")
public class AdminLoginFilter implements RpcFilter {

	@Override
	public boolean doFilter(Message request, Message response, Throwable exception) {
		System.out.println("[Filter=admin]: " + request); 
		return true;
	} 
}
