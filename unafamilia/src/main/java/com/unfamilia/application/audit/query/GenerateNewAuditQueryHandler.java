package com.unfamilia.application.audit.query;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.unfamilia.application.query.QueryHandler;
import com.unfamilia.eggbot.infrastructure.reporter.WishlistReporterAdapter;
import com.unfamilia.eggbot.infrastructure.utilities.RealmMapper;
import com.unfamilia.eggbot.infrastructure.wowguild.WoWApiGuildAdapter;
import com.unfamilia.eggbot.infrastructure.wowguild.model.CharacterProfileResponse;

@ApplicationScoped
public class GenerateNewAuditQueryHandler implements QueryHandler<List<CharacterProfileResponse>, GenerateNewAuditQuery> {
    @Inject @RestClient WishlistReporterAdapter adapter;
    @Inject @RestClient WoWApiGuildAdapter wowApiAdapter;

    @Override
    public List<CharacterProfileResponse> handle(GenerateNewAuditQuery query) {
        var roster = adapter.queryRoster();
        return roster.stream()
            .map(character -> wowApiAdapter.queryCharacter(character.name(), RealmMapper.toSlug(character.realm())))
            .collect(Collectors.toList());
    }

    @Override
    public Class<GenerateNewAuditQuery> supports() {
        return GenerateNewAuditQuery.class;
    }
    
}
