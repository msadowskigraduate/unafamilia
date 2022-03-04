package com.unfamilia.eggbot.domain.player;

import com.unfamilia.application.command.Query;
import com.unfamilia.application.command.QueryHandler;
import com.unfamilia.eggbot.infrastructure.WoWProfileClient;
import com.unfamilia.eggbot.infrastructure.wowapi.BattleNetAuthClient;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RequiredArgsConstructor
public class PlayerAddCommandHandler implements QueryHandler {
    private final BattleNetAuthClient authClient;
    private final WoWProfileClient profileClient;

    @Override
    public boolean supports(Query command) {
        return command instanceof PlayerQuery;
    }

    @Override
    public Query handle(Query query) {
        var wowProfile = profileClient.queryWowProfile(((PlayerQuery) query).getAuthorizationCode());
        ((PlayerQuery) query).setProfile(wowProfile);
        return query;
    }
}
