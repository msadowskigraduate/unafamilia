package com.unfamilia.eggbot.domain.player;

import com.unfamilia.eggbot.domain.character.Character;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Table(name = "player")
@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Player extends PanacheEntityBase {
    @Id
    private Long id;
    private Long discordUserId;
    private String battleTag;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Character> characters;

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Character mainCharacter;

    @OneToMany
    private List<Role> role;

    public Player setMainCharacter(Long characterId) {
        this.characters.stream()
                .filter(x -> x.getId().equals(characterId))
                .findFirst()
                .ifPresent(character -> this.mainCharacter = character);
        return this;
    }

    public Player setBattleTag(String battleTag) {
        this.battleTag = battleTag;
        return this;
    }

    public Player setDiscordUserId(Long id) {
        this.discordUserId = id;
        return this;
    }

    public Player setRoles(List<Role> roles) {
        this.role = roles;
        return this;
    }

    public boolean hasLinkedWithDiscord() {
        return this.discordUserId != null;
    }

    public static Optional<Player> findByDiscordUserId(Long discordUserId) {
        return Optional.ofNullable(Player.<Player>find("discordUserId", discordUserId).firstResult());
    }

    public static List<Player> findByRole(Role role) {
        return Player.<Player>findAll()
                .stream()
                .filter(player -> player.getRole().contains(role))
                .collect(Collectors.toList());
    }
}
