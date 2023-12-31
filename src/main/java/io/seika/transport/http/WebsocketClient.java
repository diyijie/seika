package io.seika.transport.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.CharsetUtil;
import io.seika.kit.JsonKit;
import io.seika.transport.AbastractClient;
import io.seika.transport.DataHandler;
import io.seika.transport.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Client of Websocket, via Netty.
 * 
 * @author leiming.hong Nov 12, 2018
 *
 */
public class WebsocketClient extends AbastractClient {
	private static final Logger logger = LoggerFactory.getLogger(WebsocketClient.class);   
	
	public static final int MAX_WSFRAME_LENGTH = 1024*1024*1024;
	
	public DataHandler<String> onText;
	public DataHandler<ByteBuffer> onBinary; 
	
	public long lastActiveTime = System.currentTimeMillis(); 
	
	private List<String> cachedSendingMessages = new ArrayList<String>();   
	  
	private SslContext sslCtx;
	private final String host;
	private final int port;
	private final URI uri;
	private String address; 
	
	
	private Channel channel;
	private ChannelFuture channelFuture;
	private Object connectLock = new Object();  
	private EventLoopGroup group;
	private boolean ownGroup = false;
	

	public WebsocketClient(String address) { 
		this(address, null);
	}
	
	public WebsocketClient(String address, EventLoopGroup group) {   
		super();
		if(group == null) {
			group = new NioEventLoopGroup();
			ownGroup = true;
		}
		this.group = group;
		 
		if(!address.startsWith("ws://") && !address.startsWith("wss://")) {
			address = "ws://" + address;
		}
		this.address = address; 
		 
		try {
			uri = new URI(this.address);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
        String scheme = uri.getScheme() == null? "ws" : uri.getScheme();
        host = uri.getHost() == null? "localhost" : uri.getHost(); 
        if (uri.getPort() == -1) {
            if ("ws".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("wss".equalsIgnoreCase(scheme)) {
                port = 443;
            } else {
                port = -1;
            }
        } else {
            port = uri.getPort();
        }

        if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
        	throw new IllegalArgumentException("Only WS(S) is supported.");  
        }

        final boolean ssl = "wss".equalsIgnoreCase(scheme); 
        if (ssl) {
            try {
				sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
			} catch (SSLException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
        }  
		
		
		onText = msg-> {  
			Message response = JsonKit.parseObject(msg, Message.class);
			if(onMessage != null) {
				onMessage.handle(response);
			} 
		};  
		
		onClose = ()-> {
			synchronized (connectLock) {
				if(channel != null){ 
					channel.close();
					channel = null;
					channelFuture = null;
				}
			}; 
			
			try {
				Thread.sleep(reconnectDelay);
			} catch (InterruptedException e) {
				// ignore
			}
			logger.info("Trying to reconnect " + WebsocketClient.this.address);
			connect();
		};
		
		onError = e -> {;
			if(onClose != null){
				try {
					onClose.handle();
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		};
	} 
	
	@Override
	protected void sendMessage0(Message data) {
		sendMessage(JsonKit.toJSONString(data));
	}  
	
	@Override
	public void close() throws IOException { 
		super.close();
		
		synchronized (this.connectLock) {
			if(this.channel != null){
				this.channel.close();  
				this.channel = null;
				this.channelFuture = null;
			} 
		} 
		
		if(ownGroup && this.group != null) {
			this.group.shutdownGracefully();
			this.group = null;
		}
	} 
	
	public void sendMessage(String command){
		synchronized (connectLock) {
			if(this.channel == null || !this.channel.isOpen()){
				this.cachedSendingMessages.add(command);
				this.connect();
				return;
			} 
			ByteBuf buf = Unpooled.wrappedBuffer(command.getBytes());
			WebSocketFrame frame = new TextWebSocketFrame(buf);
			this.channel.writeAndFlush(frame);
		}  
	}  
	
	public synchronized void connect(){  
		if(this.channelFuture != null && this.channelFuture.channel().isActive()) return; //on the way
		connectUnsafe();
	}
	
	protected void connectUnsafe(){   
		lastActiveTime = System.currentTimeMillis(); 
 
		final WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders(), MAX_WSFRAME_LENGTH);
		
		final SimpleChannelInboundHandler<Object> handler = new SimpleChannelInboundHandler<Object>() {  
		    private ChannelPromise handshakeFuture;  
		    @Override
		    public void handlerAdded(ChannelHandlerContext ctx) {
		        handshakeFuture = ctx.newPromise();
		    }

		    @Override
		    public void channelActive(ChannelHandlerContext ctx) {
		        handshaker.handshake(ctx.channel());
		    }

		    @Override
		    public void channelInactive(ChannelHandlerContext ctx) { 
		        String msg = String.format("Websocket(%s) closed", address);
				logger.info(msg);
				if(onClose != null){
					try {
						onClose.handle();
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
		    }

		    @Override
		    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		        Channel ch = ctx.channel();
		        if (!handshaker.isHandshakeComplete()) {
		            try {
		                handshaker.finishHandshake(ch, (FullHttpResponse) msg);  
		                handshakeFuture.setSuccess();
		                
		                String info = String.format("Websocket(%s) connected", address);
						logger.info(info); 
						
						if(cachedSendingMessages.size()>0){
							for(String json : cachedSendingMessages){
								sendMessage(json);
							}
							cachedSendingMessages.clear();
						} 
						if(onOpen != null){
							runner.submit(()->{
								try {
									onOpen.handle();
								} catch (Exception e) {
									logger.error(e.getMessage(), e);
								}
							});
						}
		                
		            } catch (WebSocketHandshakeException e) {
		                logger.error("WebSocket Client failed to connect", e);
		                handshakeFuture.setFailure(e);
		            }
		            return;
		        }

		        if (msg instanceof FullHttpResponse) {
		            FullHttpResponse response = (FullHttpResponse) msg;
		            throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status() +
		                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
		        }

		        WebSocketFrame frame = (WebSocketFrame) msg;
		        if (frame instanceof TextWebSocketFrame) {
		            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame; 
		            lastActiveTime = System.currentTimeMillis(); 
					try {
						if(onText != null){ 
							onText.handle(textFrame.text());
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					} 
		        } else if (frame instanceof PongWebSocketFrame) {
		            logger.info("WebSocket Client received pong");
		        } else if(frame instanceof PingWebSocketFrame) {
		        	ctx.write(new PongWebSocketFrame(frame.content().retain())); 
		        } else if (frame instanceof CloseWebSocketFrame) {
		            logger.info("WebSocket Client received closing");
		            ch.close(); 
		            
		            if(onClose != null){
						try {
							onClose.handle();
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
		        } 
		    }

		    @Override
		    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { 
		        if (!handshakeFuture.isDone()) {
		            handshakeFuture.setFailure(cause);
		        }
		        
		        String error = String.format("Websocket(%s) error: %s", address, cause.getMessage());
				logger.error(error, cause); 
				if(onError != null){
					onError.handle(cause);
				} 
		        ctx.close();
		    }
		};
		 

        Bootstrap b = new Bootstrap();
        b.group(group)
         .channel(NioSocketChannel.class)
         .handler(new ChannelInitializer<SocketChannel>() {
             @Override
             protected void initChannel(SocketChannel ch) {
                 ChannelPipeline p = ch.pipeline();
                 if (sslCtx != null) {
                     p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                 }
                 p.addLast(
                         new HttpClientCodec(),
                         new HttpObjectAggregator(MAX_WSFRAME_LENGTH), 
                         handler);
             }
         });

        this.channelFuture = b.connect(uri.getHost(), port);   
        this.channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                	channel = future.channel();
                } else {
                    // Close the connection if the connection attempt has failed.
                	channelFuture = null;
                	String error = String.format("Websocket(%s) connection error: %s", address, future.cause());
    				logger.error(error); 
    				if(onError != null){
    					onError.handle(future.cause());
    				} 
                }
            }
        });
	}  
}
