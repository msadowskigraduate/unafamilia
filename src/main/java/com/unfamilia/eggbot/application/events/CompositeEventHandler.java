package com.unfamilia.eggbot.application.events;

import com.unfamilia.eggbot.application.events.handlers.EventHandler;
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
//
//    public void handle(Event discordEvent) {
//        eventHandlers.stream()
//                .filter(eventHandler -> eventHandler.supports(discordEvent))
//                .sorted(Comparator.comparingInt(EventHandler::getPriority))
//                .map(eventHandler -> eventHandler.handle(discordEvent));
//    }

    public Mono handle(Event discordEvent) {
        Optional<EventHandler> handler = eventHandlers.stream()
                .filter(eventHandler -> eventHandler.supports(discordEvent))
                .findFirst();

        if(handler.isPresent()) {
            return handler.get().handle(discordEvent);
        }

        return Mono.empty();
    }
}
