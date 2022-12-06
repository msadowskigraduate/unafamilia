package com.unfamilia.eggbot.infrastructure.wowguild.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CharacterProfileResponse(
    Integer id,
    String name,
    String gender,
    String faction,
    Race race,
    Realm realm,
    @JsonProperty("character_class") CharacterClass characterClass,
    @JsonProperty("active_spec") ActiveSpec activeSpec,
    Integer level,
    @JsonProperty("achievement_points") Integer achievementPoints,
    @JsonProperty("average_item_level") Integer averageItemLevel,
    @JsonProperty("equipped_item_level") Integer equippedItemLevel
) {

    record Realm(
        String name,
        String slug
    ){}

    record Race(
        Integer id,
        String name
    ){}

    record CharacterClass(
        @JsonProperty("class_name") String characterClass,
        @JsonProperty("class_id") Integer characterClassId
    ){}

    record ActiveSpec(
        @JsonProperty("name") String activeSpec,
        @JsonProperty("id") Integer activeSpecId
    ){}
}