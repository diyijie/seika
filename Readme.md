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

//use  
@Resource
ZbusSeikaMq client ;

RPC 实现

@Route(exclude=true) //禁止 移除 不会注册到
class A impl xx{
    @Route("xx) //指定路径信息 不必要 seika会统一分装
    @Filter("xx) 执行该方法前的普通过滤器
    method（）{}
}
接口上面要写注解 SeikaServiceApi
@SeikaServiceApi("user") user是要调用的项目名称

过滤器的定义  分前置 后置 exception 普通
@Filterdef{

}
```
           <dependency>
                    <groupId>io.seika</groupId>
                    <artifactId>spring-seika-nacos-starter</artifactId>
                    <version>1.0.0</version>
                </dependency>
                
                
```
zbus:
    seika:
    #    enabled: true //启用消息队列
    #    address:
        rpcPort: 25555 //启用rpc时候的端口
    #    apiKey:
    #    secretKey:
zbus:
    seika:
        apiKey
        secretKey
        enabled: true //use mq 
        address: 127.0.0.1:15555
        
        rpcPort //如果提供了相关服务需要填写端口 默认15555 
        http://127.0.0.1:15555/doc  所实现该服务的进程
        