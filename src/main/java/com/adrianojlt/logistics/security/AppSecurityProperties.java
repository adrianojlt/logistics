package com.adrianojlt.logistics.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {

    private boolean enableLogin;

    public boolean isEnableLogin() {
        return enableLogin;
    }

    public void setEnableLogin(boolean enableLogin) {
        this.enableLogin = enableLogin;
    }
}
