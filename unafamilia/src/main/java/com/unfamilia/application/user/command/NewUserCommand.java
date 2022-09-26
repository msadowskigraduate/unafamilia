package com.unfamilia.application.user.command;

import com.unfamilia.application.command.Command;
import com.unfamilia.eggbot.infrastructure.session.SessionToken;

public record NewUserCommand(String accessToken, Long discordUserId, Long wowProfileId, String battleTag) implements Command {

}