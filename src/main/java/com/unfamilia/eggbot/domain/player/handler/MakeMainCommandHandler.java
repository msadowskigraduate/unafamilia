package com.unfamilia.eggbot.domain.player.handler;

import com.unfamilia.application.command.Command;
import com.unfamilia.application.command.CommandHandler;
import com.unfamilia.eggbot.domain.player.Player;
import com.unfamilia.eggbot.domain.player.command.MakeMainCommand;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class MakeMainCommandHandler implements CommandHandler {
    @Override
    public boolean supports(Command command) {
        return command instanceof MakeMainCommand;
    }

    @Override
    @Transactional
    public void handle(Command command) {
        MakeMainCommand makeMain = (MakeMainCommand) command;
        Optional<Player> optionalPlayer = Player.findByDiscordUserId(makeMain.getUser().getId().asLong());
        if(optionalPlayer.isEmpty()) {
            throw new NotFoundException("No user with Id: " + makeMain.getUser().getId().asLong() + " was found!");
        }

        optionalPlayer.get().getCharacters()
                .stream()
                .filter(character -> character.getId().equals(makeMain.getCharacterId()))
                .findAny()
                .ifPresentOrElse(character -> optionalPlayer.get().setMainCharacter(character.getId()).persist(),
                        () -> handleCharacterNotTiedToPlayer(makeMain.getCharacterId()));
    }

    private void handleCharacterNotTiedToPlayer(Long characterId) {
        throw new NotFoundException("Character with ID: " + characterId + " not found!");
    }
}
