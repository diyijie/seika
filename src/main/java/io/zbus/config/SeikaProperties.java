package io.zbus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by lijie on 9/7/16.
 */
@ConfigurationProperties(prefix = "zbus.seika")
public class SeikaProperties {
    private String address="";
    private String  apiKey ;
    private String  secretKey ;
    private boolean  enabled ;


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
