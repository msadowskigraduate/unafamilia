package com.unfamilia.eggbot.infrastructure.session;

import discord4j.core.object.entity.Member;
import lombok.Getter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.unfamilia.eggbot.infrastructure.session.SessionToken.NullSessionToken.nullToken;

@Getter
public class SessionToken {
    private static Map<String, SessionToken> cache = new HashMap<>();
    private String token;
    private Long expiresIn;
    private Member user;

    private SessionToken(String token, Long expiresIn, Member user) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public boolean isValid() {
        return Instant.now().isBefore(Instant.ofEpochSecond(expiresIn));
    }

    public static SessionToken generateForUser(Member member) {
        var token = UUID.randomUUID().toString();
        long expiresIn = Instant.now().plusSeconds(60 * 15).getEpochSecond();
        var sessionToken = new SessionToken(token, expiresIn, member);
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
