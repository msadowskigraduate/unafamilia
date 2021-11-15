package com.unfamilia.application.controllers;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v1/bonanza")
public class BonanzaController {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response commandCreateNewBonanzaSession() {
        return Response
                .ok()
                .build();
    }

    @GET
    @Path("/{session}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryBonanzaSession(@PathParam("session") String session) {
        return Response.ok().build();
    }
}
