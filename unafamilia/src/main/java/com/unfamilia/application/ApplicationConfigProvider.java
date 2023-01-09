package com.unfamilia.application;

import java.util.Map;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "application")
public interface ApplicationConfigProvider {
    @WithName("hostname")
    String hostname();

    @WithName("wow")
    WoWApiConfig wowApi();

    @WithName("discord")
    DiscordConfigurationProvider discordProvider();

    @WithName("services")
    Map<String, String> services();

    interface DiscordConfigurationProvider {
        @WithName("id")
        String clientId();

        @WithName("secret")
        String clientSecret();
    }

    interface WoWApiConfig {
        @WithName("id")
        String clientId();

        @WithName("secret")
        String clientSecret();
    }
}
