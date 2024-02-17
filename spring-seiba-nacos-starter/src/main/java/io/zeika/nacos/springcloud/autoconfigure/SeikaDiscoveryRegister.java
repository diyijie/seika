/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zeika.nacos.springcloud.autoconfigure;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.utils.StringUtils;
import io.seika.config.SeikaProperties;
import io.seika.kit.StrKit;
import io.seika.mq.MqServer;
import io.seika.mq.MqServerConfig;
import io.zeika.nacos.springcloud.SeikaServiceApi;
import io.zeika.nacos.springcloud.properties.Register;
import io.seika.rpc.RpcFilter;
import io.seika.rpc.RpcProcessor;
import io.seika.rpc.RpcServer;
import io.seika.rpc.annotation.FilterDef;
import io.seika.rpc.annotation.FilterType;
import io.seika.rpc.annotation.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Map;


@Component
public class SeikaDiscoveryRegister
        implements ApplicationListener<ApplicationStartedEvent>{ //WebServerInitializedEvent

    private static final Logger logger = LoggerFactory
            .getLogger(SeikaDiscoveryRegister.class);

   // @NacosInjected

    public NamingService namingService;

    @Autowired
    private SeikaProperties discoveryProperties;

	@Value("${spring.application.name:}")
	private String applicationName;

    @Value("${server.address:}")
    String severAdrr;//""

    private ApplicationContext context;
    private boolean rpcStart(){
        RpcProcessor p = new RpcProcessor();

        //build filer
        Map<String, Object> table = context.getBeansWithAnnotation(FilterDef.class);
        for(Map.Entry<String, Object> e : table.entrySet()) {
            Object service = e.getValue();
            if(!(service instanceof RpcFilter)) {
                continue; //ignore
            }
            RpcFilter filter = (RpcFilter) service;
            FilterDef anno = service.getClass().getAnnotation(FilterDef.class);
            if(anno == null) continue;
            String name = anno.name();
            if(StrKit.isEmpty(name)) name = anno.value();

            FilterType type = anno.type();
            if(type == FilterType.GlobalBefore) {
                p.setBeforeFilter(filter);
            } else if(type == FilterType.GlobalAfter) {
                p.setAfterFilter(filter);
            } else if(type == FilterType.Exception) {
                p.setExceptionFilter(filter);
            }

            if(!StrKit.isEmpty(name)) {
                p.getAnnotationFilterTable().put(name, filter);
            }
        }
        boolean have=false ;
        //mount service with Route annotation
        table = context.getBeansWithAnnotation(Route.class);
        for(Map.Entry<String, Object> e : table.entrySet()) {
            Object service = e.getValue();
            Class<?> clz = service.getClass();
            Route anno = clz.getAnnotation(Route.class);
            if(anno == null) {
                clz = clz.getSuperclass() ;
               anno= clz.getAnnotation(Route.class);
            };
            if(anno == null) {
                continue;
            };
            Type[] types = clz.getGenericInterfaces() ;
            for (Type typ:types
                 ) {
                if (typ instanceof Class){
                    SeikaServiceApi api= (SeikaServiceApi) ((Class) typ).getAnnotation(SeikaServiceApi.class);
                    if(api==null){
                        logger.warn(clz.getName()+"not impl interface of  @SeikaServiceApi");
                        continue;
                    }
                    p.mount( "/"+((Class<?>) typ).getCanonicalName(), service,clz);
                    have = true ;
                }

            }
        }
        if (!have){
            return false;
        }
         if (StringUtils.isBlank(severAdrr)){
            severAdrr= "0.0.0.0";
        }
         int port = 15555 ;
        if (discoveryProperties!=null ){
            if(discoveryProperties.getRpcPort() != 0) {
                port = discoveryProperties.getRpcPort();
            }
        }
        discoveryProperties.setRpcPort(port);
        MqServerConfig config = new MqServerConfig(severAdrr, port);
        config.setVerbose(false);

        RpcServer rpcServer = new RpcServer();
        rpcServer.setRpcProcessor(p);
        p.setDocFile("rpc.html");


        //rpcServer.setMqServerAddress("localhost:15555");
        rpcServer.setMq("/");
        rpcServer.setMqServer(new MqServer(config));

        if (discoveryProperties!=null && !StringUtils.isBlank(discoveryProperties.getApiKey()) && !StringUtils.isBlank(discoveryProperties.getSecretKey()) ){
            rpcServer.setApiKey(discoveryProperties.getApiKey());
            rpcServer.setSecretKey(discoveryProperties.getSecretKey());
            rpcServer.setAuthEnabled(true);
        }
        rpcServer.start();
        return true ;
    }
    //@Override
    public void onApplicationEvent(WebServerInitializedEvent event) {

    }


    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        context=applicationStartedEvent.getApplicationContext();
        boolean did=rpcStart();
        try {


            if (did  ){
                NacosConfigManager configManager = (context).getBean(NacosConfigManager.class);
                namingService = NacosFactory.createNamingService(configManager.getNacosConfigProperties().getServerAddr());
                Register register = new Register(discoveryProperties,applicationName);
                namingService.registerInstance(register.getServiceName(), register.getGroupName(),
                        register);
                logger.info("Finished auto SeiKa-rpc register  : {}, ip : {}, port : {}",
                        register.getServiceName(), register.getIp(), register.getPort());
            }

        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}
