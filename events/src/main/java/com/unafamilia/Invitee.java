package com.unafamilia;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Invitee extends PanacheEntity {
    @Column(name = "character_id")
    public Integer characterId;

    @Enumerated(EnumType.STRING)
    public AttendanceStatus status;
}
