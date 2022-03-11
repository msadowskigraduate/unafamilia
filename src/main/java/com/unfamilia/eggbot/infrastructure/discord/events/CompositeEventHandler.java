package com.unfamilia.eggbot.infrastructure.discord.events;

import com.unfamilia.eggbot.infrastructure.discord.events.handlers.EventHandler;
import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class CompositeEventHandler {

    @Inject
    Instance<EventHandler> eventHandlers;

    public Mono handle(Event discordEvent) {
        return eventHandlers.stream()
                .filter(eventHandler -> eventHandler.supports(discordEvent))
                .map(eventHandler -> eventHandler.handle(discordEvent))
                .reduce(Mono.empty(), Mono::and);
    }
}
