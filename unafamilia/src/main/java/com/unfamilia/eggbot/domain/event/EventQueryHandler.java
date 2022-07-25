package com.unfamilia.eggbot.domain.event;

import com.unfamilia.application.query.QueryHandler;
import com.unfamilia.eggbot.domain.player.Player;
import com.unfamilia.eggbot.domain.player.Role;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
@RequiredArgsConstructor
public class EventQueryHandler implements QueryHandler<Event, EventQuery> {
    @Override
    public Class<EventQuery> supports() {
        return EventQuery.class;
    }

    @Override
    @Transactional
    public Event handle(EventQuery query) {
        var roles = query.getRoles().stream()
                .map(discordRole -> Role.<Role>findByIdOptional(discordRole.id()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
        var organizer = Player.findByDiscordUserId(query.getOrganizingUserId()).get();

        var event = Event.of(
                query.getName(),
                query.getActivity(),
                roles,
                query.getDate(),
                organizer,
                List.of()
        );

        event.persist();
        return event;
    }
}
