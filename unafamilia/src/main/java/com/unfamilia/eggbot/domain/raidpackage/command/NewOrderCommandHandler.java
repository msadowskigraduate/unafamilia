package com.unfamilia.eggbot.domain.raidpackage.command;

import com.unfamilia.application.command.Command;
import com.unfamilia.application.command.CommandHandler;
import com.unfamilia.eggbot.domain.raidpackage.Item;
import com.unfamilia.eggbot.domain.raidpackage.Order;
import com.unfamilia.eggbot.domain.raidpackage.OrderItem;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class NewOrderCommandHandler implements CommandHandler {

    @Override
    public boolean supports(Command command) {
        return command instanceof NewOrderCommand;
    }

    @Override
    @Transactional
    public void handle(Command command) {
        var newOrderCommand = (NewOrderCommand) command;

        var orderItemsList = newOrderCommand.orderedItems().stream()
                        .map(orderedItem -> OrderItem.of(
                                Item.findByName(orderedItem.itemName()),
                                orderedItem.quantity()
                        ))
                .peek(orderItem -> orderItem.persist())
                .collect(Collectors.toList());

        Order.of(
                newOrderCommand.orderId(),
                newOrderCommand.orderMessageId(),
                newOrderCommand.orderingUserId(),
                null,
                false,
                false,
                Instant.now(),
                orderItemsList
        ).persist();
    }
}
