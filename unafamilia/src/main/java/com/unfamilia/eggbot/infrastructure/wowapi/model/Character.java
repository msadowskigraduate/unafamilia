package com.unfamilia.eggbot.infrastructure.wowapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Character {
    private Long id;
    private String name;
    private Gender gender;
    private Race race;
    private CharacterClass characterClass;
    private ActiveSpec activeSpec;
    private Realm realm;
    private Guild guild;
    private Integer level;
    private Integer achievementPoints;
    private Integer averageItemLevel;
    private Integer equippedItemLevel;

    @Data
    @NoArgsConstructor
    public static class Gender {
        private Type type;
        private String name;

        public enum Type {
            MALE,
            FEMALE
        }
    }

    @Data
    @NoArgsConstructor
    public static class Faction {
        private Type type;
        private String name;

        public enum Type {
            ALLIANCE,
            HORDE
        }
    }

    @Data
    @NoArgsConstructor
    public static class Race {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    public static class CharacterClass {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    public static class ActiveSpec {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    public static class Realm {
        private Long id;
        private String name;
        private String slug;
    }

    @Data
    @NoArgsConstructor
    public static class Guild {
        private Long id;
        private String name;
        private Realm realm;
        private Faction faction;
    }
}
