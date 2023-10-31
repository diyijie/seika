### fork from zbus ,
	//300000消息用时162秒 3000 channel
									//300000消息用时25秒 300 channel
									//300000消息用时29秒 30 channel

建议合理设计channel个数 不建议超过500。可以动态扩展服务集群。mq名称建议使用一个。
###
单次消息读取 只可能有一个消费者读取到消息 不可能两个读取到同一个消息 
* 是否能收到之前没有监听到的数据 取决于第一次创建mq的类型 DISK可以
* 集成spring mq 、rpc 
* 加入rpc的apikey校验

RPC 实现
过滤器的定义  分前置 后置 exception 普通
@Filterdef{ }
@Route(exclude=true) //禁止 移除 不会注册到
class A impl xx{
    //@Route("xx) //指定路径信息 不必要 seika会统一分装
    @Filter("xx) 执行该方法前的普通过滤器
    method（）{}
}
1.添加接口 接口上面要写注解 @SeikaServiceApi("user") user是要调用的项目名称
2.注册RegSeikaApi（{interface.class}）

```
           <dependency>
                    <groupId>io.seika</groupId>
                    <artifactId>spring-seika-nacos-starter</artifactId>
                    <version>1.2.0</version>
                </dependency>
                
                
```
    seika:
        apiKey
        secretKey
        enabled: true //use mq 
        address: 127.0.0.1:15555
        
        rpcPort //如果提供了相关服务需要填写端口 默认15555 
        http://127.0.0.1:15555/doc  所实现该服务的进程
        




``` code example 

seika:
  enabled: true
  address: 192.168.2.104:54322 //使用rpc上的mq
  rpcPort: 54322 //启动rpc相当于起了mq
  apiKey: xxxx
  secretKey: xxxx

拿到一个mQ操作对象

@Autowired
 private SeikaMq seikamq ;


//Mq的发送订阅 支持不同类型的javaClass
seikamq.Sub("mq", "g", (Consumer<String>) s -> System.out.println(s));
seikamq.Sub("mq", "g", (Consumer<A>) s -> System.out.println(s.getA()));
seikamq.Sub("mq", "g", new DataHandler<Message>() {
    //JsonKit.convert(message.getBody(),A.class)
    @Override
    public void handle(Message message) throws Exception {
         System.out.println(message.getContext().toString());
    }
});
seikamq.Pub("mq","gxxx","g",new DataHandler<Message>(){
    @Override
    public void handle(Message message) throws Exception {
      //  System.out.println(message);
    }
});

-------------------

//使用nacos 自动注册然后拿到rpc对象。
接口写法：
@SeikaServiceApi("vpnui")
public interface  XrayApi {
    public int a();
}
注册：
@RegSeikaApi({XrayApi.class})
class App{}


//手动拿到一个rpc接口实现
final XrayApi a = SeikaRpcClient.Of("192.168.2.104:54322", XrayApi.class,"xxxx","xxxx“);


————



//拿到原始对象 可以判断是否连接成功
RpcClient cn = SeikaRpcClient.OriginRpc("192.168.2.104:54322");
System.out.println(a.a()+1);
System.out.println("connected :"+cn.connected());



```