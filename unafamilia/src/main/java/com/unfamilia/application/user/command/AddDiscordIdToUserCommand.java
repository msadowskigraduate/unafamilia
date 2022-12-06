package com.unfamilia.application.user.command;

import com.unfamilia.application.command.Command;

public record AddDiscordIdToUserCommand(
    Long userId,
    Long discordUserId
) implements Command {}
