package com.unfamilia.eggbot.domain.raidpackage.command;

import com.unfamilia.application.command.Command;
import com.unfamilia.eggbot.domain.raidpackage.OrderItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@ToString
@Getter
@RequiredArgsConstructor
public class NewOrderCommand implements Command {
    private final Long orderMessageId;
    private final Long orderingUserId;
    private final List<OrderedItem> orderedItems;

    @Value
    public static class OrderedItem {
        String itemName;
        Integer quantity;
    }
}
