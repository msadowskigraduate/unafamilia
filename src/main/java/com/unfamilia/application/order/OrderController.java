package com.unfamilia.application.order;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.eggbot.domain.player.Player;
import com.unfamilia.eggbot.domain.raidpackage.Order;
import com.unfamilia.eggbot.domain.raidpackage.command.NewOrderCommand;
import io.quarkus.security.Authenticated;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Authenticated
@Path("/order")
@RequiredArgsConstructor
public class OrderController {
    private final CommandBus commandBus;

    @Inject
    JsonWebToken jsonWebToken;

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    public Response markOrderAsPaid(OrderPatch orderPatch) {
        var user = Player.<Player>findByIdOptional(jsonWebToken.getSubject());
        user.flatMap(player -> Order.findAllOrderForUser(player.getId()).stream()
                .filter(order -> order.id.equals(orderPatch.getOrderId()))
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newOrder(OrderPayload orderPayload) {
        var user = Player.<Player>findByIdOptional(jsonWebToken.getSubject());
        user.ifPresent(player -> commandBus.handle(
                new NewOrderCommand(
                        null,
                        player.getDiscordUserId(),
                        orderPayload.getItems().stream()
                                .map(item -> new NewOrderCommand.OrderedItem(item.getItemName(), item.getQuantity()))
                                .collect(Collectors.toList())
                )
            )
        );

        return Response.ok().build();
    }

    @Data
    @NoArgsConstructor
    public static class OrderPatch {
        private Long orderId;
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
