package com.unfamilia.application.user;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDto(
   String name,
   @JsonProperty("discord_user_id") Long discordUserId,
   @JsonProperty("battle_net_user_id") Long battleNetUserId,
   Integer rank,
   boolean isAdmin,
   List<CharacterDto> characters
) {
    static record CharacterDto(
        Long id,
        String name,
        String realm
    ) {}
}