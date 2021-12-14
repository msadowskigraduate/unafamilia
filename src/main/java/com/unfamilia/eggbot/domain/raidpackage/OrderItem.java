package com.unfamilia.eggbot.domain.raidpackage;


import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class OrderItem extends PanacheEntity {
    @OneToOne
    private Item itemId;
    private Integer quatity;
}
