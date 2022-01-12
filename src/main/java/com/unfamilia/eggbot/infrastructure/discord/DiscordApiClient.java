package com.unfamilia.eggbot.infrastructure.discord;

import com.unfamilia.eggbot.infrastructure.discord.events.CompositeEventHandler;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import io.quarkus.runtime.StartupEvent;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
@RequiredArgsConstructor
public class DiscordApiClient {
    private final GatewayDiscordClient gatewayDiscordClient;
    private final CompositeEventHandler compositeEventHandler;

    void startup(@Observes StartupEvent event) {
        gatewayDiscordClient.on(Event.class, compositeEventHandler::handle)
                .subscribe();
    }
}
