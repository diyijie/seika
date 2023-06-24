package io.zbus.config;

import io.zbus.ZbusSeikaMq;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;


@Configuration
@Import(ZbusConfig.class)
public class AutoConfiguration implements EnvironmentAware{

    private Environment environment;

    public void setEnvironment(Environment environment) {
        this.environment = environment;
     }


    @Bean()
    @ConditionalOnProperty(  name = SeikaProperties.ENABLED,havingValue = "true" )
    public ZbusSeikaMq zbusSeikaClient(SeikaProperties prop )   {
        if (prop.getAddress()!=null && !prop.getAddress().equals("")){
          return new ZbusSeikaMq(prop.getAddress(), prop.getApiKey(), prop.getSecretKey());
        }
        return null ;
    }
}
