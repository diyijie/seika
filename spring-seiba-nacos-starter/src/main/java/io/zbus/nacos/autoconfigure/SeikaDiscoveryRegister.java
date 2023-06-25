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
package io.zbus.nacos.autoconfigure;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.utils.StringUtils;
import io.zbus.config.SeikaProperties;
import io.zbus.kit.StrKit;
import io.zbus.mq.MqServer;
import io.zbus.mq.MqServerConfig;
import io.zbus.nacos.SeikaServiceApi;
import io.zbus.nacos.properties.Register;
import io.zbus.rpc.RpcFilter;
import io.zbus.rpc.RpcProcessor;
import io.zbus.rpc.RpcServer;
import io.zbus.rpc.annotation.FilterDef;
import io.zbus.rpc.annotation.FilterType;
import io.zbus.rpc.annotation.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Map;


@Component
public class SeikaDiscoveryRegister
        implements ApplicationListener<WebServerInitializedEvent>{

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

            Route anno = service.getClass().getAnnotation(Route.class);
            if(anno == null) continue;
            Type[] types = service.getClass().getGenericInterfaces() ;
            for (Type typ:types
                 ) {
                if (typ instanceof Class){
                    SeikaServiceApi api= (SeikaServiceApi) ((Class) typ).getAnnotation(SeikaServiceApi.class);
                    if(api==null){
                        continue;
                    }
                    p.mount( "/"+((Class<?>) typ).getCanonicalName(), service);
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
    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        context=event.getApplicationContext();
        Register register = new Register(discoveryProperties,applicationName);


       boolean did=rpcStart();
        try {
       //   namingService = NamingFactory.createNamingService("127.0.0.1:8848");

       // register.getMetadata().put("preserved.register.source", "SPRING_BOOT");
            NacosConfigManager configManager = (context).getBean(NacosConfigManager.class);
            namingService = NacosFactory.createNamingService(configManager.getNacosConfigProperties().getServerAddr());

            if (did && namingService!=null){
            namingService.registerInstance(register.getServiceName(), register.getGroupName(),
                    register);
            logger.info("Finished auto register service : {}, ip : {}, port : {}",
                    register.getServiceName(), register.getIp(), register.getPort());
            }

        } catch (NacosException e) {
             throw new RuntimeException(e);
        }

        //TODO 监听服务 扫描到的客户端接口
//        Instance selectOneHealthyInstance(String serviceName) throws NacosException;
//
//        namingService.getSubscribeServices().

    }


}
