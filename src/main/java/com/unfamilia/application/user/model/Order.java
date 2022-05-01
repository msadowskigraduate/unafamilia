package com.unfamilia.application.user.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Order {
    private Long id;
    private Boolean isPaid;
    private Boolean isFulfilled;
    private List<Item> orderItems;

    public static Order from(com.unfamilia.eggbot.domain.raidpackage.Order order) {
        return Order.builder()
                .id(order.id)
                .isPaid(order.getOrderPaid())
                .isFulfilled(order.getOrderFulfilled())
                .orderItems(order.getOrderItems().stream()
                        .map(orderItem -> new Item(orderItem.getItem().getSlug(), orderItem.getQuantity(), orderItem.getItem().getMedia()))
                        .collect(toList()))
                .build();
    }

    @Getter
    @AllArgsConstructor
    public static class Item {
        private String name;
        private Integer quantity;
        private String iconUrl;
    }
}
