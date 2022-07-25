package com.unfamilia.eggbot.domain.player.handler;

import com.unfamilia.application.command.CommandHandler;
import com.unfamilia.eggbot.domain.character.Character;
import com.unfamilia.eggbot.infrastructure.wowapi.model.WowProfile;

import java.util.List;
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
                        wowCharacter.getRealm().getSlug(),
                        wowCharacter.getFaction().getType()))
                .collect(Collectors.toList());
    }
}
