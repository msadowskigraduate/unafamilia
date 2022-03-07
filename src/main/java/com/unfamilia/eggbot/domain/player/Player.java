package com.unfamilia.eggbot.domain.player;

import com.unfamilia.eggbot.domain.character.Character;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Player extends PanacheEntityBase {
    @Id Long id;

    @OneToMany
    List<Character> characters;
}
