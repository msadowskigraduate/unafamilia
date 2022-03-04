package com.unfamilia.eggbot.infrastructure.wowapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CharacterMedia {
    @JsonProperty("_links")
    private SelfKey self;
    private Key character;
    private String name;
    private Character.Realm realm;
    private List<Asset> assets;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Asset {
        private String key;
        private String value;
    }
}
