package com.unfamilia.eggbot.infrastructure.session;

import lombok.Getter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
public class SessionToken {
    private static final Map<String, SessionToken> cache = new HashMap<>();
    private final String token;
    private final Long expiresIn;
    private final Long userId;

    private SessionToken(String token, Long expiresIn, Long user) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.userId = user;
    }

    public boolean isValid() {
        return Instant.now().isBefore(Instant.ofEpochSecond(expiresIn));
    }

    public static SessionToken generateForUser(Long userId) {
        var token = UUID.randomUUID().toString();
        long expiresIn = Instant.now().plusSeconds(60 * 15).getEpochSecond();
        var sessionToken = new SessionToken(token, expiresIn, userId);
        cache.put(sessionToken.getToken(), sessionToken);
        return sessionToken;
    }

    public static SessionToken get(String token) {
        if(token == null) return NullSessionToken.nullToken();
        return Optional.ofNullable(cache.get(token))
                .orElseThrow(InvalidTokenException::new);
    }

    public static class NullSessionToken extends SessionToken {
        private NullSessionToken() {
            super(null, null, null);
        }

        @Override
        public boolean isValid() {
            return false;
        }

        protected static SessionToken nullToken() {
            return new NullSessionToken();
        }
    }
}
