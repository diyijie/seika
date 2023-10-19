package io.zeika.nacos.springcloud;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.util.Map;

public class ServiceRegister implements BeanFactoryPostProcessor {
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinitionRegistry registry=(BeanDefinitionRegistry)beanFactory;
      String bp ="";
        Map<String, Object> clis =   beanFactory.getBeansWithAnnotation(RegSeikaApi.class);

        for (Map.Entry<String,Object> b :clis.entrySet()) {
            RegSeikaApi regClz = null ;
            try{
                 regClz = b.getValue().getClass().getAnnotation(RegSeikaApi.class);
           }catch (Exception e){

           }
             if (regClz==null){
                 continue;
             }
            for(Class  beanDefinition:regClz.Value()){
                String clzstr = beanDefinition.getName();
                GenericBeanDefinition definition=(GenericBeanDefinition) BeanDefinitionBuilder.genericBeanDefinition(clzstr).getBeanDefinition();
                try{
                    definition.getPropertyValues().addPropertyValue("innerClass",Class.forName(clzstr));

                }catch (ClassNotFoundException e){
                    throw new RuntimeException(e);
                }
                definition.getPropertyValues().addPropertyValue("factory",beanFactory);

                definition.setBeanClass(SeikaRpcClientFactoryProxy.class);
                registry.registerBeanDefinition(clzstr, definition);
            }
        }

    }

}
