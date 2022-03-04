package com.unfamilia.application.roster;

import lombok.RequiredArgsConstructor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("v1/roster")
@RequiredArgsConstructor
public class RosterController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoster() {
        return Response.ok().build();
    }
}
