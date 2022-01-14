package com.unfamilia.eggbot.domain.raidpackage;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.ManyToOne;

@Data
@EqualsAndHashCode(callSuper = false)
public class Item extends PanacheEntity {
    private String name;
    @ManyToOne(optional = false)
    private ItemCategoryOption itemCategory;
    private Double itemPrice;
    private String slug;
    private Integer maxAmount;
    private String expansionLevel;
    private ItemCategoryOption itemCategoryOption;

    public void setItemCategoryOption(ItemCategoryOption itemCategoryOption) {
        this.itemCategoryOption = itemCategoryOption;
    }

    public ItemCategoryOption getItemCategoryOption() {
        return itemCategoryOption;
    }
}
