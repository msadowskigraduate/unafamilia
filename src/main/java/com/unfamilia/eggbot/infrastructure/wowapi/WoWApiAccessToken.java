package com.unfamilia.eggbot.infrastructure.wowapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
class WoWApiAccessToken {
    private String accessToken;
    private String tokenType;
    private Instant expiresIn;
    private String sub;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresIn);
    }

    @JsonProperty("expires_in")
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = Instant.now().plusSeconds(expiresIn);
    }
}
