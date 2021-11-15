package com.unfamilia.application.discord;

import org.eclipse.microprofile.config.ConfigProvider;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class DiscordBotConfigurationProvider {

    public String getToken() {
        return get(DiscordConfigurationKeys.DISCORD_API_TOKEN);
    }

    public String getForKey(String key) {
        return get(key);
    }

    private String get(String key) {
        Optional<String> discordApiToken = ConfigProvider
                .getConfig()
                .getOptionalValue(key, String.class);

        return discordApiToken.orElseThrow(MissingDiscordApiTokenException::new);
    }
}
