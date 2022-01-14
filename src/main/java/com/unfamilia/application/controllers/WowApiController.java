package com.unfamilia.application.controllers;

import com.unfamilia.eggbot.infrastructure.wowapi.WoWGameDataClient;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

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
