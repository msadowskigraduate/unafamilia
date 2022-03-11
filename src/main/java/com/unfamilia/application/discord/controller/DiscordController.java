package com.unfamilia.application.discord.controller;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.application.discord.model.ItemDto;
import com.unfamilia.eggbot.domain.guild.command.SetGuildAsOriginCommand;
import com.unfamilia.eggbot.domain.raidpackage.Item;
import com.unfamilia.eggbot.domain.raidpackage.command.NewItemCommand;
import com.unfamilia.eggbot.infrastructure.session.InvalidTokenException;
import com.unfamilia.eggbot.infrastructure.session.SessionToken;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/discord")
@RequiredArgsConstructor
public class DiscordController {
    private final CommandBus commandBus;
    @Inject Template login;
    @Inject Template error;

    @PUT
    @Path("/guild/{guildId}")
    public Response setGuildAsOrigin(@PathParam("guildId") Long guildId) {
        var command = SetGuildAsOriginCommand.of(guildId);
        commandBus.handle(command);
        return Response.accepted().build();
    }

    @POST
    @Path("/item")
    public Response addItem(ItemDto item) {
        var command = NewItemCommand.of(item);
        var location = UriBuilder.fromUri("/discord/item/" + command.getItemDto().getId()).build();
        commandBus.handle(command);
        return Response.created(location).build();
    }

    @GET
    @Path("/item/{itemId}")
    public Response queryItem(@PathParam("itemId") Long itemId) {
        return Response.ok(Item.findById(itemId)).build();
    }

    @GET
    @Path("/item")
    public Response queryItems() {
        return Response.ok(Item.findAll()).build();
    }

    @GET
    public TemplateInstance linkAccount(@QueryParam("session_token") String token) {
        try {
            var sessionToken = SessionToken.get(token);
            if(sessionToken.isValid()) {
                return login.data("login", LoginData.builder()
                        .loginUrl("/wow/login/" + sessionToken.getToken())
                        .build()
                );
            } else {
                return error.data(
                        "login", LoginData.builder()
                        .errorCode(Response.Status.UNAUTHORIZED.toString())
                        .errorMessage("Invalid Token! Return to Discord and restart the process.")
                        .build()
                );
            }
        } catch (InvalidTokenException e) {
            return error.data(
                    "login", LoginData.builder()
                            .errorCode(Response.Status.FORBIDDEN.toString())
                            .errorMessage("Return to Discord and restart the process.")
                            .build()
            );
        }
    }

    @Builder
    @Getter
    private static class LoginData {
        private String loginUrl;
        private String errorCode;
        private String errorMessage;
    }
}
