package com.unfamilia.application.item;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.unfamilia.application.command.CommandBus;
import com.unfamilia.eggbot.domain.player.Player;
import com.unfamilia.eggbot.domain.raidpackage.Item;
import com.unfamilia.eggbot.domain.raidpackage.command.NewItemCommand;
import io.quarkus.security.Authenticated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Path("/item")
@Authenticated
@RequiredArgsConstructor
public class ItemController {
    private final CommandBus commandBus;

    @Inject
    JsonWebToken jsonWebToken;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewItem(NewItemRequest newItemRequest) {
        var user = Player.<Player>findByIdOptional(jsonWebToken.getSubject());
        if(user.isPresent() && user.get().isAdmin()) {
            commandBus.handle(new NewItemCommand(newItemRequest.getItemId(), newItemRequest.getMaxQuantity(), newItemRequest.getSlug()));
            return Response.ok().build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryItems() {
        var user = Player.<Player>findByIdOptional(jsonWebToken.getSubject());
        if(user.isPresent()) {
            return Response.ok(Item.findAll().list()).build();
        }

        return Response.status(UNAUTHORIZED).build();
    }

    @GET
    @Path("/{item_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryItem(@PathParam("item_id") String itemId) {
        var user = Player.<Player>findByIdOptional(jsonWebToken.getSubject());
        if(user.isPresent()) {
            Optional<Item> optionalItem = Item.<Item>findByIdOptional(itemId);
            if(optionalItem.isPresent()) return Response.ok(optionalItem.get()).build();
            return Response.status(NOT_FOUND).build();
        }

        return Response.status(UNAUTHORIZED).build();
    }

    @NoArgsConstructor
    @Setter
    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class NewItemRequest {
        private Long itemId;
        private Integer maxQuantity;
        private String slug;
    }
}
