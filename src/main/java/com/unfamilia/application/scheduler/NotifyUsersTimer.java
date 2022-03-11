package com.unfamilia.application.scheduler;

import com.unfamilia.eggbot.domain.event.Event;
import com.unfamilia.eggbot.domain.player.Player;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.User;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.TimerTask;

@RequiredArgsConstructor(staticName = "of")
public class NotifyUsersTimer extends TimerTask {
    private final GatewayDiscordClient client;
    private final List<Player> players;
    private final Event event;

    @Override
    public void run() {
        players.stream()
                .map(player -> client.getUserById(Snowflake.of(player.getId())))
                .map(Mono::block).filter(Objects::nonNull)
                .map(User::getPrivateChannel)
                .map(Mono::block).filter(Objects::nonNull)
                .forEach(dm -> dm.createMessage(event.getName() + " starts in 30 mins!").subscribe());
    }
}
