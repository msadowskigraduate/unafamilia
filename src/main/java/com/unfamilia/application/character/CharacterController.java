package com.unfamilia.application.character;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("v1/character")
public class CharacterController {

    @GET
    public Response getCharacter() {
        return Response.ok().build();
    }
}
