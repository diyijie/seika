package io.zeika.nacos.springcloud;

import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.utils.StringUtils;
import io.seika.config.SeikaProperties;
import io.seika.rpc.RpcClient;
import io.seika.rpc.RpcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class SeikaRpcClient implements InvocationHandler {
    private NamingService namingService;
    private String serviceName ;
    private  Class<?> clz;
////


    @Autowired
    private SeikaProperties properties;
    private static ConcurrentHashMap<String,Object> rpcs= new ConcurrentHashMap<>();
    public <T> T Get(NamingService namingService,String s,Class<T> clazz) {
        // String s="localhost:15555";
        //延迟调用 先获取代理
            try {
                SeikaRpcClient sei = new SeikaRpcClient();
                sei.properties = this.properties;
                sei.namingService = namingService ;
                sei.serviceName = s ;
                sei.clz = clazz;
                Class<T>[] interfaces = new Class[] { clazz };
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                Object v = Proxy.newProxyInstance(classLoader, interfaces, sei);
                return (T)v;
            } catch (Exception e) {
                throw new RpcException(e);
            }
    }
    public <T> T Get(String address,Class<T> clazz) {
        String path = "/" + clazz.getCanonicalName() ;
        String cacheKey = address+path;
        if (!rpcs.containsKey(cacheKey)){
            RpcClient  rpc = new RpcClient(address);

            if (properties!=null){
                if (!StringUtils.isBlank(properties.getApiKey()) && !StringUtils.isBlank(properties.getSecretKey()) ){
                    rpc.setApiKey(properties.getApiKey());
                    rpc.setSecretKey(properties.getSecretKey());
                    rpc.setAuthEnabled(true);
                }
            }

            T cli = rpc.createProxy(path, clazz);
            rpcs.put(cacheKey,cli);
            return (T)cli ;
        }else{
            return (T) rpcs.get(cacheKey);
        }
     }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        Instance instHeal = this.namingService.selectOneHealthyInstance(this.serviceName);
        String address = instHeal.getIp() + ":" + instHeal.getPort();

        Object re = this.Get(address, this.clz);
        InvocationHandler prx = Proxy.getInvocationHandler(re);
       return prx.invoke(re,method,objects);
    }
}
