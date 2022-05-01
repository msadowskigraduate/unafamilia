package com.unfamilia.eggbot.domain.raidpackage;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "raid_package_order")
@NoArgsConstructor
public class Order extends PanacheEntity {
    private Long orderMessageId;
    private Long orderUserId;
    private Boolean orderFulfilled;
    private Boolean orderPaid;
    private Instant orderDateTime;

    @OneToMany
    private List<OrderItem> orderItems = new ArrayList<>();

    public static Order of(
            Long orderMessageId,
            Long orderUserId,
            Boolean orderFulfilled,
            Boolean orderPaid,
            Instant orderDateTime,
            List<OrderItem> orderItems) {
        var order = new Order();
        order.setOrderMessageId(orderMessageId);
        order.setOrderUserId(orderUserId);
        order.setOrderFulfilled(orderFulfilled);
        order.setOrderPaid(orderPaid);
        order.setOrderDateTime(orderDateTime);
        order.setOrderItems(orderItems);
        return order;
    }

    public static List<Order> findAllOrdersForDiscordUser(Long orderUserId) {
        if(orderUserId == null) return null;
        return find("orderUserId", orderUserId).list();
    }
}
