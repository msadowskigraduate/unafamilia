package com.unfamilia.application.order;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.eggbot.domain.player.Player;
import com.unfamilia.eggbot.domain.raidpackage.Order;
import com.unfamilia.eggbot.domain.raidpackage.command.NewOrderCommand;
import io.quarkus.security.Authenticated;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Authenticated
@Path("/{tenant_id}/{userId}/order")
@RequiredArgsConstructor
public class OrderController {
    private final CommandBus commandBus;

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    public Response markOrderAsPaid(OrderPatch orderPatch, @PathParam("userId") Long userId) {
        var user = Player.<Player>findByIdOptional(userId);
        user.flatMap(player -> Order.findAllOrderForUser(player.getId()).stream()
                .filter(order -> order.getOrderId().equals(orderPatch.getOrderId()))
                .findFirst()).ifPresent(order -> {
                    if(orderPatch.isPaid != null) {
                        order.setOrderPaid(orderPatch.getIsPaid());
                    }
                    if(orderPatch.isFulfilled != null) {
                        order.setOrderFulfilled(orderPatch.isFulfilled);
                    }
        });
        return Response.ok().build();
    }

    @GET
    @Path("/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryOrder( @PathParam("userId") Long userId, @PathParam("orderId") Long orderId) {
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newOrder(OrderPayload orderPayload, @PathParam("userId") Long userId) {
        var user = Player.<Player>findByIdOptional(userId);
        var orderId = UUID.randomUUID();
        user.ifPresent(player -> commandBus.handle(
                new NewOrderCommand(
                        null,
                        player.getDiscordUserId(),
                        orderId,
                        orderPayload.getItems().stream()
                                .map(item -> new NewOrderCommand.OrderedItem(item.item_id(), item.quantity()))
                                .collect(Collectors.toList())
                )
            )
        );

        return Response.created(URI.create(userId  + "/order/" + orderId.toString())).build();
    }

    @Data
    @NoArgsConstructor
    public static class OrderPatch {
        private UUID orderId;
        private Boolean isPaid;
        private Boolean isFulfilled;
    }

    @Data
    @NoArgsConstructor
    public static class OrderPayload {
        private List<NewOrderCommand.OrderedItem> items;

        @Data
        @NoArgsConstructor
        public static class OrderedItem {
            private String name;
            private Integer quantity;
        }
    }
}
