package com.unfamilia.eggbot.domain.player.handler;

import com.unfamilia.application.command.Command;
import com.unfamilia.eggbot.domain.character.Character;
import com.unfamilia.eggbot.domain.player.Player;
import com.unfamilia.eggbot.domain.player.Role;
import com.unfamilia.eggbot.domain.player.command.RegisterNewPlayerFromDiscordCommand;
import com.unfamilia.eggbot.infrastructure.wowapi.WoWProfileApiClient;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class RegisterNewPlayerFromDiscordCommandHandler extends CommonRegisterNewPlayerCommandHandler {
    private final WoWProfileApiClient woWProfileApiClient;

    @Override
    public boolean supports(Command command) {
        return command instanceof RegisterNewPlayerFromDiscordCommand;
    }

    @Override
    @Transactional
    public void handle(Command command) {
        var registerNewPlayerCommand = (RegisterNewPlayerFromDiscordCommand) command;
        var player = Player.<Player>findByIdOptional(registerNewPlayerCommand.getWowProfileId());

        if(player.isPresent()) {
            Long discordUserId = registerNewPlayerCommand.getSessionToken().getUser().getId().asLong();
            List<Role> roles = getDiscordUserRoles(registerNewPlayerCommand.getSessionToken().getUser());
            updatePlayerInformation(player.get(), discordUserId, roles);
            return;
        }

        var wowProfile = woWProfileApiClient.queryWowProfile(registerNewPlayerCommand.getAccessToken());
        var wowProfileId = registerNewPlayerCommand.getWowProfileId();
        var roles = getDiscordUserRoles(registerNewPlayerCommand.getSessionToken().getUser());
        var characters = listCharactersFromWoWProfile(wowProfile);

        persistNewUser(
                wowProfileId,
                registerNewPlayerCommand.getSessionToken().getUser().getId().asLong(),
                registerNewPlayerCommand.getBattleTag(),
                characters,
                roles);
    }

    private void persistNewUser(Long wowProfileId, long discordUserId, String battleTag, List<Character> characters, List<Role> roles) {
        Player player = Player.of(
                wowProfileId,
                discordUserId,
                battleTag,
                characters,
                null,
                roles
        );
        player.persist();
    }

    private void updatePlayerInformation(Player player, Long discordUserId, List<Role> roleList) {
        player
                .setDiscordUserId(discordUserId)
                .setRoles(roleList)
                .persist();
    }
}
