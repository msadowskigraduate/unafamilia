package com.unfamilia.eggbot.infrastructure.wowapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WowProfile {
    private List<WowAccount> wowAccounts;

    @Data
    @NoArgsConstructor
    public static class WowAccount {
        private String id;
        private List<Character> characters;
    }

    @Data
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Character {
        private String name;
        private String id;
        private Long level;

        @JsonProperty("playable_class")
        private com.unfamilia.eggbot.infrastructure.wowapi.model.Character.CharacterClass playableClass;
        private com.unfamilia.eggbot.infrastructure.wowapi.model.Character.Faction faction;
    }
}
