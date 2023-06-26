package io.seika.rpc.biz.generic;

import io.seika.rpc.annotation.Route;

@Route
public class GenericMethodImpl implements GenericMethod{

	@Override
	public <T> void test(T t) { 
		System.out.println(t);
	}

}
