package com.unfamilia.application.audit;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RosterAuditModel(
        String name,
        String realm,
        @JsonProperty("class_name") String characterClass,
        @JsonProperty("class_id") Integer characterClassId,
        @JsonProperty("average_item_level") Integer averageItemLevel,
        @JsonProperty("equipped_item_level") Integer equippedItemLevel,
        @JsonProperty("dungeons_done") Integer dungeonsDone,
        @JsonProperty("world_quests_done") Integer worldQuestsDone,
        @JsonProperty("highest_keystone_done") Integer highestKeystoneDone,
        @JsonProperty("highest_keystone_done") Integer dungeonOptionOne,
        @JsonProperty("highest_keystone_done") Integer dungeonOptionTwo,
        @JsonProperty("highest_keystone_done") Integer dungeonOptionThree,
        @JsonProperty("highest_keystone_done") Integer raidOptionOne,
        @JsonProperty("highest_keystone_done") Integer raidOptionTwo,
        @JsonProperty("highest_keystone_done") Integer raidOptionThree,
        @JsonProperty("highest_keystone_done") Integer pvpOptionOne,
        @JsonProperty("highest_keystone_done") Integer pvpOptionTwo,
        @JsonProperty("highest_keystone_done") Integer pvpOptionThree) {
}
