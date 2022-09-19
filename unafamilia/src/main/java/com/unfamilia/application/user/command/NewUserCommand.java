package com.unfamilia.application.user.command;

import com.unfamilia.application.command.Command;
import com.unfamilia.eggbot.infrastructure.session.SessionToken;

public record NewUserCommand(SessionToken sessionToken, Long wowProfileId, String battleTag) implements Command {

}