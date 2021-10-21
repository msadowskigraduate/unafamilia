package com.unfamilia.application.discord;

import org.eclipse.microprofile.config.ConfigProvider;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class DiscordTokenProvider {

    public String getToken() {
        Optional<String> discordApiToken = ConfigProvider
                .getConfig()
                .getOptionalValue(DiscordConfigurationKeys.DISCORD_API_TOKEN, String.class);

        return discordApiToken.orElseThrow(MissingDiscordApiTokenException::new);
    }
}
