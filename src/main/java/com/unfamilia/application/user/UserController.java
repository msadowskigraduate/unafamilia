package com.unfamilia.application.user;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.application.user.model.Event;
import com.unfamilia.application.user.model.User;
import com.unfamilia.eggbot.domain.character.Character;
import com.unfamilia.eggbot.domain.player.Player;
import com.unfamilia.eggbot.domain.player.Role;
import com.unfamilia.eggbot.domain.player.command.MakeMainCommand;
import com.unfamilia.eggbot.domain.raidpackage.Item;
import com.unfamilia.eggbot.domain.raidpackage.Order;
import com.unfamilia.eggbot.domain.raidpackage.OrderItem;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;

@Path("/user")
@RequiredArgsConstructor
public class UserController {
    private final CommandBus commandBus;
    private final GatewayDiscordClient discordClient;

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
            return Response.ok(Templates.user(idToken.getRawToken(), accessTokenCredential.getToken(), User.from(player), orders, List.of(), List.of()))
                    .header(HttpHeaders.SET_COOKIE, "authorization_code=" + accessTokenCredential.getToken() + "; HttpOnly")
                    .build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        }
    }

    @GET
    @Path("/test")
    public Response test() {
        var item1 = new Item();
        item1.setName("Potion of Spectral Intellect");
        item1.setItemPrice(0.0);
        item1.setId(171273l);
        item1.setMaxAmount(20);

        var item2 = new Item();
        item2.setName("Potion of Spectral Agility");
        item2.setItemPrice(0.0);
        item2.setId(171270l);
        item2.setMaxAmount(20);


        var orderItems = List.of(OrderItem.of(item1, 20), OrderItem.of(item2, 5));
        var orders = List.of(Order.of(123l, 123l, false, false, Instant.now(), orderItems), Order.of(123l, 123l, false, false, Instant.now(), orderItems)).stream()
                .map(com.unfamilia.application.user.model.Order::from)
                .collect(toList());


        var char1 = Character.of(1l, "Nyly", 60l, 1l, "Death Knight", "magtheridon", com.unfamilia.eggbot.infrastructure.wowapi.model.Character.Faction.Type.ALLIANCE);
        var char2 = Character.of(1l, "Lockedupnyly", 60l, 1l, "Warlock", "magtheridon", com.unfamilia.eggbot.infrastructure.wowapi.model.Character.Faction.Type.ALLIANCE);
        var player = Player.of(1l, 1l, "Sadocha", List.of(char1, char2), char1, List.of(Role.of(1l, "Raider"), Role.of(2l, "Officer")));
        List<Event> events = List.of(Event.from(com.unfamilia.eggbot.domain.event.Event.of("Sepulcher", "Raid", "Raider", LocalDateTime.now(), player, List.of(char1, char2))),Event.from(com.unfamilia.eggbot.domain.event.Event.of("Sepulcher", "Raid", "Raider", LocalDateTime.now(), player, List.of(char1, char2))));
        return Response.ok(Templates.user("some_token","access_token", User.from(player), orders, List.of(item1, item2), events)).build();
    }

    @CheckedTemplate(basePath = "")
    public static class Templates {
        public static native TemplateInstance user(String jwt, String authCode, User user, List<com.unfamilia.application.user.model.Order> orders, List<Item> items, List<Event> events);
    }
}
