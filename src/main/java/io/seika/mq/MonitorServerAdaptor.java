package io.seika.mq;

import java.util.ArrayList;
import java.util.List;

import io.seika.rpc.RpcProcessor;
import io.seika.rpc.StaticResource;
import io.seika.rpc.annotation.Route;
import io.seika.transport.Message;
import io.seika.kit.FileKit;
import io.seika.mq.model.MessageQueue;
import io.seika.mq.plugin.MonitorUrlFilter;

public class MonitorServerAdaptor extends MqServerAdaptor {  
	
	private SubscriptionManager subscriptionManager;
	public MonitorServerAdaptor(MqServerAdaptor mqServerAdaptor) {
		super(mqServerAdaptor);
		
		requestAuth = null;
		if (config.monitorServer != null && config.monitorServer.auth != null) {
			requestAuth = config.monitorServer.auth; 
		}  
		
		this.subscriptionManager = mqServerAdaptor.subscriptionManager;
		
		this.rpcProcessor = new RpcProcessor();
		StaticResource staticResource = new StaticResource();
		staticResource.setCacheEnabled(false); // TODO turn if off in production
		
		rpcProcessor.mount("/", new MonitorService()); 
		rpcProcessor.mount("/static", staticResource, false);
		rpcProcessor.mountDoc(); 
		
		filterList.clear();
		filterList.add(new MonitorUrlFilter(rpcProcessor)); 
	}
 
	
	class MonitorService {
		private FileKit fileKit = new FileKit();  
		
		@Route(path = "/favicon.ico", docEnabled = false)
		public Message favicon() {
			return fileKit.render("static/favicon.ico");
		}
		
		@Route("/")
		public List<Protocol.MqInfo> home() {
			List<Protocol.MqInfo> res = mqManager.mqInfoList();
			for(Protocol.MqInfo mqInfo : res) {
				for(Protocol.ChannelInfo channelInfo : mqInfo.channelList) {
					channelInfo.subscriptions = subscriptionManager.getSubscriptionList(mqInfo.name, channelInfo.name);
					if(channelInfo.subscriptions == null) {
						channelInfo.subscriptions = new ArrayList<>();
					}
				} 
			}
			
			return res;
		}  
		 
		public Protocol.MqInfo info(String mq, String channel) {
			MessageQueue q = mqManager.get(mq);
			if(q == null) {
				return null;
			}
			Protocol.MqInfo res = q.info();
			for(Protocol.ChannelInfo channelInfo : res.channelList) {
				channelInfo.subscriptions = subscriptionManager.getSubscriptionList(mq, channel);
				if(channelInfo.subscriptions == null) {
					channelInfo.subscriptions = new ArrayList<>();
				}
			} 
			return res;  
		}  
	} 
}

