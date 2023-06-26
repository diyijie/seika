package io.seika.mq.commands;

import java.io.IOException;

import io.seika.transport.Message;
import io.seika.transport.Session;

public interface CommandHandler{
	void handle(Message msg, Session sess) throws IOException;
}