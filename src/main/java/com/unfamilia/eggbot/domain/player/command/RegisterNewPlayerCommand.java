package com.unfamilia.eggbot.domain.player.command;

import com.unfamilia.application.command.Command;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class RegisterNewPlayerCommand implements Command {
    private final String accessToken;
    private final String battleTag;
    private final Long wowProfileId;
}
