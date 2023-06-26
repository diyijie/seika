package io.seika.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by lijie on 9/7/16.
 */
@ConfigurationProperties(prefix = SeikaProperties.SEIKA_PREFIX)
public class SeikaProperties {
    final static String SEIKA_PREFIX= "zbus.seika";
    public final static  String ENABLED = SeikaProperties.SEIKA_PREFIX + ".enabled";
    private String address;
    private int rpcPort;
    private String  apiKey ;
    private String  secretKey ;
    private boolean  enabled ;

    public int getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(int rpcPort) {
        this.rpcPort = rpcPort;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
