package com.unfamilia.eggbot.application.events.handlers;

import com.unfamilia.eggbot.application.raidpackage.channel.RaidPackageChannelProvider;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.TextChannel;
import reactor.core.publisher.Mono;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class OnGuildJoinEventHandler implements EventHandler {

    private final RaidPackageChannelProvider channelProvider;
//    private final RaidPackageEmbedProvider RaidPackageEmbedProvider

    public OnGuildJoinEventHandler(RaidPackageChannelProvider channelProviders) {
        this.channelProvider = channelProviders;
    }

    @Override
    public Boolean supports(Event event) {
        return GuildCreateEvent.class.isAssignableFrom(event.getClass());
    }

    @Override
    public Mono handle(Event event) {
        GuildCreateEvent guildEvent = (GuildCreateEvent) event;
        checkOrCreateOrderConfirmChannel(guildEvent.getGuild());
        checkOrCreateOrderInitChannel(guildEvent.getGuild());
        return Mono.empty();
    }

    private void checkOrCreateOrderInitChannel(Guild guild) {
        Optional<GuildChannel> initChannel = guild.getChannels()
                .toStream()
                .filter(channel -> channel.getName().equalsIgnoreCase(channelProvider.getOrderInitName()))
                .findAny();

        if (initChannel.isEmpty()) {
            channelProvider.setInitializeOrderChannel(guild.createTextChannel(spec -> {
                spec.setName(channelProvider.getOrderInitName());
                spec.setNsfw(false);
            }).block());
        } else {
            channelProvider.setInitializeOrderChannel((TextChannel) initChannel.get());
        }
    }

    private void checkOrCreateOrderConfirmChannel(Guild guild) {
        Optional<GuildChannel> confirmedChannel = guild.getChannels()
                .toStream()
                .filter(channel -> channel.getName().equalsIgnoreCase(channelProvider.getOrderConfirmedName()))
                .findAny();

        if (confirmedChannel.isEmpty()) {
            channelProvider.setInitializeOrderChannel(guild.createTextChannel(spec -> {
                spec.setName(channelProvider.getOrderConfirmedName());
                spec.setNsfw(false);
            }).block());
        } else {
            channelProvider.setInitializeOrderChannel((TextChannel) confirmedChannel.get());
        }
    }
}
