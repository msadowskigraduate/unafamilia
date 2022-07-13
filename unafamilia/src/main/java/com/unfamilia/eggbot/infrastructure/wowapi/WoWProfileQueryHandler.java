package com.unfamilia.eggbot.infrastructure.wowapi;

import com.unfamilia.application.query.QueryHandler;
import com.unfamilia.eggbot.infrastructure.WoWProfileClient;
import com.unfamilia.eggbot.infrastructure.wowapi.model.WowProfile;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RequiredArgsConstructor
public class WoWProfileQueryHandler implements QueryHandler<WowProfile, WoWProfileQuery> {
    private final WoWProfileClient profileClient;

    @Override
    public Class<WoWProfileQuery> supports() {
        return WoWProfileQuery.class;
    }

    @Override
    public WowProfile handle(WoWProfileQuery query) {
        return profileClient.queryWowProfile(query.getAuthorizationCode());
    }
}
