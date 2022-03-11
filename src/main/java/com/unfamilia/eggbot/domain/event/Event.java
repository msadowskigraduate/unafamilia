package com.unfamilia.eggbot.domain.event;

import com.unfamilia.eggbot.domain.character.Character;
import com.unfamilia.eggbot.domain.player.Player;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends PanacheEntity {
    private String name;
    private String activity;
    private String role;
    private LocalDateTime date;
    @OneToOne
    private Player organizer;
    @OneToMany
    private List<Character> characters;

    public static Event of(String name, String activity, String role, LocalDateTime date, Player organizer, List<Character> characters) {
        var event = new Event();
        event.activity = activity;
        event.name = name;
        event.role = role;
        event.date = date;
        event.organizer = organizer;
        event.characters = characters;
        return event;
    }
}
