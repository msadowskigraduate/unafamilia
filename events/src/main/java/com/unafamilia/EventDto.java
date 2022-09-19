package com.unafamilia;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

record EventDto(
        Date date,
        @JsonProperty("organizer_id") Long organizerId,
        @JsonProperty("event_name") String eventName,
        @JsonProperty("activity_name") String activityName,
        @JsonProperty("drafted_players") List<InviteeDto> draftedPlayers) {
        }
record InviteeDto(@JsonProperty("character_id") Integer characterId, AttendanceStatus status) {}