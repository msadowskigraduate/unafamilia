package com.unfamilia.application.audit.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.unfamilia.application.audit.RosterAuditModel;
import com.unfamilia.application.query.QueryHandler;
import com.unfamilia.eggbot.infrastructure.reporter.AuditReport;
import com.unfamilia.eggbot.infrastructure.reporter.WishlistReporterAdapter;
import com.unfamilia.eggbot.infrastructure.utilities.RealmMapper;
import com.unfamilia.eggbot.infrastructure.wowguild.WoWApiGuildAdapter;

@ApplicationScoped
public class GenerateNewAuditQueryHandler implements QueryHandler<List<RosterAuditModel>, GenerateNewAuditQuery> {
    @Inject @RestClient WishlistReporterAdapter adapter;
    @Inject @RestClient WoWApiGuildAdapter wowApiAdapter;

    @Override
    public List<RosterAuditModel> handle(GenerateNewAuditQuery query) {
        var roster = adapter.queryRoster();
        var audit = collectToMap(adapter.queryAudit());
        return roster.stream()
            .map(character -> wowApiAdapter.queryCharacter(character.name(), RealmMapper.toSlug(character.realm())))
            .map(profile -> {
                var auditReport = audit.get(profile.name() + "-" + profile.realm().name());
                return new RosterAuditModel(
                    profile.name(),
                    profile.realm().name(),
                    profile.characterClass().characterClass(),
                    profile.characterClass().characterClassId(), 
                    profile.averageItemLevel(), 
                    profile.equippedItemLevel(), 
                    auditReport.data().dungeonsDone(), 
                    auditReport.data().worldQuestsDone(), 
                    auditReport.data().highestKeystoneDone(), 
                    auditReport.data().vaultOptions().dungeons().option1(),
                    auditReport.data().vaultOptions().dungeons().option2(),
                    auditReport.data().vaultOptions().dungeons().option3(),
                    auditReport.data().vaultOptions().raids().option1(),
                    auditReport.data().vaultOptions().raids().option2(),
                    auditReport.data().vaultOptions().raids().option3(),
                    auditReport.data().vaultOptions().pvp().option1(),
                    auditReport.data().vaultOptions().pvp().option2(),
                    auditReport.data().vaultOptions().pvp().option3());
            })
            .collect(Collectors.toList());
    }

    @Override
    public Class<GenerateNewAuditQuery> supports() {
        return GenerateNewAuditQuery.class;
    }

    private Map<String, AuditReport.Character> collectToMap(AuditReport reports) {
        return reports.characters().stream().collect(Collectors.toMap(key -> key.name() + "-" + key.realm(), value -> value));
    }
    
}
