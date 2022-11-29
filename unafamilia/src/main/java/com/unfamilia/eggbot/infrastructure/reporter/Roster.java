package com.unfamilia.eggbot.infrastructure.reporter;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Roster(
    int id,
    String name,
    String realm,
    @JsonProperty("class") String myclass,
    String role,
    String rank,
    String status,
    Object note,
    int blizzard_id,
    Date tracking_since
) {}
