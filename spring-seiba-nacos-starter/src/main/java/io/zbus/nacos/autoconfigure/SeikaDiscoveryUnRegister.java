
package io.zbus.nacos.autoconfigure;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import io.zbus.config.SeikaProperties;
import io.zbus.nacos.properties.Register;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;


/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.2.3
 */
@Component
public class SeikaDiscoveryUnRegister
        implements  ApplicationListener<ContextClosedEvent> {

    private static final Logger logger = LoggerFactory
            .getLogger(SeikaDiscoveryUnRegister.class);

    @NacosInjected
    private NamingService namingService;

    @Autowired
    private SeikaProperties discoveryProperties;

	@Value("${spring.application.name:}")
	private String applicationName;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        Register register = new Register(discoveryProperties,applicationName);

        if (register.getPort() == 0) {
            register.setPort(15555);
        }

        // register.getMetadata().put("preserved.register.source", "SPRING_BOOT");

        try {
            if (register.getPort()!=0){
                namingService.deregisterInstance(register.getServiceName(), register.getGroupName(),
                        register);
                logger.info("Finished auto deregister service : {}, ip : {}, port : {}",
                        register.getServiceName(), register.getIp(), register.getPort());
            }
        }
        catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}
