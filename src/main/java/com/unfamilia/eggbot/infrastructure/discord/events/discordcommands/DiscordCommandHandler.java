package com.unfamilia.eggbot.infrastructure.discord.events.discordcommands;

import com.unfamilia.application.ApplicationConfigProvider;
import com.unfamilia.eggbot.domain.player.Player;
import com.unfamilia.eggbot.infrastructure.discord.events.handlers.EventHandler;
import com.unfamilia.eggbot.infrastructure.session.SessionToken;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.User;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import javax.transaction.Transactional;

abstract class DiscordCommandHandler implements EventHandler {
    @Inject ApplicationConfigProvider configProvider;

    public Boolean hasChanged() {
        return false;
    }

    protected Boolean isApplicationCommand(Event event) {
        return event.getClass().isAssignableFrom(ApplicationCommandInteractionEvent.class);
    }

    protected Boolean isSlashCommand(Event event) {
        return event instanceof ChatInputInteractionEvent;
    }

    @Override
    public Boolean supports(Event event) {
        if (!isSlashCommand(event)) {
            return false;
        }

        ChatInputInteractionEvent slashCommand = (ChatInputInteractionEvent) event;
        return slashCommand.getCommandName().equalsIgnoreCase(this.getCommand());
    }


    @Transactional
    protected boolean isUserRegistered(Long userId) {
        return Player.findByIdOptional(userId).isPresent();
    }

    protected Mono registerResponse(User user, ChatInputInteractionEvent command) {
        var member = command.getInteraction().getMember().get();
        SessionToken token = SessionToken.generateForUser(member);
        return command.reply()
                .withContent("I am sorry but you have not registered to use me yet. <wink wink> Visit: " + buildUrlLogin(token))
                .withEphemeral(true);
    }

    private String buildUrlLogin(SessionToken token) {
        return configProvider.hostname() + "/discord?session_token=" + token.getToken();
    }

    abstract ApplicationCommandRequest build();
    public abstract String getCommand();
}
