package com.unfamilia.eggbot.domain.event;

import com.unfamilia.application.query.Query;
import com.unfamilia.application.user.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class EventQuery implements Query<Event> {
    private final String name;
    private final String activity;
    private final List<Role> roles;
    private final LocalDateTime date;
    private final Long organizingUserId;
}