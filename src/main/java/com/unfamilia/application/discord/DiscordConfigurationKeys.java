package com.unfamilia.application.discord;

public interface DiscordConfigurationKeys {
    String SEPARATOR = ".";
    String BASE = "application";

    String BASE_DISCORD_CONFIGURATION_KEY = BASE + SEPARATOR + "discord";
    String DISCORD_API_TOKEN = BASE_DISCORD_CONFIGURATION_KEY + SEPARATOR + "token";

    String RAID_PACKAGE = BASE_DISCORD_CONFIGURATION_KEY + SEPARATOR + "raidpackage";
    String INIT_CHANNEL_CONFIGURATION_KEY = RAID_PACKAGE + SEPARATOR + "channels.initializeOrder";
    String CONFIRMED_CHANNEL_CONFIGURATION_KEY = RAID_PACKAGE + SEPARATOR + "channels.confirmOrder";

    String ORIGIN_CONFIGURATION_KEY = BASE_DISCORD_CONFIGURATION_KEY + SEPARATOR + "origin";
    String GUILD_CONFIGURATION_KEY = ORIGIN_CONFIGURATION_KEY + SEPARATOR + "guild";
    String ORIGIN_GUILD_ID_CONFIGURATION_KEY = GUILD_CONFIGURATION_KEY + SEPARATOR + "id";
}
