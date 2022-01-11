package com.unfamilia.application.discord;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.shard.GatewayBootstrap;
import discord4j.core.shard.ShardingStrategy;
import discord4j.gateway.GatewayOptions;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@Dependent
public class DiscordBotConfiguration {

    @Produces
    public DiscordClient discordClient(DiscordBotConfigurationProvider discordBotConfigurationProvider) {
        return DiscordClient.create(discordBotConfigurationProvider.getToken());
    }

    @Produces
    @Singleton
    public EventDispatcher eventDispatcher() {
        return EventDispatcher.builder().build();
    }

    @Produces
    @Singleton
    public GatewayDiscordClient gatewayDiscordClient(DiscordClient discordClient, EventDispatcher eventDispatcher) {
        GatewayBootstrap<GatewayOptions> gateway = discordClient.gateway();
        return gateway
                .setEventDispatcher(eventDispatcher)
                .setAwaitConnections(true)
                .setSharding(ShardingStrategy.single())
                .login()
                .log()
                .block();
    }
}
