package com.unfamilia.eggbot.domain.guild;

import com.unfamilia.eggbot.domain.player.Role;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.ws.rs.Consumes;
import java.util.List;

@Entity
@Table(name = "guild")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Guild extends PanacheEntityBase {
    @Id
    @Column(name = "guild_id")
    private Long guildId;
    private String guildName;
    private Boolean isOriginGuild;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "guild_id")
    private List<Role> roles;
}
