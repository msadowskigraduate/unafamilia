package com.unfamilia.application.user;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.eggbot.domain.player.MakeMainCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import io.quarkus.oidc.IdToken;
import io.quarkus.qute.Template;
import io.quarkus.security.Authenticated;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;

@Authenticated
@Path("/user")
@RequiredArgsConstructor
public class UserController {
    private final CommandBus commandBus;
    private final GatewayDiscordClient discordClient;
    @Inject Template user;

    @Inject @IdToken JsonWebToken idToken;
    @Inject JsonWebToken jsonWebToken;

    @POST
    @Path("/{userId}/{characterId}")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response makeMain(@PathParam("userId") Long userId, @PathParam("characterId") Long characterId) {
        var user = discordClient.getUserById(Snowflake.of(userId)).blockOptional();

        if(user.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        commandBus.handle(MakeMainCommand.of(user.get(), characterId));
        return Response.ok().build();
    }

    @GET
    @NoCache
    @Authenticated
    public Response getPlayerInfo(@QueryParam("code") String code) {
        return Response.ok(
                user
                        .data("principal", idToken.getClaim("battle_tag"))
                        .data("jwt", idToken.getRawToken())
        )
                .header(HttpHeaders.SET_COOKIE, "authorization_code=" + code + "; HttpOnly")
                .build();
    }
}
