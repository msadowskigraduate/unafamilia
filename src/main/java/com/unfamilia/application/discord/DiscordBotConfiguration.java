package com.unfamilia.application.discord;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.shard.GatewayBootstrap;
import discord4j.gateway.GatewayOptions;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
public class DiscordBotConfiguration {

    @Produces
    public DiscordClient discordClient(DiscordTokenProvider discordTokenProvider) {
        return DiscordClient.create(discordTokenProvider.getToken());
    }

    @Produces
    @ApplicationScoped
    public EventDispatcher eventDispatcher() {
        return EventDispatcher.builder().build();
    }

    @Produces
    public GatewayDiscordClient gatewayDiscordClient(DiscordClient discordClient, EventDispatcher eventDispatcher) {
        GatewayBootstrap<GatewayOptions> gateway = discordClient.gateway();
        return gateway
                .setEventDispatcher(eventDispatcher)
                .setAwaitConnections(true)
                .login()
                .block();
    }
}
