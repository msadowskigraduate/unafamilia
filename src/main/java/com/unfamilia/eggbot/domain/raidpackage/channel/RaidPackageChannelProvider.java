package com.unfamilia.eggbot.domain.raidpackage.channel;

import com.unfamilia.application.discord.DiscordBotConfigurationProvider;
import discord4j.core.object.entity.channel.TextChannel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.enterprise.context.ApplicationScoped;

import static com.unfamilia.application.discord.DiscordConfigurationKeys.CONFIRMED_CHANNEL_CONFIGURATION_KEY;
import static com.unfamilia.application.discord.DiscordConfigurationKeys.INIT_CHANNEL_CONFIGURATION_KEY;

@Data
@ApplicationScoped
public class RaidPackageChannelProvider {
    private final DiscordBotConfigurationProvider discordBotConfigurationProvider;
    private TextChannel initializeOrderChannel;
    private TextChannel confirmOrderChannel;

    public String getOrderInitName() {
        return discordBotConfigurationProvider.getForKey(INIT_CHANNEL_CONFIGURATION_KEY);
    }

    public String getOrderConfirmedName() {
        return discordBotConfigurationProvider.getForKey(CONFIRMED_CHANNEL_CONFIGURATION_KEY);
    }

    public TextChannel getConfirmOrderChannel() {
        return confirmOrderChannel;
    }

    public void setConfirmOrderChannel(TextChannel confirmOrderChannel) {
        this.confirmOrderChannel = confirmOrderChannel;
    }

    public TextChannel getInitializeOrderChannel() {
        return initializeOrderChannel;
    }

    public void setInitializeOrderChannel(TextChannel initializeOrderChannel) {
        this.initializeOrderChannel = initializeOrderChannel;
    }
}
