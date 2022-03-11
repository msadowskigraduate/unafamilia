package com.unfamilia.eggbot.domain.guild.command;

import com.unfamilia.application.command.Command;
import discord4j.core.object.entity.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class AddNewGuildCommand implements Command {
    private final Long guildId;
    private final String guildName;
    private final Boolean isOriginGuild;
    private final List<Role> roles;
}
