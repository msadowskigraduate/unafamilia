package com.unfamilia.application;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "application")
public interface ApplicationConfigProvider {
    @WithName("hostname")
    String hostname();

    @WithName("wow-api")
    WoWApiConfig wowApi();

    @WithName("discord")
    DiscordConfig discord();

    interface WoWApiConfig {
        @WithName("client-id")
        String clientId();

        @WithName("client-secret")
        String clientSecret();
    }

    interface DiscordConfig {
        String token();
        Raidpackage raidpackage();

        interface Raidpackage {
            @WithName("channels.initializeOrder")
            String initializeOrder();

            @WithName("channels.confirmOrder")
            String confirmOrder();
        }
    }
}
