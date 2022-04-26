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

    @GET
    @Path("/token")
    @Produces(APPLICATION_JSON)
    public Response queryWowTokenPrice() throws Exception {
        var token = gameDataClient.getWoWTokenPrice();
        return Response
                .ok(token)
                .build();
    }
}
