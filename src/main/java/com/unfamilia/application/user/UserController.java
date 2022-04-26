package com.unfamilia.application.user;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.eggbot.domain.player.MakeMainCommand;
import com.unfamilia.eggbot.infrastructure.wowapi.WoWGameDataClient;
import com.unfamilia.eggbot.infrastructure.wowapi.WoWProfileApiClient;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.oidc.IdToken;
import io.quarkus.oidc.IdTokenCredential;
import io.quarkus.qute.Template;
import io.quarkus.security.Authenticated;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;

@Authenticated
@Path("/user")
@RequiredArgsConstructor
public class UserController {
    private final CommandBus commandBus;
    private final GatewayDiscordClient discordClient;
    private final WoWProfileApiClient woWProfileApiClient;
    @Inject Template user;

    @Inject @IdToken JsonWebToken idToken;
    @Inject AccessTokenCredential accessTokenCredential;

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
    public Response getPlayerInfo() {
        try {
            var profileData = woWProfileApiClient.queryWowProfile(accessTokenCredential.getToken());
            var characters = profileData.getWowAccounts().stream()
                    .flatMap(x -> x.getCharacters().stream())
                    .collect(toList());
            return Response.ok(
                            user
                                    .data("name", idToken.getClaim("battle_tag"))
                                    .data("jwt", idToken.getRawToken())
                                    .data("authCode", accessTokenCredential.getToken())
                                    .data("characters", characters)
                                    .data("userid", idToken.claim("sub"))
                    )
                    .header(HttpHeaders.SET_COOKIE, "authorization_code=" + accessTokenCredential.getToken() + "; HttpOnly")
                    .build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        }
    }
}
