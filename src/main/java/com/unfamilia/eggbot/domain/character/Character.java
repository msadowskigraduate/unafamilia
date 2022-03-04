package com.unfamilia.eggbot.domain.character;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Character extends PanacheEntityBase {
    @Id
    private Long id;
    private String selfUrl;
}
