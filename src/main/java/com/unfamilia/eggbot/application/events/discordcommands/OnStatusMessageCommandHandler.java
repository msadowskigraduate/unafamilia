package com.unfamilia.eggbot.application.events.discordcommands;

import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import io.quarkus.logging.Log;
import reactor.core.publisher.Mono;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;

@ApplicationScoped
public class OnStatusMessageCommandHandler extends DiscordCommandHandler {
    private final static String COMMAND_NAME = "status";

    @Override
    public Boolean supports(Event event) {
        if (!isSlashCommand(event)) {
            return false;
        }

        ChatInputInteractionEvent slashCommand = (ChatInputInteractionEvent) event;
        return slashCommand.getCommandName().equalsIgnoreCase(this.getCommand());
    }

    @Override
    public Mono handle(Event event) {
        ChatInputInteractionEvent slashCommand = (ChatInputInteractionEvent) event;
        Log.info("Handling event: " + event);
        return slashCommand.reply(spec ->
                spec.setEphemeral(true)
                        .addEmbed(this::createRaidPackageEmbed)
        );
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

    private EmbedCreateSpec createRaidPackageEmbed(EmbedCreateSpec spec) {
        return spec
                .setTitle("Raid Package Order")
                .setColor(Color.BISMARK)
                .setTimestamp(Instant.now())
                .setDescription("Click the \uD83D\uDCDD reaction below to begin your order")
                .addField(
                        "Intro",
                        "Welcome to the raid consumable ordering service. You can use this service to order" +
                                " consumables before a raid night at a cheaper price than the auction house.",
                        false)
                .addField(
                        "Starting a new order",
                        "To begin, click the \uD83D\uDCDD emoji below, and the bot will send you a DM. " +
                                "Simply click the items you want and type the quantities. Click the green tick to confirm your order.",
                        false)
                .addField(
                        "Delivery and Payment",
                        "An officer will mail you the items," +
                                " and message you the price to deposit into the guild bank before the raid begins.",
                        false)
                ;
    }
}
