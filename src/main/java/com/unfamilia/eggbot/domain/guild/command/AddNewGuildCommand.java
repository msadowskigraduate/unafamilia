package com.unfamilia.eggbot.domain.guild.command;

import com.unfamilia.application.command.Command;

public class AddNewGuildCommand implements Command {
    private final Long guildId;
    private final String guildName;
    private final Boolean isOriginGuild;

    private AddNewGuildCommand(Long guildId, String guildName, Boolean isOriginGuild) {
        this.guildId = guildId;
        this.guildName = guildName;
        this.isOriginGuild = isOriginGuild;
    }

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
