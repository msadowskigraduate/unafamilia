package com.unfamilia.eggbot.domain.character;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Character extends PanacheEntityBase {
    @Id
    private Long id;
    private String name;
    private Long level;
    private Long characterClass;
    private String characterClassName;
    private com.unfamilia.eggbot.infrastructure.wowapi.model.Character.Faction.Type faction;
}
