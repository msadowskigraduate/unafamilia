package com.unfamilia.eggbot.domain.raidpackage;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "raidpackage_order")
public class Order extends PanacheEntityBase {
    private Long orderMessageId;
    private Long orderUserId;

    private Boolean orderFulfilled;
    private Boolean orderPaid;
    private Instant orderDateTime;
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinTable(name = "order_item_id")
    private List<OrderItem> orderItems = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public static Order of(
            Long orderMessageId,
            Long orderUserId,
            Boolean orderFulfilled,
            Boolean orderPaid,
            Instant orderDateTime,
            List<OrderItem> items) {
        var order = new Order();
        order.setOrderMessageId(orderMessageId);
        order.setOrderUserId(orderUserId);
        order.setOrderFulfilled(orderFulfilled);
        order.setOrderPaid(orderPaid);
        order.setOrderDateTime(orderDateTime);
        order.setOrderItems(items);
        return order;
    }
}
