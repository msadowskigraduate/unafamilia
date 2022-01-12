package com.unfamilia.eggbot.infrastructure.discord.events.discordcommands;

import discord4j.core.GatewayDiscordClient;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class DiscordCommandRegistrar {
    private final Instance<DiscordCommandHandler> commandHandlers;
    private final GatewayDiscordClient gatewayDiscordClient;

    public void registerCommands(long applicationId, long guildId) {
        gatewayDiscordClient
                .getRestClient()
                .getApplicationService()
                .bulkOverwriteGuildApplicationCommand(
                        applicationId,
                        guildId,
                        commandHandlers.stream().map(DiscordCommandHandler::build).collect(Collectors.toList()))
                .subscribe();
    }
}
