package com.unfamilia.eggbot.domain.raidpackage.command;

import com.unfamilia.application.command.Command;
import lombok.Getter;

@Getter
//@AllArgsConstructor(staticName = "of")
public class NewItemCommand implements Command {
    //    private final ItemDto itemDto;
    // implement fields from itemDto
    // create new class to map itemDto into new ItemCommand
    // Or handle this mapping in a controller called DiscordController method addItem
    private Long id;
    private Integer maxAmount;
    private String slug;

    public NewItemCommand(Long id, Integer maxAmount, String slug) {
        this.id = id;
        this.maxAmount = maxAmount;
        this.slug = slug;
    }
}
