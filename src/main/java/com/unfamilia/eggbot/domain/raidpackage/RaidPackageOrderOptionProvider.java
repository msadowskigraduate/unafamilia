package com.unfamilia.eggbot.domain.raidpackage;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class RaidPackageOrderOptionProvider {

    public List<Item> getAvailableItems() {
        List<Item> fakeItems = List.of(
                getFakeItem(1L, "Potion of Str", ItemCategory.POTION, 1.0),
                getFakeItem(2L, "Potion of Int", ItemCategory.POTION, 1.0),
                getFakeItem(3L, "Potion of Agi", ItemCategory.POTION, 1.0)
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
