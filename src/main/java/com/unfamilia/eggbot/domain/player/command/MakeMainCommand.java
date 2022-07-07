package com.unfamilia.eggbot.domain.player.command;

import com.unfamilia.application.command.Command;
import com.unfamilia.eggbot.domain.player.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class MakeMainCommand implements Command {
    private final Player userId;
    private final Long characterId;
}
