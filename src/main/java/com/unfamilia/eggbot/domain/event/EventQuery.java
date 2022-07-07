package com.unfamilia.eggbot.domain.event;

import com.unfamilia.application.query.Query;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class EventQuery implements Query<Event> {
    private final String name;
    private final String activity;
    private final String role;
    private final LocalDateTime date;
    private final Long organizingUserId;
}