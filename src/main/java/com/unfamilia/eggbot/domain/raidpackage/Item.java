package com.unfamilia.eggbot.domain.raidpackage;


import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Item extends PanacheEntityBase {
    @Id
    @Column(name = "order_item_id")
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_subclass_id")
    private ItemSubclass itemSubclass;

    private Double itemPrice;
    private String slug;
    private Integer maxAmount;

    public void setItemSubclass(ItemSubclass itemSubclass) {
        this.itemSubclass = itemSubclass;
    }

    public ItemSubclass getItemSubclass() {
        return itemSubclass;
    }

    public static Item findByName(String slug) {
        return find("slug", slug).firstResult();
    }
}
