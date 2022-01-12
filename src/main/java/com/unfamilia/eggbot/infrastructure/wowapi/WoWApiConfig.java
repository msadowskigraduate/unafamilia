package com.unfamilia.eggbot.infrastructure.wowapi;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "application.wow-api")
interface WoWApiConfig {
    @WithName("client-id")
    String clientId();

    @WithName("client-secret")
    String clientSecret();
}
