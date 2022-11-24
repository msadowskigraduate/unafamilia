package com.unfamilia.application.user;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Entity(name = "Users")
@Table(name = "unafamilia_users")
public class User extends PanacheEntityBase {
    private String name;

    @Column(name = "discord_user_id")
    private Long discordUserId;

    @Id
    @Column(name = "battlenet_user_id")
    private Long battleNetUserId;
    private Integer rank;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "unafamilia_users_characters")
    private List<Character> characters = new ArrayList<>();

    public static Optional<User> findByOptionalDiscordId(Long discordUserId) {
        return User.find("discord_user_id", discordUserId).singleResultOptional();
    }

    public static Optional<User> findByOptionalBattleNetId(Long battleNetUserId) {
        return User.find("battlenet_user_id", battleNetUserId).singleResultOptional();
    }
}
