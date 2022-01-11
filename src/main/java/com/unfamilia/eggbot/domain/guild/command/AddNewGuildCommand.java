package com.unfamilia.eggbot.domain.guild.command;

import com.unfamilia.application.command.Command;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AddNewGuildCommand implements Command {
    private final Long guildId;
    private final String guildName;
    private final Boolean isOriginGuild;

    public static AddNewGuildCommand of(Long guildId, String guildName) {
        return new AddNewGuildCommand(guildId, guildName, false);
    }

    public Long getGuildId() {
        return guildId;
    }

    public String getGuildName() {
        return guildName;
    }

    public Boolean getOriginGuild() {
        return isOriginGuild;
    }
}
