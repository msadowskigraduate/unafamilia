package com.unfamilia.eggbot.application.events;

import com.unfamilia.eggbot.application.events.handlers.EventHandler;
import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class CompositeEventHandler {

    @Inject
    Instance<EventHandler> eventHandlers;

//    public Mono handle(Event discordEvent) {
//        Optional<EventHandler> handler = eventHandlers.stream()
//                .filter(eventHandler -> eventHandler.supports(discordEvent))
//                .findFirst();
//
//        if(handler.isPresent()) {
//            return handler.get().handle(discordEvent);
//        }
//
//        return Mono.empty();
//    }

    public Mono handle(Event discordEvent) {
        return eventHandlers.stream()
                .filter(eventHandler -> eventHandler.supports(discordEvent))
                .map(eventHandler -> eventHandler.handle(discordEvent))
                .reduce(Mono.empty(), Mono::and);
    }
}
