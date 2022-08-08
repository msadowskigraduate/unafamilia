package com.unfamilia.eggbot.domain.raidpackage.command;

import com.unfamilia.application.command.Command;

import java.util.List;
import java.util.UUID;

public record NewOrderCommand(Long orderMessageId, Long orderingUserId, UUID orderId,
                              List<OrderedItem> orderedItems) implements Command {
    public record OrderedItem(Long itemId, Integer quantity) {
    }
}
