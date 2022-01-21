package com.unfamilia.eggbot.domain.raidpackage.command;

import com.unfamilia.application.command.Command;
import com.unfamilia.application.discord.model.ItemDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class NewItemCommand implements Command {
    private final ItemDto itemDto;
}
