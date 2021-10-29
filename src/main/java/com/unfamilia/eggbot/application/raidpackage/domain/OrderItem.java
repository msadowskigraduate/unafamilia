package com.unfamilia.eggbot.application.raidpackage.domain;


import io.quarkus.hibernate.reactive.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class OrderItem extends PanacheEntity {
    @OneToOne
    private Item itemId;
    private Integer quatity;
}
