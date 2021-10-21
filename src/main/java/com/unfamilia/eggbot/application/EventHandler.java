package com.unfamilia.eggbot.application;

import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;

public interface EventHandler {
    Boolean supports(Event eventClass);
    Mono handle(Event event);
}
