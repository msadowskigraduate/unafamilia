package com.unfamilia.eggbot.domain.player.command;

import com.unfamilia.application.command.Command;
import com.unfamilia.eggbot.infrastructure.session.SessionToken;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class RegisterNewPlayerFromDiscordCommand implements Command {
    private final String accessToken;
    private final SessionToken sessionToken;
    private final String battleTag;
    private final Long wowProfileId;
}
