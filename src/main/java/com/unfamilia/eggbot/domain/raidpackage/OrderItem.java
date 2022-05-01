package com.unfamilia.eggbot.domain.raidpackage;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_item")
public class OrderItem extends PanacheEntity {
    private Integer quantity;

    @OneToOne
    private Item item;

    public static OrderItem of(
            Item item,
            Integer quantity) {
        var orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setQuantity(quantity);
        return orderItem;
    }

}



