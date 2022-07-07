package com.unfamilia.application.user.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Order {
    private Long id;
    private Boolean isPaid;
    private Boolean isFulfilled;
    private String date;
    private List<Item> orderItems;

    public static Order from(com.unfamilia.eggbot.domain.raidpackage.Order order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .withZone(ZoneId.systemDefault());
        return Order.builder()
                .id(order.id)
                .isPaid(order.getOrderPaid())
                .isFulfilled(order.getOrderFulfilled())
                .date(formatter.format(order.getOrderDateTime()))
                .orderItems(order.getOrderItems().stream()
                        .map(orderItem -> new Item(orderItem.getItem().getId(), orderItem.getItem().getName(), orderItem.getQuantity(), orderItem.getItem().getMedia()))
                        .collect(toList()))
                .build();
    }

    @Getter
    @AllArgsConstructor
    public static class Item {
        private Long id;
        private String name;
        private Integer quantity;
        private String iconUrl;
    }
}
