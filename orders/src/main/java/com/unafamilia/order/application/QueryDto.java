package com.unafamilia.order.application;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QueryDto(UUID orderId, String orderStatus, Long discordMessageId, Long discordUserId, Long wowUserId, String characterName, Long amountDue,
                       List<OrderItemDto> items, @JsonProperty("order_date_time") String orderDateTime) {
}
