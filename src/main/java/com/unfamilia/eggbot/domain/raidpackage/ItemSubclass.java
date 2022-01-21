package com.unfamilia.eggbot.domain.raidpackage;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "item_subclass")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ItemSubclass extends PanacheEntityBase {
    @Id
    @Column(name = "item_subclass_id")
    private Long id;
    private String name;

    public static ItemSubclass findByName(String name) {
        return find("Name", name).firstResult();
    }
}

