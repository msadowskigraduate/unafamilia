package com.unfamilia.eggbot.infrastructure.discord;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import com.unfamilia.application.ApplicationConfigProvider;

@ApplicationScoped
public class DiscordConfigurationProvider {
    @Inject
    ApplicationConfigProvider configProvider;

    public String provideAuthenticationUrl() {
        return UriBuilder.fromPath(this.provideBaseDiscordUrl())
                .queryParam("response_type", "code")
                .queryParam("client_id", configProvider.discordProvider().clientId())
                .queryParam("scope", "identify")
                .queryParam("redirect_uri", this.provideRedirectUri())
                .queryParam("prompt", "consent")
                .toTemplate();
    }

    public String provideDiscordClientId() {
        return configProvider.discordProvider().clientId();
    }

    public String provideRedirectUri() {
        return configProvider.hostname() + "/discord/oauth2/authorize";
    }

    public MultivaluedMap<String, String> provideAuthenticationForm(String code) {
        Form tokenExchangeRequest = new Form()
                .param("client_id", configProvider.discordProvider().clientId())
                .param("client_secret", configProvider.discordProvider().clientSecret())
                .param("code", code)
                .param("grant_type", "authorization_code")
                .param("redirect_uri",  provideRedirectUri());

        return tokenExchangeRequest.asMap();
    }

    public String provideBaseDiscordUrl() {
        return "https://discord.com/oauth2/authorize";
    }
}
