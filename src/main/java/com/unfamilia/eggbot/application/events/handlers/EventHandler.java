package com.unfamilia.eggbot.application.events.handlers;

import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;

public interface EventHandler {
    Boolean supports(Event event);
    Mono handle(Event event);
    default Integer getPriority() {
        return HandlerPriority.NORMAL;
    }
}
