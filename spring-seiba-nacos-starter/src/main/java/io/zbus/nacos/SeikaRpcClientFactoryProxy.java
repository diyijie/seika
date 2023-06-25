package io.zbus.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.zbus.nacos.properties.Register;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class  SeikaRpcClientFactoryProxy<T> implements FactoryBean<T> {
     private BeanDefinitionRegistry factory;
   private NamingService namingService;

    private  SeikaRpcClient seikaRpcClient ;
    private Class<T>  innerClass;

    public BeanDefinitionRegistry getFactory() {
        return factory;
    }

    public void setFactory(BeanDefinitionRegistry factory) {
        this.factory = factory;
    }

    public Class<?> getInnerClass() {
        return innerClass;
    }

    public void setInnerClass(Class<T> innerClass) {
        this.innerClass = innerClass;
    }

    @Override
    public T getObject() throws Exception {
        SeikaServiceApi api=   (innerClass).getAnnotation(SeikaServiceApi.class);
        if(api==null){
             throw new RuntimeException("interface error");
        }

        if (seikaRpcClient==null){
            seikaRpcClient = ((DefaultListableBeanFactory) factory).getBean(SeikaRpcClient.class);
        }
        if (namingService ==null ){
            NacosConfigManager configManager = ((DefaultListableBeanFactory) factory).getBean(NacosConfigManager.class);
            namingService = NacosFactory.createNamingService(configManager.getNacosConfigProperties().getServerAddr());
        }
        try{
           Instance instHeal = namingService.selectOneHealthyInstance(Register.WrapServiceName(api.value()));
           String address = instHeal.getIp()+":"+instHeal.getPort();
            return seikaRpcClient.Get(address,innerClass);
           }catch (NacosException | RuntimeException e){
               return seikaRpcClient.Get(namingService,Register.WrapServiceName(api.value()),innerClass);
            }
    }

    @Override
    public Class<T> getObjectType() {
        return innerClass ;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
