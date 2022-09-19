package com.unfamilia.application.user.command;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import com.unfamilia.application.command.Command;
import com.unfamilia.application.command.CommandHandler;
import com.unfamilia.application.user.User;

import io.quarkus.logging.Log;

@ApplicationScoped
public class NewUserCommandHandler implements CommandHandler {

    @Override
    public boolean supports(Command command) {
        return command instanceof NewUserCommand;
    }

    /* (non-Javadoc)
     * @see com.unfamilia.application.command.CommandHandler#handle(com.unfamilia.application.command.Command)
     * Creates new User record.
     */
    @Override
    @Transactional
    public void handle(Command command) {
        var newUserCommand = (NewUserCommand) command;

        Log.info("Creating user: " + newUserCommand.battleTag());
        var user = new User();
        user.setBattleNetUserId(newUserCommand.wowProfileId());
        user.setName(newUserCommand.battleTag());
        user.setDiscordUserId(newUserCommand.sessionToken().getUserId());
        user.persist();
    }
}
