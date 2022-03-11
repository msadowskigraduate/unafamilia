package com.unfamilia.eggbot.domain.character;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Table(name = "character", indexes = {
        @Index(columnList = "id")
})
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Character extends PanacheEntityBase {
    @Id
    @Column(name = "id")
    private Long id;
    private String name;
    private Long level;
    private Long characterClass;
    private String characterClassName;
    @Enumerated
    private com.unfamilia.eggbot.infrastructure.wowapi.model.Character.Faction.Type faction;
}
