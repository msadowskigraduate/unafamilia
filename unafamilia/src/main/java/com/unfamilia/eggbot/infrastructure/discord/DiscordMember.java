package com.unfamilia.eggbot.infrastructure.discord;

public record DiscordMember(User user) {
    public record User(
            String id,
            String username,
            String discriminator) {}
}
