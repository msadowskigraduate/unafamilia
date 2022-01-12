package com.unfamilia.application.controllers;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.eggbot.domain.guild.command.SetGuildAsOriginCommand;
import com.unfamilia.eggbot.infrastructure.wowapi.WoWGameDataClient;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/discord")
@RequiredArgsConstructor
public class DiscordController {
    private final CommandBus commandBus;
    private final WoWGameDataClient gameDataClient;

    @PUT
    @Path("/guild/{guildId}")
    public Response setGuildAsOrigin(@PathParam("guildId") Long guildId) {
        var command = SetGuildAsOriginCommand.of(guildId);
        commandBus.handle(command);
        return Response.accepted().build();
    }

    @GET
    @Path("/wow/token")
    public Response test() throws Exception {
        gameDataClient.getWoWTokenPrice();
        return Response.accepted().build();
    }
}
