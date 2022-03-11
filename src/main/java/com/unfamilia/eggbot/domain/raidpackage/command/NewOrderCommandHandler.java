package com.unfamilia.eggbot.domain.raidpackage.command;

import com.unfamilia.application.command.Command;
import com.unfamilia.application.command.CommandHandler;
import com.unfamilia.eggbot.domain.raidpackage.Item;
import com.unfamilia.eggbot.domain.raidpackage.Order;
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

        Order.of(
                newOrderCommand.getOrderMessageId(),
                newOrderCommand.getOrderingUserId(),
                false,
                false,
                Instant.now(),
                newOrderCommand.getOrderedItems().stream()
                        .map(item -> Item.findByName(item.getItemName()))
                        .collect(Collectors.toList())
        ).persistAndFlush();
    }
}
