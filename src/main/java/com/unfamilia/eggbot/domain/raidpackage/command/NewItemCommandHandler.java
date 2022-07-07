package com.unfamilia.eggbot.domain.raidpackage.command;

import com.unfamilia.application.command.Command;
import com.unfamilia.application.command.CommandHandler;
import com.unfamilia.eggbot.domain.raidpackage.Item;
import com.unfamilia.eggbot.domain.raidpackage.ItemSubclass;
import com.unfamilia.eggbot.infrastructure.wowapi.WoWGameDataClient;
import com.unfamilia.eggbot.infrastructure.wowapi.model.WowItem;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
@RequiredArgsConstructor
public class NewItemCommandHandler implements CommandHandler {
    private final WoWGameDataClient client;

    @Override
    public boolean supports(Command command) {
        return command instanceof NewItemCommand;
    }

    @Override
    @Transactional
    public void handle(Command command) {
        var newItemCommand = (NewItemCommand) command;

        if (Item.findByIdOptional(newItemCommand.getId()).isPresent()) {
            return;
        }

        handle(newItemCommand,
                client.getWowItem(newItemCommand.getId()));
    }

    private void handle(NewItemCommand newItemCommand, WowItem wowItem) {
        if (wowItem.getId() == null) {
            return;
        }

        var subclass = ItemSubclass.findByName(wowItem.getItemClass().getName());

        if (subclass == null) {
            subclass = new ItemSubclass(wowItem.getItemClass().getId(), wowItem.getItemClass().getName());
            subclass.persist();
        }

        Item item = new Item(
                wowItem.getId(),
                wowItem.getName(),
                subclass,
                0.0,
                newItemCommand.getSlug(),
                newItemCommand.getMaxAmount(),
                wowItem.getMediaLink()
        );
        item.persist();
    }
}
