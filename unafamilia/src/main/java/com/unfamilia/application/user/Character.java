package com.unfamilia.application.user;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Character")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "characters")
public class Character extends PanacheEntityBase {
    @Id public Long id;
    public String name;
    public String realm;
}
