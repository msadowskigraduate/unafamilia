package com.unfamilia.eggbot.application.events;

import com.unfamilia.eggbot.application.EventHandler;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
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
        return Mono.fromRunnable(() -> {
            final User self = ((ReadyEvent) event).getSelf();
            System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
        });
    }
}
