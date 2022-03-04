package com.unfamilia.eggbot.domain.player;

import com.unfamilia.application.command.Query;
import com.unfamilia.eggbot.infrastructure.wowapi.model.WowProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor(staticName = "of")
public class PlayerQuery implements Query {
    private final String authorizationCode;
    private WowProfile profile;
}
