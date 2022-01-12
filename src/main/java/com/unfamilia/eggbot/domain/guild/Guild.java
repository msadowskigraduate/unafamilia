package com.unfamilia.eggbot.domain.guild;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "guild")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Guild extends PanacheEntityBase {
    @Id
    private Long guildId;
    private String guildName;
    private Boolean isOriginGuild;
}
