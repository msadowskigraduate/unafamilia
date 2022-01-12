package com.unfamilia.eggbot.domain.guild.command;

import com.unfamilia.application.command.Command;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class SetGuildAsOriginCommand implements Command {
    private final Long guildId;
}
