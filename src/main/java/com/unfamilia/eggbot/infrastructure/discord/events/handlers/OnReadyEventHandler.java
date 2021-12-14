package com.unfamilia.eggbot.infrastructure.discord.events.handlers;

import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import io.quarkus.logging.Log;
import reactor.core.publisher.Mono;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OnReadyEventHandler implements EventHandler {

    @Override
    public Boolean supports(Event event) {
        return event.getClass().isAssignableFrom(ReadyEvent.class);
    }

    @Override
    public Mono<Void> handle(Event event) {
        Log.info("Handling event:" + event);
        final User self = ((ReadyEvent) event).getSelf();
        Log.info(String.format("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator()));
        return Mono.empty();
    }
}
