package com.unfamilia.eggbot.domain.player.handler;

import com.unfamilia.application.command.CommandHandler;
import com.unfamilia.eggbot.domain.character.Character;
import com.unfamilia.eggbot.domain.player.Role;
import com.unfamilia.eggbot.domain.player.command.RegisterNewPlayerFromDiscordCommand;
import com.unfamilia.eggbot.infrastructure.wowapi.model.WowProfile;
import discord4j.core.object.entity.Member;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

abstract class CommonRegisterNewPlayerCommandHandler implements CommandHandler {

    protected List<Character> listCharactersFromWoWProfile(WowProfile wowProfile) {
        return wowProfile.getWowAccounts()
                .stream().flatMap(wowAccount -> wowAccount.getCharacters().stream())
                .map(wowCharacter -> Character.of(
                        wowCharacter.getId(),
                        wowCharacter.getName(),
                        wowCharacter.getLevel(),
                        wowCharacter.getPlayableClass().getId(),
                        wowCharacter.getPlayableClass().getName(),
                        wowCharacter.getFaction().getType()))
                .collect(Collectors.toList());
    }

    protected List<Role> getDiscordUserRoles(Member member) {
        var roles = Optional.ofNullable(member.getRoles().buffer().blockFirst());
        return roles.map(roleList -> roleList.stream()
                .map(role -> Role.<Role>findByIdOptional(role.getId().asLong())
                        .orElse(
                                Role.of(role.getId().asLong(),
                                        role.getName())
                        )
                )
                .collect(Collectors.toList())).orElse(null);
    }
}
