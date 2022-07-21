package com.unfamilia.application;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "application")
public interface ApplicationConfigProvider {
    @WithName("hostname")
    String hostname();

    @WithName("wow")
    WoWApiConfig wowApi();

    interface WoWApiConfig {
        @WithName("id")
        String clientId();

        @WithName("secret")
        String clientSecret();
    }
}
