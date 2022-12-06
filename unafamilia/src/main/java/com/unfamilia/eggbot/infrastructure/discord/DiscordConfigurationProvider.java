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
        return UriBuilder.fromPath("https://discord.com/oauth2/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", configProvider.discordProvider().clientId())
                .queryParam("scope", "identify")
                .queryParam("redirect_uri", "http://localhost:9000/discord/oauth2/authorize")
                .queryParam("prompt", "consent")
                .toTemplate();
        // return
        // "https://discord.com/oauth2/authorize?response_type=code&client_id=902943569298489364&scope=identify&redirect_uri=http%3A%2F%2Flocalhost%3A9000%2Fdiscord%2Foauth2%2Fauthorize&prompt=consent";
    }

    public MultivaluedMap<String, String> provideAuthenticationForm(String code) {
        Form tokenExchangeRequest = new Form()
                .param("client_id", configProvider.discordProvider().clientId())
                .param("client_secret", configProvider.discordProvider().clientSecret())
                .param("code", code)
                .param("grant_type", "authorization_code")
                .param("redirect_uri", "http://localhost:9000/discord/oauth2/authorize");

        return tokenExchangeRequest.asMap();
    }
}
