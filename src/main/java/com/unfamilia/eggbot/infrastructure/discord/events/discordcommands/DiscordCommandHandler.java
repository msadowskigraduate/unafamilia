package com.unfamilia.eggbot.infrastructure.discord.events.discordcommands;

import com.unfamilia.eggbot.infrastructure.discord.events.handlers.EventHandler;
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
    public abstract String getCommand();
}
