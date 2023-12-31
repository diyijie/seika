package io.seika.transport;

import io.seika.auth.RequestSign;
import io.seika.transport.http.WebsocketClient;
import io.seika.transport.inproc.InprocClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * 
 * Decoration pattern on AbastractClient, making Client's sub class type adaptive to all real clients such as
 * WebsocketClient, InprocClient
 * 
 * @author leiming.hong Jun 27, 2018
 *
 */
public class Client extends AbastractClient {
	private AtomicBoolean connected = new AtomicBoolean(false);
	protected AbastractClient support;
	
	public Client(String address) {
		support = new WebsocketClient(address);
	}

	public Client(IoAdaptor ioAdaptor) {
		support = new InprocClient(ioAdaptor);
	}

	public boolean connected() {
		return connected.get();
	}

	@Override
	protected void sendMessage0(Message data) { 
		support.sendMessage0(data);
	}
	
	public void sendMessage(Message data) {
		support.sendMessage(data);
	}

	public void connect() {
		support.connect();
	}

	public synchronized void heartbeat(long interval, TimeUnit timeUnit, AbastractClient.MessageBuilder builder) {
		support.heartbeat(interval, timeUnit, builder);
	}

	@Override
	public void close() throws IOException {
		support.close();
		connected.set(false);
	}

	public void invoke(Message req, DataHandler<Message> dataHandler) {
		support.invoke(req, dataHandler);
	}

	public void invoke(Message req, DataHandler<Message> dataHandler,
			ErrorHandler errorHandler) {
		support.invoke(req, dataHandler, errorHandler);
	}

	public Message invoke(Message req) throws IOException, InterruptedException {
		return support.invoke(req);
	}

	public Message invoke(Message req, long timeout, TimeUnit timeUnit)
			throws IOException, InterruptedException {
		return support.invoke(req, timeout, timeUnit);
	}

	public boolean handleInvokeResponse(Message response) throws Exception {
		return support.handleInvokeResponse(response);
	};

	public void setApiKey(String apiKey) {
		support.setApiKey(apiKey);
	}

	public void setSecretKey(String secretKey) {
		support.setSecretKey(secretKey);
	}

	public void setAuthEnabled(boolean authEnabled) {
		support.setAuthEnabled(authEnabled);
	}

	public void setRequestSign(RequestSign requestSign) {
		support.setRequestSign(requestSign);
	}

	public void onMessage(DataHandler<Message> onMessage) {
		support.onMessage(onMessage);
	}

	public void onClose(EventHandler onClose) {
		support.onClose(new connected(onClose, unused -> {
			connected.set(false);
			return null ;
		}));
	}

	public void onOpen(EventHandler onOpen) {
		support.onOpen(new connected(onOpen, unused -> {
			connected.set(true);
			return null ;
		}));
	}

	public void onError(ErrorHandler onError) {
		support.onError(onError);
	}

	public void setReconnectDelay(int reconnectDelay) {
		support.setReconnectDelay(reconnectDelay);
	}
	
	@Override
	public void setAfterReceived(MessageInterceptor afterReceived) { 
		support.setAfterReceived(afterReceived);
	}
	
	@Override
	public void setBeforeSend(MessageInterceptor beforeSend) { 
		support.setBeforeSend(beforeSend);
	}
}
class connected implements  EventHandler{
	private final Function<Void, Void> fn;
	private  EventHandler eventHandler ;

	public connected(EventHandler eventHandler, Function<Void,Void> fn) {
		this.eventHandler = eventHandler;
		this.fn = fn ;
	}

	@Override
public void handle() throws Exception {
			if (this.fn!=null){
				this.fn.apply(null);

			}
			if (this.eventHandler!=null){
				this.eventHandler.handle();
			}
		}
		}