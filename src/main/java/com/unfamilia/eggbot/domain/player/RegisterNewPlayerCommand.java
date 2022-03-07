package com.unfamilia.eggbot.domain.player;

import com.unfamilia.application.command.Command;
import com.unfamilia.eggbot.infrastructure.wowapi.model.WowProfile;
import discord4j.core.object.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class RegisterNewPlayerCommand implements Command {
    private final WowProfile wowProfile;
    private final User user;
}
