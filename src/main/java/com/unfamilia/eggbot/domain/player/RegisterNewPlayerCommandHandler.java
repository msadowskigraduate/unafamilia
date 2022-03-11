package com.unfamilia.eggbot.domain.player;

import com.unfamilia.application.command.Command;
import com.unfamilia.application.command.CommandHandler;
import com.unfamilia.eggbot.domain.character.Character;
import discord4j.core.GatewayDiscordClient;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class RegisterNewPlayerCommandHandler implements CommandHandler {
    @Override
    public boolean supports(Command command) {
        return command instanceof RegisterNewPlayerCommand;
    }

    @Override
    @Transactional
    public void handle(Command command) {
        var registerNewPlayerCommand = (RegisterNewPlayerCommand) command;

        var roles = Optional.ofNullable(registerNewPlayerCommand.getUser().getRoles().buffer().blockFirst())
                .orElse(new ArrayList<>())
                .stream()
                .map(role -> Role.<Role>findByIdOptional(role.getId().asLong())
                        .orElse(
                                Role.of(role.getId().asLong(),
                                        role.getName())
                        )
                )
                .collect(Collectors.toList());

        var characters = registerNewPlayerCommand.getWowProfile().getWowAccounts()
                .stream().flatMap(wowAccount -> wowAccount.getCharacters().stream())
                .map(wowCharacter -> Character.of(
                        wowCharacter.getId(),
                        wowCharacter.getName(),
                        wowCharacter.getLevel(),
                        wowCharacter.getPlayableClass().getId(),
                        wowCharacter.getPlayableClass().getName(),
                        wowCharacter.getFaction().getType()))
                .collect(Collectors.toList());
        Player player = Player.of(
                registerNewPlayerCommand.getUser().getId().asLong(),
                characters,
                null,
                roles
        );
        player.persistAndFlush();
    }
}
