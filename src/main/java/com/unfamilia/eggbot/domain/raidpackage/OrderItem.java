package com.unfamilia.eggbot.domain.raidpackage;


import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import java.util.List;

@Entity
public class OrderItem extends PanacheEntity {
    private Item itemId;
    @Basic
    private Integer quatity;
    @ManyToMany
    @JoinTable(
            name = "OrderItemItem",
            joinColumns = @JoinColumn(name = "orderItemId", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "itemId", referencedColumnName = "ID")
    )
    private List<Item> items;

    public void addItem(Item item) {
        this.items.add(item);
    }
}

