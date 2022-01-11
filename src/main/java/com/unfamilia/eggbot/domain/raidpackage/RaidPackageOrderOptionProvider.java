package com.unfamilia.eggbot.domain.raidpackage;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class RaidPackageOrderOptionProvider {

    public List<Item> getAvailableItems() {
        List<Item> fakeItems = List.of(
                getFakeItem(1L, "potion_str", ItemCategory.POTION, 1.0),
                getFakeItem(2L, "potion_int", ItemCategory.POTION, 1.0),
                getFakeItem(3L, "potion_agi", ItemCategory.POTION, 1.0)
                );
        return fakeItems;
    }


    //TODO delete
    private Item getFakeItem(Long id, String name, ItemCategory category, Double price) {
        Item item = new Item();
        item.setName(name);
        item.setItemCategory(category);
        item.id = id;
        item.setItemPrice(price);

        return item;
    }
}
