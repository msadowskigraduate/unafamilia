package com.unfamilia.eggbot.application.events;

import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
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
        final MessageChannel channel = message.getChannel().block();
        return channel.createMessage(createStatusMessage()).log();
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }

    private String createStatusMessage() {
        return "Online and Connected! Cuba/Artio Sucks huge VPS COCKS.";
    }
}
