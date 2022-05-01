package com.unfamilia.eggbot.infrastructure.discord.events.discordcommands;

import com.unfamilia.application.ApplicationConfigProvider;
import com.unfamilia.eggbot.domain.player.Player;
import com.unfamilia.eggbot.infrastructure.session.SessionToken;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import io.quarkus.logging.Log;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
@RequiredArgsConstructor
public class NewPlayerCommandHandler extends DiscordCommandHandler {
    @Override
    ApplicationCommandRequest build() {
        return ApplicationCommandRequest.builder()
                .name(getCommand())
                .description("Link your Battle.Net profile!")
                .build();
    }

    @Override
    public String getCommand() {
        return "link";
    }

    @Override
    public Mono handle(Event event) {
        Log.info("Handling event: " + event);
        var command = (ChatInputInteractionEvent) event;
        var user = command.getInteraction().getUser();
        if (!isUserRegistered(user.getId().asLong())) {
            return registerResponse(user, command);
        }

        return command.reply()
                .withEphemeral(true)
                .withContent("You have already registered!");
    }
}
