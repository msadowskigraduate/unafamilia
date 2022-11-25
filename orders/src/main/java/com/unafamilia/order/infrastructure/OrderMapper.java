package com.unafamilia.order.infrastructure;

import com.unafamilia.order.Order;
import com.unafamilia.order.OrderItem;
import com.unafamilia.order.OrderStatus;
import com.unafamilia.order.application.OrderDto;
import com.unafamilia.order.application.OrderItemDto;
import com.unafamilia.order.application.QueryDto;

import javax.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderMapper {
    public Order from(UUID id, OrderDto dto) {
        var order = new Order();
        order.setOrderId(id);
        order.setWowUserId(dto.wowUserId());
        order.setDiscordUserId(dto.discordUserId());
        order.setOrderMessageId(dto.discordMessageId());
        order.setOrderStatus(OrderStatus.NEW);
        order.setDeliverToCharacter(dto.characterName());
        order.setOrderItems(
                dto.items().stream()
                        .map(item -> {
                            var orderItem = new OrderItem();
                            orderItem.setItemId(item.itemId());
                            orderItem.setQuantity(item.quantity());
                            return orderItem;
                        })
                        .collect(Collectors.toList())
        );
        order.setOrderDateTime(Instant.now());

        return order;
    }

    public QueryDto from(Order order) {
        return new QueryDto(
                order.getOrderId(),
                order.getOrderStatus().toString(),
                order.getOrderMessageId(),
                order.getDiscordUserId(),
                order.getWowUserId(),
                order.getDeliverToCharacter(),
                order.getAmountDue(),
                order.getOrderItems().stream().map(
                        item -> new OrderItemDto(item.getItemId(), item.getQuantity())
                ).collect(Collectors.toList()),
                order.getOrderDateTime().toString()
        );
    }
}
