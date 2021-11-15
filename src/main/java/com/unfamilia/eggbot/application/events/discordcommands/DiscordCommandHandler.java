package com.unfamilia.eggbot.application.events.discordcommands;

import com.unfamilia.eggbot.application.events.handlers.EventHandler;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;

abstract class DiscordCommandHandler implements EventHandler {

    public Boolean hasChanged() {
        return false;
    }

    protected Boolean isApplicationCommand(Event event) {
        return event.getClass().isAssignableFrom(ApplicationCommandInteractionEvent.class);
    }

    protected Boolean isSlashCommand(Event event) {
        return event.getClass().isAssignableFrom(ChatInputInteractionEvent.class);
    }

    abstract ApplicationCommandRequest build();
    abstract String getCommand();
}
