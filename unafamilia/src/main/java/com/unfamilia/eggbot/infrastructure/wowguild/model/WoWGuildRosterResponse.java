package com.unfamilia.eggbot.infrastructure.wowguild.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WoWGuildRosterResponse(
        @JsonProperty("_link") String link,
        @JsonProperty("name") String name,
        @JsonProperty("id") Integer id,
        @JsonProperty("members") List<Member> members) {

    public record Realm(
            @JsonProperty("slug") String slug,
            @JsonProperty("name") String name,
            @JsonProperty("id") Integer id) {
    }

    public record Member(
            @JsonProperty("name") String name,
            @JsonProperty("id") Long id,
            @JsonProperty("realm_slug") String realm,
            @JsonProperty("level") Integer level,
            @JsonProperty("playable_class") Class playableClass,
            @JsonProperty("race") Race race,
            @JsonProperty("rank") Integer rank) {
    }

    public record Class(
        @JsonProperty("class_name") String className,
        @JsonProperty("class_id") Integer id
    ){}

    public record Race(
        @JsonProperty("name") String name,
        @JsonProperty("id") Integer id
    ){}
}
