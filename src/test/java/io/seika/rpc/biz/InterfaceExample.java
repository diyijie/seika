package io.seika.rpc.biz;

import java.util.List;
import java.util.Map;

import io.seika.rpc.biz.model.MyEnum;
import io.seika.rpc.biz.model.Order;
import io.seika.rpc.biz.model.User;
import io.seika.transport.Message;

public interface InterfaceExample{
  
	int getUserScore();
	
	String echo(String string);
	
	String getString(String name);  
	
	String[] stringArray();
	
	byte[] getBin();
	
	int plus(int a, int b);
	
	MyEnum myEnum(MyEnum e);
	
	User getUser(String name);
	
	Order getOrder();
	
	User[] getUsers();
	
	List<User> listUsers();
	
	Object[] objectArray(String id);

	int saveObjectArray(Object[] array);
	
	int saveUserArray(User[] array);
	
	int saveUserList(List<User> array);
	

	Map<String, Object> map(int value1);
	
	List<Map<String, Object>> listMap();
	
	String testEncoding();
	
	Class<?> classTest(Class<?> inClass);
	
	void testTimeout();
	
	void noReturn();
	
	void throwNullPointerException();
	
	void throwUserException() throws UserException;
	
	void throwException();
	
	void throwUnkownException();
	
	String nullParam(String nullStr);  
	
	Message html();
	
	Object callJs();
}