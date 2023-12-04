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

import io.seika.SeikaMq;
import io.seika.config.AutoConfiguration;
import io.seika.config.SeikaProperties;
import io.zeika.nacos.springcloud.SeikaRpcClient;
import io.zeika.nacos.springcloud.ServiceRegister;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Configuration
public class SeikaDiscoveryAutoConfiguration implements ApplicationListener<EnvironmentChangeEvent> {



	@Bean
	public SeikaDiscoveryRegister seikaDiscoveryRegister() {
		return new SeikaDiscoveryRegister();
	}
	@Bean
 	public SeikaRpcClient seikaRpcClient() {
		return new SeikaRpcClient();
	}
//	@Bean
//	public ServiceDefiner definer(){
//		return new ServiceDefiner();
//	}
		@Bean
	public ServiceRegister definer(){
		return new ServiceRegister();
	}

	@Override
	public void onApplicationEvent(EnvironmentChangeEvent environmentChangeEvent) {
		boolean returnf = true;
		for (String key : environmentChangeEvent.getKeys()) {
			if (key.equals("seika.address")){
				returnf = false;
				break;
			}
		}
		if (returnf){
			return;
		}
 		ApplicationContext ct = ((ApplicationContext) environmentChangeEvent.getSource());
		DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry)ct.getAutowireCapableBeanFactory();
		SeikaMq old= ct.getBean(SeikaMq.class);
		if (old==null){
			return;
		}
		old.des();
		Map<String, SeikaMq> maps = ct.getBeansOfType(SeikaMq.class);
		String beanname = maps.keySet().stream().findAny().get();

		registry.destroySingleton(beanname);

		SeikaProperties pro=ct.getBean(SeikaProperties.class);
		pro.setAddress(ct.getEnvironment().getProperty("seika.address"));
		SeikaMq newsingleton = (new AutoConfiguration()).zbusSeikaClient(pro);
		registry.registerSingleton(beanname,newsingleton);
 	}
}
