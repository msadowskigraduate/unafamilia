package com.unfamilia.application.user;

import java.util.List;

public record UserDto(
   String name,
   Long discordUserId,
   Long battleNetUserId,
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