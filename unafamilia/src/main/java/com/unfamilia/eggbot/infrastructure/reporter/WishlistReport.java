package com.unfamilia.eggbot.infrastructure.reporter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WishlistReport(
    @JsonProperty("user_name") String name,
    @JsonProperty("name") String characterName,
    @JsonProperty("realm") String realm,
    @JsonProperty("discord_user_id") Integer discordUserId,
    @JsonProperty("battle_net_user_id") Integer battleNetUserId,
    @JsonProperty("rank") Integer rank,
    @JsonProperty("error") String error,
    @JsonProperty("issues") List<Issue> issues
) {}

record Issue(
    @JsonProperty("reason") String reason,
    @JsonProperty("timestamp") String timestamp,
    @JsonProperty("item") String item,
    @JsonProperty("instance_name") String instanceName,
    @JsonProperty("wishlist_name") String wishlistName,
    @JsonProperty("difficulty") String difficulty
) {}