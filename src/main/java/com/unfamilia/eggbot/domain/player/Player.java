package com.unfamilia.eggbot.domain.player;

import com.unfamilia.eggbot.domain.character.Character;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapsId;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "player")
@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Player extends PanacheEntityBase {
    @Id
    private Long id;

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

    public static List<Player> findByRole(Role role) {
        return Player.<Player>findAll()
                .stream()
                .filter(player -> player.getRole().contains(role))
                .collect(Collectors.toList());
    }
}
