//package io.zeika.nacos.springcloud;
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.beans.factory.support.BeanDefinitionBuilder;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.beans.factory.support.GenericBeanDefinition;
//import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
//import org.springframework.core.type.AnnotationMetadata;
//import org.springframework.core.type.classreading.MetadataReader;
//import org.springframework.core.type.classreading.MetadataReaderFactory;
//import org.springframework.core.type.filter.TypeFilter;
//
//import java.io.IOException;
//import java.util.Set;
//
//class scaner extends ClassPathBeanDefinitionScanner {
//    scaner( BeanDefinitionRegistry registery){
//        super(registery);
//        this.addIncludeFilter(new TypeFilter() {
//            @Override
//            public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
//                return true;
//            }
//        });
//    }
//
//    @Override
//    protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
//        return metadataReader.getClassMetadata().isInterface();
//    }
//
//    @Override
//    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
//        AnnotationMetadata mata=beanDefinition.getMetadata();
//        boolean is = mata.isInterface();
//        if(is){
//            return  mata.hasAnnotation(SeikaServiceApi.class.getCanonicalName());
//        }
//        return false ;
//    }
//}
//public class ServiceDefiner implements BeanFactoryPostProcessor {
//
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException{
//        BeanDefinitionRegistry registry=(BeanDefinitionRegistry)beanFactory;
//        //  main package
//      //  List<String> ps = AutoConfigurationPackages.get(beanFactory.getBean(ApplicationContext.class).getAutowireCapableBeanFactory());
//        String bp ="";
////        if( ps.size()>0 ){
////            String[] psarr = ps.get(0).split(".", 3);
////            if (psarr.length >=2) {
////                bp=psarr[0]+"."+psarr[1];
////            }
////        }
//
//        scaner scan=new scaner(registry);
//        Set<BeanDefinition> cpmps = scan.findCandidateComponents(bp);
//
//        for(BeanDefinition beanDefinition:cpmps){
//            String clzstr = beanDefinition.getBeanClassName();
//            GenericBeanDefinition definition=(GenericBeanDefinition) BeanDefinitionBuilder.genericBeanDefinition(clzstr).getBeanDefinition();
//            try{
//                definition.getPropertyValues().addPropertyValue("innerClass",Class.forName(clzstr));
//
//            }catch (ClassNotFoundException e){
//                throw new RuntimeException(e);
//            }
//             definition.getPropertyValues().addPropertyValue("factory",beanFactory);
//
//            definition.setBeanClass(SeikaRpcClientFactoryProxy.class);
//          //  definition.setDestroyMethodName("close");
//            registry.registerBeanDefinition(clzstr, definition);
//        }
//
//    }
//
//}
