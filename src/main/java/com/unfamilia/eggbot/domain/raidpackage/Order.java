package com.unfamilia.eggbot.domain.raidpackage;


import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Order extends PanacheEntity {
    private String orderMessageId;
    private String orderUserId;

    @OneToMany
    private List<OrderItem> orderItems;
}
