package com.unfamilia.eggbot.application.raidpackage.domain;


import io.quarkus.hibernate.reactive.panache.PanacheEntity;

import javax.persistence.Entity;

@Entity
public class Item extends PanacheEntity {
    private String name;
    private String slug;
    private ItemCategory itemCategory;
    private Integer maxQuantity;
    private Integer positionId;
}
