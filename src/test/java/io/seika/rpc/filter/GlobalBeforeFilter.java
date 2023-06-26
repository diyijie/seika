package io.seika.rpc.filter;

import java.util.HashMap;
import java.util.Map;

import io.seika.transport.Message;
import io.seika.rpc.RpcFilter;

//@FilterDef(type=FilterType.GlobalBefore)
public class GlobalBeforeFilter implements RpcFilter {

	@Override
	public boolean doFilter(Message request, Message response, Throwable exception) {
		System.out.println("[Filter=GlobalBefore]: " + request);
		Map<String, Object> ctx = new HashMap<>();
		ctx.put("accessTime", System.currentTimeMillis());
		request.setContext(ctx);
		return true;
	} 
}
