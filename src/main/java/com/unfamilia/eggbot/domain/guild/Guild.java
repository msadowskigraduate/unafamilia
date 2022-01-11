package com.unfamilia.eggbot.domain.guild;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "guild")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Guild extends PanacheEntity {
    @Id
    private Long guildId;
    private String guildName;
    private Boolean isOriginGuild;
}
