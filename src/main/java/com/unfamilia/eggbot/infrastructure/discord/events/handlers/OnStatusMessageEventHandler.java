package com.unfamilia.eggbot.infrastructure.discord.events.handlers;

import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OnStatusMessageEventHandler implements MessageEventHandler {
    private final static String COMMAND = "!status";

    @Override
    public Boolean supports(Event event) {
        if(!event.getClass().isAssignableFrom(MessageCreateEvent.class)) {
            return false;
        }

        final Message message = ((MessageCreateEvent) event).getMessage();
        return getCommand().equalsIgnoreCase(message.getContent());
    }

    @Override
    public Mono<Message> handle(Event event) {
        final Message message = ((MessageCreateEvent) event).getMessage();
        return message.getChannel()
                .flatMap(channel -> channel.createMessage(createStatusMessage()));
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }

    private String createStatusMessage() {
        return "Online and Connected! Cuba/Artio Sucks huge VPS COCKS.";
    }
}
