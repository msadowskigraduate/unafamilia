package com.unfamilia.application.wow;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.application.query.QueryBus;
import com.unfamilia.eggbot.infrastructure.wowapi.WoWProfileQuery;
import com.unfamilia.eggbot.domain.player.RegisterNewPlayerCommand;
import com.unfamilia.eggbot.infrastructure.session.InvalidTokenException;
import com.unfamilia.eggbot.infrastructure.session.SessionToken;
import com.unfamilia.eggbot.infrastructure.wowapi.BattleNetAuthClient;
import com.unfamilia.eggbot.infrastructure.wowapi.WoWGameDataClient;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/wow")
@RequiredArgsConstructor
public class WowApiController {
    private final WoWGameDataClient gameDataClient;
    private final BattleNetAuthClient battleNetAuthClient;
    private final QueryBus bus;
    private final CommandBus commandBus;

    @Inject Template success;
    @Inject Template error;

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
    @Path("/login/{session_token}")
    @Produces(APPLICATION_JSON)
    public Response startBattleNetOAuthLoginFlow(@NotNull @PathParam("session_token") String sessionToken) throws Exception {
        return Response.seeOther(battleNetAuthClient.getAuthorizationCode(sessionToken)).build();
    }

    @GET
    @Path("/callback")
    @Produces(APPLICATION_JSON)
    public TemplateInstance callback(@NotNull @QueryParam("code") String code, @NotNull @QueryParam("session_token") String sessionToken) throws Exception {
        try {
            var token = SessionToken.get(sessionToken);
            if (!token.isValid()) {
                return error.data("login.errorCode", "403", "login.errorMessage", "Invalid token!");
            }

            var authorizationCode = battleNetAuthClient.getLogin(code, token.getToken());
            WoWProfileQuery query = WoWProfileQuery.of(authorizationCode);
            var result = bus.handle(query);
            commandBus.handle(RegisterNewPlayerCommand.of(result, token.getUser()));

            var characters = result.getWowAccounts().stream()
                    .flatMap(x -> x.getCharacters().stream())
                    .collect(Collectors.toList());

            return success.data("characters", characters);
        } catch (InvalidTokenException e) {
            return error.data("login.errorCode", "401", "login.errorMessage", "Invalid token!");
        }
    }
}
