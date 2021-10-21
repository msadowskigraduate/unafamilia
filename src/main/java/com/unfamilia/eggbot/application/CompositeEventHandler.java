package com.unfamilia.eggbot.application;

import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class CompositeEventHandler {

    @Inject
    Instance<EventHandler> eventHandlers;

    public Mono handle(Event discordEvent) {
        Optional<EventHandler> handler = eventHandlers.stream()
                .filter(h -> h.supports(discordEvent))
                .findFirst();

        if(handler.isEmpty()) {
            return Mono.empty();
        }

        return handler.get().handle(discordEvent);
    }
}
