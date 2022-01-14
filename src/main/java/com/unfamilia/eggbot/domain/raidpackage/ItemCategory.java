package com.unfamilia.eggbot.domain.raidpackage;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "itemCategory")
public class ItemCategory extends PanacheEntity {
    private ItemCategoryOption categoryName;
}
