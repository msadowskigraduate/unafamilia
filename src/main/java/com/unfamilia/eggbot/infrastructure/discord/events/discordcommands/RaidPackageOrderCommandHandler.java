package com.unfamilia.eggbot.infrastructure.discord.events.discordcommands;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.eggbot.domain.raidpackage.Item;
import com.unfamilia.eggbot.domain.raidpackage.command.NewOrderCommand;
import com.unfamilia.eggbot.domain.raidpackage.command.NewOrderCommandFactory;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import io.quarkus.logging.Log;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class RaidPackageOrderCommandHandler extends DiscordCommandHandler {
    private final static String COMMAND_NAME = "raidpackage";
    private final CommandBus commandBus;

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

        NewOrderCommand order = NewOrderCommandFactory.from(slashCommand);
        commandBus.handle(order);

        return slashCommand.reply()
                .withEmbeds(embedBuilder(order).build());
    }

    @Override
    @Transactional
    public ApplicationCommandRequest build() {
        List<Item> offeredItems = Item.listAll();

        List<ApplicationCommandOptionData> optionData = offeredItems.stream()
                .map(item -> ApplicationCommandOptionData.builder()
                        .name(item.getSlug())
                        .description(item.getItemSubclass().getName())
                        .type(ApplicationCommandOption.Type.INTEGER.getValue())
                        .required(false)
                        .build())
                .collect(Collectors.toList());

        return ApplicationCommandRequest.builder()
                .name(getCommand())
                .description("Create raid package order!")
                .options(optionData)
                .build();
    }

    @Override
    public String getCommand() {
        return COMMAND_NAME;
    }

    private EmbedCreateSpec.Builder embedBuilder(NewOrderCommand order) {
        var builder = EmbedCreateSpec.builder()
                .title("Raid Package Order")
                .color(Color.BISMARK)
                .timestamp(Instant.now())
                .description("Thank you for your order!")
                .addField(
                        "Delivery and Payment",
                        "An officer will mail you the items," +
                                " and message you the price to deposit into the guild bank before the raid begins.",
                        false);

        order.getOrderedItems()
                .forEach(orderItem -> builder.addField(
                        orderItem.getItemName(),
                        orderItem.getQuantity().toString(),
                        false));

        return builder;
    }
}