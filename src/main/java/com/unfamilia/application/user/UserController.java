package com.unfamilia.application.user;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.application.user.model.User;
import com.unfamilia.eggbot.domain.player.Player;
import com.unfamilia.eggbot.domain.player.command.MakeMainCommand;
import com.unfamilia.eggbot.domain.raidpackage.Order;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.oidc.IdToken;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
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
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;

@Authenticated
@Path("/user")
@RequiredArgsConstructor
public class UserController {
    private final CommandBus commandBus;
    private final GatewayDiscordClient discordClient;
    //    @Inject Template user;

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
            return Response.seeOther(URI.create("/login?redirect_uri=/user")).build();
        }

        try {
            var orders = Optional.of(Order.findAllOrdersForDiscordUser(player.getDiscordUserId()))
                    .map(orders1 -> orders1.stream()
                            .map(com.unfamilia.application.user.model.Order::from)
                            .collect(toList())
                    )
                    .orElse(null);
            return Response.ok(
                    Templates.user(idToken.getRawToken(), accessTokenCredential.getToken(), User.from(player), orders)
//                            user
//                                    .data("jwt", idToken.getRawToken())
//                                    .data("authCode", accessTokenCredential.getToken())
//                                    .data("user", User.from(player))
//                                    .data("orders", orders)
//                                    .render()
                    )
                    .header(HttpHeaders.SET_COOKIE, "authorization_code=" + accessTokenCredential.getToken() + "; HttpOnly")
                    .build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        }
    }

    @CheckedTemplate(basePath = "")
    public static class Templates {
        public static native TemplateInstance user(String jwt, String authCode, User user, List<com.unfamilia.application.user.model.Order> orders);
    }
}
