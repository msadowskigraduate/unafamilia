package com.unfamilia.application.user.command;

import com.unfamilia.application.command.Command;

public record NewUserCommand(String accessToken, Long discordUserId, Long wowProfileId, String battleTag) implements Command {

}