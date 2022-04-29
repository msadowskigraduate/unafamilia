package com.unfamilia.eggbot.domain.player.command;

import com.unfamilia.application.command.Command;
import discord4j.core.object.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class MakeMainCommand implements Command {
    private final User user;
    private final Long characterId;
}
