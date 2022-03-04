package com.unfamilia.application.wow;

import com.unfamilia.application.command.CommandBusImpl;
import com.unfamilia.eggbot.domain.player.PlayerQuery;
import com.unfamilia.eggbot.infrastructure.wowapi.BattleNetAuthClient;
import com.unfamilia.eggbot.infrastructure.wowapi.WoWApiAccessToken;
import com.unfamilia.eggbot.infrastructure.wowapi.WoWGameDataClient;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/wow")
@RequiredArgsConstructor
public class WowApiController {
    private final CommandBusImpl bus;
    private final WoWGameDataClient gameDataClient;
    private final BattleNetAuthClient battleNetAuthClient;

    @Inject
    Template success;

    @GET
    @Path("/token")
    @Produces(APPLICATION_JSON)
    public Response queryWowTokenPrice() throws Exception {
        var token = gameDataClient.getWoWTokenPrice();
        return Response
                .ok(token)
                .build();
    }

    @GET
    @Path("/login")
    @Produces(APPLICATION_JSON)
    public Response startBattleNetOAuthLoginFlow() throws Exception {
        return Response.seeOther(battleNetAuthClient.getAuthorizationCode()).build();
    }

    @GET
    @Path("/callback")
    @Produces(APPLICATION_JSON)
    public TemplateInstance callback(@QueryParam("code") String code) throws Exception {
        var token = battleNetAuthClient.getLogin(code);
        PlayerQuery query = PlayerQuery.of(token);
        PlayerQuery result = (PlayerQuery) bus.handle(query);
        var characters = result.getProfile().getWowAccounts().stream()
                .flatMap(x -> x.getCharacters().stream())
                .collect(Collectors.toList());

        return success.data("characters", characters);
    }
}
