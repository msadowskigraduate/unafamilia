package com.unfamilia.eggbot.domain.raidpackage.command;

import com.unfamilia.application.command.Command;

import java.util.List;

public class NewOrderCommand implements Command {
    private final Long orderMessageId;
    private final Long orderingUserId;
    private final List<OrderedItem> orderedItems;

    public NewOrderCommand(Long orderMessageId, Long orderingUserId, List<OrderedItem> orderedItems) {
        this.orderMessageId = orderMessageId;
        this.orderingUserId = orderingUserId;
        this.orderedItems = orderedItems;
    }

    public Long getOrderMessageId() {
        return orderMessageId;
    }

    public Long getOrderingUserId() {
        return orderingUserId;
    }

    public List<OrderedItem> getOrderedItems() {
        return orderedItems;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static class OrderedItem {
        private final String itemName;
        private final Integer quantity;

        public OrderedItem(String itemName, Integer quantity) {
            this.itemName = itemName;
            this.quantity = quantity;
        }

        public String getItemName() {
            return itemName;
        }

        public Integer getQuantity() {
            return quantity;
        }
    }
}
