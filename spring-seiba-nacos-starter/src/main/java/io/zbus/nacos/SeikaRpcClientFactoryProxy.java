package io.zbus.nacos;

import com.alibaba.nacos.api.naming.NamingService;
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
//        Instance instace=  namingService.selectOneHealthyInstance(Register.WrapServiceName(api.value()));
//
//        String address = instace.getIp()+":"+instace.getPort();
        String address = "127.0.0.1:15555";
        return seikaRpcClient.Get(address,innerClass);
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
