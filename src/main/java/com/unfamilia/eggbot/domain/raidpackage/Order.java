package com.unfamilia.eggbot.domain.raidpackage;


import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.List;

public class Order extends PanacheEntity {
    private String orderMessageId;
    private String orderUserId;

    private Boolean orderFulfilled;
    private Boolean orderPaid;
    private Timestamp orderDateTime;
    @OneToMany
    private List<OrderItem> orderItems;
}
