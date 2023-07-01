package io.seika;

import io.seika.mq.MqServer;

public class Zbus {
	public static void main(String[] args) {
	MqServer.main(args);


		//这是原来的流程 应该是发出去的时候就没有成功 没成功的按照元直接发出 然后收到解析失败后就没有set 这样是可以的 相当于没用jsonkit编码
		//用了jskit编码成功后 解密肯定是错误的
//		Object msg = "535553";
//		byte[] msgd = JsonKit.toJSONBytes(msg);
//		//这是一个bug 不懂原来的作者为啥转一次 为了jS解析？？？
//		byte[] body = Base64.getDecoder().decode(new String(msgd));
//		String afs = new String(JsonKit.toJSONBytes(body));
//		System.out.println("转换后内容不通过"+ msg+" >>>"+afs);

	}
}
