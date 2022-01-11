package com.unfamilia.eggbot.infrastructure.discord.events.handlers;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.eggbot.domain.guild.command.AddNewGuildCommand;
import com.unfamilia.eggbot.domain.raidpackage.channel.RaidPackageChannelProvider;
import com.unfamilia.eggbot.infrastructure.discord.DiscordApplicationProvider;
import com.unfamilia.eggbot.infrastructure.discord.events.discordcommands.DiscordCommandRegistrar;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.TextChannelCreateSpec;
import reactor.core.publisher.Mono;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class OnGuildJoinEventHandler implements EventHandler {

    private final RaidPackageChannelProvider channelProvider;
    private final DiscordCommandRegistrar commandRegistrar;
    private final DiscordApplicationProvider applicationProvider;
    private final CommandBus commandBus;

    public OnGuildJoinEventHandler(RaidPackageChannelProvider channelProviders, DiscordCommandRegistrar commandRegistrar, DiscordApplicationProvider applicationProvider, CommandBus commandBus) {
        this.channelProvider = channelProviders;
        this.commandRegistrar = commandRegistrar;
        this.applicationProvider = applicationProvider;
        this.commandBus = commandBus;
    }

    @Override
    public Boolean supports(Event event) {
        return GuildCreateEvent.class.isAssignableFrom(event.getClass());
    }

    @Override
    public Mono handle(Event event) {
        Guild guild = ((GuildCreateEvent) event).getGuild();
        checkOrCreateOrderConfirmChannel(guild);
        checkOrCreateOrderInitChannel(guild);
        this.commandRegistrar.registerCommands(applicationProvider.getApplicationId(), guild.getId().asLong());
        commandBus.handle(AddNewGuildCommand.of(guild.getId().asLong(), guild.getName()));
        return Mono.empty();
    }

    private void checkOrCreateOrderInitChannel(Guild guild) {
        Optional<GuildChannel> initChannel = guild.getChannels()
                .toStream()
                .filter(channel -> channel.getName().equalsIgnoreCase(channelProvider.getOrderInitName()))
                .findAny();

        if (initChannel.isEmpty()) {
            channelProvider.setInitializeOrderChannel(guild.createTextChannel(
                    TextChannelCreateSpec.builder()
                            .name(channelProvider.getOrderInitName())
                            .nsfw(false)
                            .build()
            ).block());
        } else {
            channelProvider.setInitializeOrderChannel((TextChannel) initChannel.get());
        }
    }

    private void checkOrCreateOrderConfirmChannel(Guild guild) {
        Optional<GuildChannel> confirmedChannel = guild.getChannels()
                .toStream()
                .filter(channel -> channel.getName().equalsIgnoreCase(channelProvider.getOrderConfirmedName()))
                .findAny();

        if (confirmedChannel.isEmpty()) {
            channelProvider.setInitializeOrderChannel(guild.createTextChannel(
                    TextChannelCreateSpec.builder()
                            .name(channelProvider.getOrderConfirmedName())
                            .nsfw(false)
                            .build()
            ).block());
        } else {
            channelProvider.setInitializeOrderChannel((TextChannel) confirmedChannel.get());
        }
    }
}
