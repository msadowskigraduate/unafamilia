package com.unafamilia.order.application;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderItemDto(
        @JsonProperty("item_id") Integer itemId,
        Integer quantity) {
}
