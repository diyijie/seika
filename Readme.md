### fork from zbus ,

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
过滤器的定义  分前置 后置 exception 普通
@Filterdef{

}

zbus:
    seika:
        apiKey
        secretKey

        enabled: true //use mq 
        address: 127.0.0.1:15555
        
        rpcPort //如果提供了相关服务需要填写端口 默认15555 

        