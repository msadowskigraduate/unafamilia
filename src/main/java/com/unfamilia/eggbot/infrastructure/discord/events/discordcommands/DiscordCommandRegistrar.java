package com.unfamilia.eggbot.infrastructure.discord.events.discordcommands;

import discord4j.core.GatewayDiscordClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import java.util.stream.Collectors;

@ApplicationScoped
public class DiscordCommandRegistrar {
    private final Instance<DiscordCommandHandler> commandHandlers;
    private final GatewayDiscordClient gatewayDiscordClient;

    public DiscordCommandRegistrar(Instance<DiscordCommandHandler> commandHandlers, GatewayDiscordClient gatewayDiscordClient) {
        this.commandHandlers = commandHandlers;
        this.gatewayDiscordClient = gatewayDiscordClient;
    }

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
