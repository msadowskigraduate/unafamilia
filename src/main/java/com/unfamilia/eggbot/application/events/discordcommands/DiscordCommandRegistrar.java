package com.unfamilia.eggbot.application.events.discordcommands;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import java.util.List;

@ApplicationScoped
public class DiscordCommandRegistrar {
    private final Instance<DiscordCommandHandler> commandHandlers;
    private final GatewayDiscordClient gatewayDiscordClient;

    public DiscordCommandRegistrar(Instance<DiscordCommandHandler> commandHandlers, GatewayDiscordClient gatewayDiscordClient) {
        this.commandHandlers = commandHandlers;
        this.gatewayDiscordClient = gatewayDiscordClient;
    }

    void startup(@Observes StartupEvent event) {
        Long applicationId = this.gatewayDiscordClient.getRestClient().getApplicationId().block();
        long guildId = 902943035468423229L;

        if (applicationId == null) {
            Log.warn("Application Id resolved as null! Something smells fishy here...");
        }

        commandHandlers.stream()
                .map(DiscordCommandHandler::build)
                .forEach(request -> gatewayDiscordClient
                        .getRestClient()
                        .getApplicationService()
                        .bulkOverwriteGuildApplicationCommand(applicationId, guildId, List.of(request))
                        .subscribe()
                );
    }
}
