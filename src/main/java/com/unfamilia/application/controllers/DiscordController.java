package com.unfamilia.application.controllers;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.eggbot.domain.guild.command.SetGuildAsOriginCommand;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

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
}
