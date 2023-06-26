package io.seika.rpc.biz.inheritance;

import io.seika.rpc.annotation.Route;

@Route
public class SubService2 extends BaseServiceImpl<String> implements SubServiceInterface2 {
	@Override
	public boolean save(String t) {
		System.out.println("override! " + t);
		return true;
	}
}
