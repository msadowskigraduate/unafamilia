package com.unfamilia.eggbot.infrastructure.wcl;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WclWebToken {
    private String accessToken;
    private String tokenType;
    private Instant expiresIn;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresIn);
    }

    @JsonProperty("expires_in")
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = Instant.now().plusSeconds(expiresIn);
    }
}
