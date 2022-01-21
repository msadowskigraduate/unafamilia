package com.unfamilia.eggbot.domain.raidpackage;


import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Table(name = "order_item")
public class OrderItem extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;
    private Integer quantity;

    @OneToOne
    private Item item;

    public static OrderItem of(Integer quantity, Item item) {
        var orderItem = new OrderItem();
        orderItem.setQuantity(quantity);
        orderItem.setItem(item);
        return orderItem;
    }
}