package com.unfamilia.eggbot.domain.raidpackage.command;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;

import java.util.List;
import java.util.stream.Collectors;

public class NewOrderCommandFactory {
    public static NewOrderCommand from(ChatInputInteractionEvent event) {
        Long interactionId = event.getInteraction().getId().asLong();
        Long orderingUserId = event.getInteraction().getUser().getId().asLong();

        List<NewOrderCommand.OrderedItem> orderedItems = event.getOptions().stream()
                .map(NewOrderCommandFactory::from)
                .collect(Collectors.toList());
        return new NewOrderCommand(interactionId, orderingUserId, orderedItems);
    }

    private static NewOrderCommand.OrderedItem from(ApplicationCommandInteractionOption option) {
        String itemName = option.getName();
        Long quantity = 0L;
        if(option.getValue().isPresent()) {
            quantity = option.getValue().get().asLong();
        }
        return new NewOrderCommand.OrderedItem(itemName, Math.toIntExact(quantity));
    }
}
