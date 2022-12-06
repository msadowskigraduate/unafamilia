package com.unfamilia.application.user.command;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import com.unfamilia.application.command.Command;
import com.unfamilia.application.command.CommandHandler;
import com.unfamilia.application.command.GenericCommandBusException;
import com.unfamilia.application.user.User;

@ApplicationScoped
public class AddDiscordIdToUserCommandHandler implements CommandHandler<AddDiscordIdToUserCommand> {

    @Override
    public boolean supports(Command command) {
        return command instanceof AddDiscordIdToUserCommand;
    }

    @Override
    @Transactional
    public void handle(AddDiscordIdToUserCommand command) throws GenericCommandBusException {
        var user = User.findByOptionalBattleNetId(command.userId());
        
        user.ifPresent(usr -> {
            usr.setDiscordUserId(command.discordUserId());
            usr.persist();
        });
    }
}
