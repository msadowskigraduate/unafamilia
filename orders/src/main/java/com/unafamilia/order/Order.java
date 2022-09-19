package com.unafamilia.order;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "raid_package_order")
@NoArgsConstructor
public class Order extends PanacheEntityBase {
    @Id private UUID orderId;
    private Long orderMessageId;
    private Long wowUserId;
    private Long discordUserId;
    private String deliverToCharacter;
    private Long amountDue;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private Instant orderDateTime;

    @OneToMany
    private List<OrderItem> orderItems = new ArrayList<>();

    public static List<Order> findAllOrdersForDiscordUser(Long wowUserId) {
        if(wowUserId == null) return null;
        return list("wowUserId", Sort.ascending("id"), wowUserId);
    }

    public static List<Order> findAllOrderForUser(Long discordUserId) {
        if(discordUserId == null) return null;
        return list("discordUserId", Sort.ascending("id"), discordUserId);
    }

    public Order paid() {
        if(orderStatus == OrderStatus.NEW) {
            orderStatus = OrderStatus.PAID;
        }

        return this;
    }

    public Order cancel() {
        if(orderStatus == OrderStatus.NEW) {
            orderStatus = OrderStatus.CANCELLED;
        }

        return this;
    }

    public Order fulfill() {
        if(orderStatus == OrderStatus.PAID) {
            orderStatus = OrderStatus.FULFILLED;
        }

        return this;
    }
}
