package com.unfamilia.eggbot.domain.raidpackage;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Item extends PanacheEntity {
    private String name;
    private ItemCategory itemCategory;
    private Double itemPrice;
    private String slug;
    private Integer maxAmount;
    private String expansionLevel;


    public void setItemCategory(ItemCategory itemCategory) {
        this.itemCategory = itemCategory;
    }

    public ItemCategory getItemCategory() {
        return itemCategory;
    }
}
