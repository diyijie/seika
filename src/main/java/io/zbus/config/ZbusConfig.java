package io.zbus.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({SeikaProperties.class})
@Configuration
public class ZbusConfig {
}
