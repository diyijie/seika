### fork from zbus ,

###
单次消息读取 只可能有一个消费者读取到消息 不可能两个读取到同一个消息 
* 是否能收到之前没有监听到的数据 取决于第一次创建mq的类型 DISK可以


[ ] 集成spring 
@Resource
ZbusSeikaClient client ;


zbus:
    seika:
        enabled: true
        address: 127.0.0.1:15555
        apiKey
        secretKey