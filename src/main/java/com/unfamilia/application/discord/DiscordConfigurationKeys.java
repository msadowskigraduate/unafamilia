package com.unfamilia.application.discord;

public interface DiscordConfigurationKeys {
    String BASE_DISCORD_CONFIGURATION_KEY = "application.discord";
    String DISCORD_API_TOKEN = BASE_DISCORD_CONFIGURATION_KEY + "." + "token";
    String INIT_CHANNEL_CONFIGURATION_KEY = BASE_DISCORD_CONFIGURATION_KEY + "." + "raidpackage.channels.initializeOrder";
    String CONFIRMED_CHANNEL_CONFIGURATION_KEY = BASE_DISCORD_CONFIGURATION_KEY + "." + "raidpackage.channels.confirmOrder";
}
