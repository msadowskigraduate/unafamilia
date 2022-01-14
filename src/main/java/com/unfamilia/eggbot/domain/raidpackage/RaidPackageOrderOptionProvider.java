package com.unfamilia.eggbot.domain.raidpackage;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class RaidPackageOrderOptionProvider {

    public List<Item> getAvailableItems() {
        List<Item> fakeItems = List.of(
                getFakeItem(1L, "potion_str", ItemCategoryOption.POTION, 1.0),
                getFakeItem(2L, "potion_int", ItemCategoryOption.POTION, 1.0),
                getFakeItem(3L, "potion_agi", ItemCategoryOption.POTION, 1.0)
                );
        return fakeItems;
    }


    //TODO delete
    private Item getFakeItem(Long id, String name, ItemCategoryOption category, Double price) {
        Item item = new Item();
        item.setName(name);
        item.setItemCategoryOption(category);
        item.id = id;
        item.setItemPrice(price);

        return item;
    }
}
