package com.unafamilia;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class Event extends PanacheEntityBase {
    @Id
    @Column(name = "event_id")
    UUID id;

    @Column(name = "date")
    Date date;

    @Column(name = "organizer_id")
    Long organizerId;

    @Column(name = "event_name")
    String eventName;

    @Column(name = "activity_name")
    String activityName;

    @OneToMany
    @JoinColumn(name = "event_id")
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL})
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<Invitee> draftedPlayers;

    private Event(UUID id) {
        this.id = id;
    }

    protected Event() {}

    public static Event create(Date date, Long organizerId, String eventName, String activityName, List<Invitee> draftedPlayersIds) {
        var event = new Event(UUID.randomUUID());
        event.date = date;
        event.organizerId = organizerId;
        event.eventName = eventName;
        event.activityName = activityName;
        event.draftedPlayers = draftedPlayersIds;
        return event;
    }
}
