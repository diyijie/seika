package io.seika.mq.plugin;

import java.util.Map.Entry;

import io.seika.kit.HttpKit;
import io.seika.mq.MqServerAdaptor;
import io.seika.rpc.RpcProcessor;
import io.seika.transport.Message;

public class MonitorUrlFilter implements Filter {   
	private RpcProcessor rpcProcessor;
	
	public MonitorUrlFilter(RpcProcessor rpcProcessor) {
		this.rpcProcessor = rpcProcessor;
	} 
	
	@Override
	public void init(MqServerAdaptor mqServerAdaptor) {
		
	}
	
	@Override
	public boolean doFilter(Message req, Message res) {
		String url = req.getUrl();
		if(url == null) return true;     
		
		if(url.startsWith("/?") || url.startsWith("?")) { //special case for headers injection
			HttpKit.UrlInfo info = HttpKit.parseUrl(url);
			if(info.queryParamMap.size() > 0) {
				for(Entry<String, Object> e : info.queryParamMap.entrySet()) {
					String key = e.getKey();
					Object value = e.getValue();
					if(key.equals("body")) {
						req.setBody(value);
					} else {
						req.setHeader(key, value);
					}
				} 
				return true;
			}
		}
		
		if(rpcProcessor != null) {
			if(rpcProcessor.matchUrl(url)) { 
				rpcProcessor.process(req, res); 
				return false;
			} 
		} 
		
		return true;
	} 
	
	public void setRpcProcessor(RpcProcessor rpcProcessor) {
		this.rpcProcessor = rpcProcessor;
	}
}
