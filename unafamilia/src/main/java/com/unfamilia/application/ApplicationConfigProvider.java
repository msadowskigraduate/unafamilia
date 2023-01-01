package com.unfamilia.application;

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

    @WithName("wcl")
    WarcraftLogsConfigurationProvider wclProvider();

    interface DiscordConfigurationProvider {
        @WithName("id")
        String clientId();

        @WithName("secret")
        String clientSecret();
    }

    interface WarcraftLogsConfigurationProvider {
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
