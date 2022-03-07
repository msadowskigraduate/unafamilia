package com.unfamilia.eggbot.infrastructure.discord.events.discordcommands;

import com.unfamilia.eggbot.infrastructure.session.SessionToken;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import io.quarkus.logging.Log;
import reactor.core.publisher.Mono;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
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
        ChatInputInteractionEvent slashCommand = (ChatInputInteractionEvent) event;
        var user = slashCommand.getInteraction().getUser();
        SessionToken token = SessionToken.generateForUser(user);
        var url = "http://localhost:9000/discord?session_token=" + token.getToken();
        return slashCommand.reply().withContent("Visit " + url).withEphemeral(true);
    }
}
