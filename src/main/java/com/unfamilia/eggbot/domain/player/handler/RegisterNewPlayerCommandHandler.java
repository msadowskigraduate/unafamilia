package com.unfamilia.eggbot.domain.player.handler;

import com.unfamilia.application.command.Command;
import com.unfamilia.eggbot.domain.player.Player;
import com.unfamilia.eggbot.domain.player.command.RegisterNewPlayerCommand;
import com.unfamilia.eggbot.infrastructure.wowapi.WoWProfileApiClient;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
@RequiredArgsConstructor
public class RegisterNewPlayerCommandHandler extends CommonRegisterNewPlayerCommandHandler {
    private final WoWProfileApiClient woWProfileApiClient;

    @Override
    public boolean supports(Command command) {
        return command instanceof RegisterNewPlayerCommand;
    }

    @Override
    @Transactional
    public void handle(Command command) {
        var registerNewPlayerCommand = (RegisterNewPlayerCommand) command;
        if(Player.findByIdOptional(registerNewPlayerCommand.getWowProfileId()).isEmpty()) {
            var wowProfile = woWProfileApiClient.queryWowProfile(registerNewPlayerCommand.getAccessToken());
            var wowProfileId = ((RegisterNewPlayerCommand) command).getWowProfileId();
            var characters = listCharactersFromWoWProfile(wowProfile);
            Player player = Player.of(
                    wowProfileId,
                    null,
                    registerNewPlayerCommand.getBattleTag(),
                    characters,
                    null,
                    null
            );
            player.persistAndFlush();
        }
    }

}
