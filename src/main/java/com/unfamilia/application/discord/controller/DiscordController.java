package com.unfamilia.application.discord.controller;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.application.discord.model.ItemDto;
import com.unfamilia.eggbot.domain.guild.command.SetGuildAsOriginCommand;
import com.unfamilia.eggbot.domain.raidpackage.Item;
import com.unfamilia.eggbot.domain.raidpackage.command.NewItemCommand;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/discord")
@RequiredArgsConstructor
public class DiscordController {
    private final CommandBus commandBus;

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
}
