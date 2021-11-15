package com.unfamilia.eggbot.application.events.handlers;

import discord4j.core.event.domain.Event;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public interface MessageEventHandler extends EventHandler {
    String getCommand();
    @Override
    Mono<Message> handle(Event event);
}
