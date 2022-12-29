package com.unfamilia.eggbot.infrastructure.reporter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuditReport(
        @JsonProperty("year") Integer year,
        @JsonProperty("week") Integer week,
        @JsonProperty("characters") List<Character> characters) {
    public static record Character(
            @JsonProperty("id") Integer id,
            @JsonProperty("name") String name,
            @JsonProperty("realm") String realm,
            @JsonProperty("data") Data data) {
        public static record Data(
                @JsonProperty("dungeons_done") Integer dungeonsDone,
                @JsonProperty("world_quests_done") Integer worldQuestsDone,
                @JsonProperty("highest_keystone_done") Integer highestKeystoneDone,
                @JsonProperty("vault_options") VaultOptions vaultOptions) {
            public static record VaultOptions(
                    @JsonProperty("raids") Option raids,
                    @JsonProperty("dungeons") Option dungeons,
                    @JsonProperty("pvp") Option pvp) {
                public static record Option(
                        @JsonProperty("option_1") Integer option1,
                        @JsonProperty("option_2") Integer option2,
                        @JsonProperty("option_3") Integer option3) {
                }
            }
        }
    }
}
