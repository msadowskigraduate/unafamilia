package com.unfamilia.application.report.query;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.unfamilia.application.query.QueryHandler;
import com.unfamilia.application.user.User;
import com.unfamilia.eggbot.infrastructure.reporter.Roster;
import com.unfamilia.eggbot.infrastructure.reporter.WishlistReport;
import com.unfamilia.eggbot.infrastructure.reporter.WishlistReporterAdapter;
import com.unfamilia.eggbot.infrastructure.utilities.RealmMapper;

import io.quarkus.panache.common.Parameters;

@ApplicationScoped
public class NewWishlistReportQueryHandler implements QueryHandler<List<WishlistReport>, NewWishlistReportQuery> {
    @Inject
    @RestClient
    WishlistReporterAdapter adapter;
    
    @Override
    public Class<NewWishlistReportQuery> supports() {
        return NewWishlistReportQuery.class;
    }

    @Override
    public List<WishlistReport> handle(NewWishlistReportQuery query) {
        List<Roster> roster = adapter.queryRoster();
        return roster.stream()
            .filter(character -> query.roles().contains(character.role()))
            .map(character -> adapter.queryReportForRosterV2(String.valueOf(character.id()), query.difficulty()))
            .map(this::augmentReportWithUserData)
            .collect(Collectors.toList());
    }

    @Transactional
    private WishlistReport augmentReportWithUserData(WishlistReport report) {
        try {
            var user = User.<User>find(
                    "select u from Users u inner join u.characters c where c.name = :name and c.realm = :realm",
                    Parameters.with("name", report.characterName()).and("realm", RealmMapper.toSlug(report.realm()))).singleResult();
            return new WishlistReport(report.name(), report.characterName(), report.realm(), user.getDiscordUserId(), user.getBattleNetUserId(), user.getRank(), report.error(), report.issues());
        } catch (NoResultException exception) {
            return new WishlistReport(report.name(), report.characterName(), report.realm(), null, null, null, "User is not registered with this service. Cannot fetch World of Warcraft and Discord profile data.", report.issues());
        }
    }
}