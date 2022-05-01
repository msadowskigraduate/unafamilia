package com.unfamilia.eggbot.infrastructure.discord.events.discordcommands;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.eggbot.domain.raidpackage.command.NewItemCommand;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import io.quarkus.logging.Log;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
@RequiredArgsConstructor
public class AddNewItemCommandHandler extends DiscordCommandHandler {
    private final static String COMMAND_NAME = "additem";
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
        // Retrieve option value from event
        // Refactor this into a factory: Long id = retrieveOptionValue();
        //for now this will do for poc:
        ChatInputInteractionEvent slashCommand = (ChatInputInteractionEvent) event;
        Log.info("Handling event: " + event);
        Long id = slashCommand.getOption("id")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asLong)
                .get();


        // Create NewItemCommand takes in option value (id)
//        Long id = Long.valueOf(171276);
        Integer maxAmount = Math.toIntExact(slashCommand.getOption("maxamount")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asLong)
                .get());


        String slug = slashCommand.getOption("slug")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();

        var newItemCommand = new NewItemCommand(id, maxAmount, slug);
        Log.info("newItemCommand created: " + newItemCommand);
        // Emit this command onto the command bus
        commandBus.handle(newItemCommand);
        return slashCommand.reply("Item: " + id + " has been added.");
    }

    @Override
    public ApplicationCommandRequest build() {
        return ApplicationCommandRequest.builder()
                .name(getCommand())
                .description("Add a new item using the WoW item ID")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("id")
                        .description("Item ID (can find using WoWHead)")
                        .type(ApplicationCommandOption.Type.INTEGER.getValue())
                        .required(true)
                        .build()
                    )
                .addOption(ApplicationCommandOptionData.builder()
                        .name("maxamount")
                        .description("Maximum amount purchasable in one transaction")
                        .type(ApplicationCommandOption.Type.INTEGER.getValue())
                        .required(true)
                        .build()
                )
                .addOption(ApplicationCommandOptionData.builder()
                        .name("slug")
                        .description("Name of the item in lowercase separated by '-'. eg shaded-sharpening-stone")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .required(true)
                        .build()
                )
                .build();
    }

    @Override
    public String getCommand() {
        return COMMAND_NAME;
    }
}
