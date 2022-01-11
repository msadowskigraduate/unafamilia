package com.unfamilia.eggbot.domain.guild;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "guild")
public class Guild extends PanacheEntity {
    @Id
    private Long guildId;
    private String guildName;
    private Boolean isOriginGuild;

    public Guild() {}

    public Guild(Long guildId, String guildName, Boolean isOriginGuild) {
        this.guildId = guildId;
        this.guildName = guildName;
        this.isOriginGuild = isOriginGuild;
    }

    public Long getGuildId() {
        return guildId;
    }

    public void setGuildId(Long guildId) {
        this.guildId = guildId;
    }

    public String getGuildName() {
        return guildName;
    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }
}
