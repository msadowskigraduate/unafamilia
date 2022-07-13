package com.unfamilia.application.event;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.ZoneOffset;
import java.util.Date;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Event {
    private String title;
    private String activity;
    private Date date;
    private String organizerName;
    private final String backgroundUrl = "https://tanknotes.com/storage/uploads/2022/02/28/621d2e0075e87sepulcher-of-the-first-ones-small.jpg";

    public static Event from(com.unfamilia.eggbot.domain.event.Event event) {
        return Event.builder()
                .title(event.getName())
                .activity(event.getActivity())
                .organizerName(event.getOrganizer().getBattleTag())
                .date(Date.from(event.getDate().toInstant(ZoneOffset.UTC)))
                .build();
    }
}
