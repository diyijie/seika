package io.seika.mq.plugin;

import io.seika.transport.Session;

public class DefaultIpFilter implements IpFilter {

	@Override
	public boolean doFilter(Session sess) { 
		return true;
	}

}
