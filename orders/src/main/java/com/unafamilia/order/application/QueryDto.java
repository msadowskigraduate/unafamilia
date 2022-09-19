package com.unafamilia.order.application;

import java.util.List;
import java.util.UUID;

public record QueryDto(UUID orderId, String orderStatus, Long discordMessageId, Long discordUserId, Long wowUserId, String characterName, Long amountDue,
                       List<OrderItemDto> items) {
}
