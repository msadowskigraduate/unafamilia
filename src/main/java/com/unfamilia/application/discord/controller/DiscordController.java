package com.unfamilia.application.discord.controller;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.application.model.ErrorResponse;
import com.unfamilia.eggbot.domain.guild.command.SetGuildAsOriginCommand;
import com.unfamilia.eggbot.domain.player.Player;
import com.unfamilia.eggbot.infrastructure.session.InvalidTokenException;
import com.unfamilia.eggbot.infrastructure.session.SessionToken;
import io.quarkus.qute.Template;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

    @GET
    public Response linkAccount(@QueryParam("session_token") String token) {
        try {
            var sessionToken = SessionToken.get(token);
            if(sessionToken.isValid()) {
                return Response.seeOther(URI.create("/login?session_token=" + sessionToken.getToken() + "&redirect_uri=" + URLEncoder.encode("/user", StandardCharsets.UTF_8))).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).entity(error.data(
                        "login", ErrorResponse.builder()
                        .errorCode(Response.Status.UNAUTHORIZED.getStatusCode())
                        .error("Invalid Token! Return to Discord and restart the process.")
                        .build()
                ).render()).build();
            }
        } catch (InvalidTokenException e) {
            return Response.status(Response.Status.FORBIDDEN.getStatusCode()).entity(error.data(
                    "login", ErrorResponse.builder()
                            .errorCode(Response.Status.FORBIDDEN.getStatusCode())
                            .error("Invalid Token! Return to Discord and restart the process.")
                            .build()
            ).render()).build();
        }
    }

    @GET
    public Response sessionTokenForUser(Long userId) {
        if(Player.findByDiscordUserId(userId).isEmpty()) {
            return Response.ok(SessionToken.generateForUser(userId)).build();
        }

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(ErrorResponse.builder().errorCode(400).error("User exists"))
                .build();
    }
}
