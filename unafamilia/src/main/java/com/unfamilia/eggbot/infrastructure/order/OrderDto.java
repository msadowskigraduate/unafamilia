package com.unfamilia.eggbot.infrastructure.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OrderDto(
        @JsonProperty("discord_message_id") Long discordMessageId,
        @JsonProperty("discord_user_id") Long discordUserId,
        @JsonProperty("wow_user_id") Long wowUserId,
        @JsonProperty("character_name") String characterName,
        List<OrderItemDto> items
) {}
