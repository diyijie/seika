package io.seika.mq.disk;

import java.io.File;

import io.seika.mq.Protocol;
import io.seika.mq.model.Channel;

public class DiskQueueTest {

	public static void main(String[] args) throws Exception { 
		File baseDir = new File("/tmp");
		DiskQueue q = new DiskQueue("/", baseDir);
		System.out.println(q.name());
		q = new DiskQueue("/abc/def/", baseDir);
		Channel channel = new Channel(q.name());
		q.saveChannel(channel);
		System.out.println(q.name());
		Protocol.ChannelInfo info = q.channel(q.name());
		System.out.println(info.name);
	}

}
