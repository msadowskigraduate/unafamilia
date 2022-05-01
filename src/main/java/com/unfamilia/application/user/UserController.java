package com.unfamilia.application.user;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.application.user.model.User;
import com.unfamilia.eggbot.domain.player.Player;
import com.unfamilia.eggbot.domain.player.Role;
import com.unfamilia.eggbot.domain.player.command.MakeMainCommand;
import com.unfamilia.eggbot.domain.raidpackage.Order;
import com.unfamilia.eggbot.infrastructure.wowapi.WoWProfileApiClient;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.oidc.IdToken;
import io.quarkus.qute.Template;
import io.quarkus.security.Authenticated;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.net.URI;

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
    @Transactional
    public Response getPlayerInfo() {
        var player = Player.<Player>findById(Long.valueOf(idToken.getClaim("sub")));

        if(player == null) {
            return Response.seeOther(URI.create("/callback?redirect_uri=/user")).build();
        }

        try {
            var orders = Order.findAllOrdersForDiscordUser(player.getDiscordUserId());
            return Response.ok(
                            user
                                    .data("jwt", idToken.getRawToken())
                                    .data("authCode", accessTokenCredential.getToken())
                                    .data("user", User.from(player))
                                    .data("orders", orders)
                                    .render()
                    )
                    .header(HttpHeaders.SET_COOKIE, "authorization_code=" + accessTokenCredential.getToken() + "; HttpOnly")
                    .build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        }
    }
}
