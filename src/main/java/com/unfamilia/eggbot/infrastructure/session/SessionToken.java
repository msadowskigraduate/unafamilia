package com.unfamilia.eggbot.infrastructure.session;

import discord4j.core.object.entity.User;
import lombok.Getter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
public class SessionToken {
    private static Map<String, SessionToken> cache = new HashMap<>();
    private String token;
    private Long expiresIn;
    private User user;

    private SessionToken(String token, Long expiresIn, User user) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public boolean isValid() {
        return Instant.now().isBefore(Instant.ofEpochSecond(expiresIn));
    }

    public static SessionToken generateForUser(User user) {
        var token = UUID.randomUUID().toString();
        long expiresIn = Instant.now().plusSeconds(60 * 15).getEpochSecond();
        var sessionToken = new SessionToken(token, expiresIn, user);
        cache.put(sessionToken.getToken(), sessionToken);
        return sessionToken;
    }

    public static SessionToken get(String token) {
        return Optional.ofNullable(cache.get(token))
                .orElseThrow(InvalidTokenException::new);
    }
}
