package com.unfamilia.eggbot.domain.event;

import com.unfamilia.application.query.QueryHandler;
import com.unfamilia.application.scheduler.NotifyUsersTimer;
import com.unfamilia.eggbot.domain.player.Player;
import com.unfamilia.eggbot.domain.player.Role;
import discord4j.core.GatewayDiscordClient;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;

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
        var role = Role.<Role>find("name", query.getRole()).firstResult();
        var players = Player.findByRole(role);
        var organizer = Player.findByDiscordUserId(query.getOrganizingUserId()).get();
        var characters = players.stream()
                .map(Player::getMainCharacter)
                .collect(Collectors.toList());

        var event = Event.of(
                query.getName(),
                query.getActivity(),
                query.getRole(),
                query.getDate(),
                organizer,
                characters
        );

        event.persist();
        scheduleTask(players, event);
        return event;
    }

    private void scheduleTask(List<Player> playerList, Event event) {
        Date notificationTime = Date.from(
                event.getDate()
                        .minusMinutes(30)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
        var scheduledTask = NotifyUsersTimer.of(client, playerList, event);
        new Timer().schedule(scheduledTask, notificationTime);
    }
}
