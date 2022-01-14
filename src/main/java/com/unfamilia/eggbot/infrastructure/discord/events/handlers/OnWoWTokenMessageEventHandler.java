package com.unfamilia.eggbot.infrastructure.discord.events.handlers;

import com.unfamilia.eggbot.infrastructure.wowapi.WoWGameDataClient;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;

@ApplicationScoped
@RequiredArgsConstructor
public class OnWoWTokenMessageEventHandler implements MessageEventHandler {
    private final static String COMMAND = "!wowtoken";
    private final WoWGameDataClient woWGameDataClient;

    @Override
    public Boolean supports(Event event) {
        if(!event.getClass().isAssignableFrom(MessageCreateEvent.class)) {
            return false;
        }

        final Message message = ((MessageCreateEvent) event).getMessage();
        return getCommand().equalsIgnoreCase(message.getContent());
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public Mono handle(Event event) {
        final Message message = ((MessageCreateEvent) event).getMessage();
        message.delete();
        try {
            var token = woWGameDataClient.getWoWTokenPrice();
            return message.getChannel()
                    .flatMap(channel -> channel.createMessage(
                            EmbedCreateSpec.builder()
                                    .timestamp(Instant.now())
                                    .color(Color.YELLOW)
                                    .title("WoW Token EU Price")
                                    .addField(
                                            "Price",
                                            token.getPrice().toString(),
                                            false
                                    )
                                    .addField(
                                            "Last updated",
                                            token.getLastUpdatedTimestamp().toString(),
                                            false
                                    )
                                    .build()
                    ));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.empty();
    }
}
