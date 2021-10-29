package com.unfamilia.eggbot.application.events.discordcommands;

import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import io.quarkus.logging.Log;
import reactor.core.publisher.Mono;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OnStatusMessageCommandHandler extends DiscordCommandHandler {
    private final static String COMMAND_NAME = "status";

    @Override
    public Boolean supports(Event event) {
        if(!isSlashCommand(event)) {
            return false;
        }

        ChatInputInteractionEvent slashCommand = (ChatInputInteractionEvent) event;
        return slashCommand.getCommandName().equalsIgnoreCase(this.getCommand());
    }

    @Override
    public Mono handle(Event event) {
        ChatInputInteractionEvent slashCommand = (ChatInputInteractionEvent) event;
        Log.info("Handling event: " + event);
        return slashCommand.reply(createStatusMessage());
    }

    @Override
    public ApplicationCommandRequest build() {
        return ApplicationCommandRequest.builder()
                .name(getCommand())
                .description("Returns Status")
                .build();
    }

    @Override
    public String getCommand() {
        return COMMAND_NAME;
    }

    private String createStatusMessage() {
        return "Online and Connected! Cuba/Artio Sucks huge VPS COCKS.";
    }
}
