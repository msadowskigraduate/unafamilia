package com.unfamilia.eggbot.infrastructure.discord;

import com.unfamilia.eggbot.application.CompositeEventHandler;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class DiscordApiClient {
    private final GatewayDiscordClient gatewayDiscordClient;
    private final CompositeEventHandler compositeEventHandler;

    public DiscordApiClient(GatewayDiscordClient gatewayDiscordClient, CompositeEventHandler compositeEventHandler) {
        this.gatewayDiscordClient = gatewayDiscordClient;
        this.compositeEventHandler = compositeEventHandler;
    }

    void startup(@Observes StartupEvent event) {
        gatewayDiscordClient.on(Event.class, compositeEventHandler::handle)
                .subscribe();
    }
}
