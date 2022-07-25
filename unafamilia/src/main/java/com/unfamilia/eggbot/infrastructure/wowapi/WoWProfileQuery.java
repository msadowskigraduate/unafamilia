package com.unfamilia.eggbot.infrastructure.wowapi;

import com.unfamilia.application.query.Query;
import com.unfamilia.eggbot.infrastructure.wowapi.model.WowProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor(staticName = "of")
public class WoWProfileQuery implements Query<WowProfile> {
    private final String authorizationCode;
}
