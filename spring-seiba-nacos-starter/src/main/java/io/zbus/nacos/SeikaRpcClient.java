package io.zbus.nacos;

import com.alibaba.nacos.api.utils.StringUtils;
import io.zbus.config.SeikaProperties;
import io.zbus.rpc.RpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
@Component
public class SeikaRpcClient {
    @Autowired
    private SeikaProperties properties;
    private static ConcurrentHashMap<String,Object> rpcs= new ConcurrentHashMap<>();
    public <T> T Get(String s,Class<T> clazz) {
       // String s="localhost:15555";

        String path = "/" + clazz.getCanonicalName() ;
        String cacheKey = s+path;
        if (!rpcs.containsKey(cacheKey)){
            RpcClient  rpc = new RpcClient(s);

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
}
