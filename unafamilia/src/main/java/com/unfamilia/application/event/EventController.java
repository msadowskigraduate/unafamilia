package com.unfamilia.application.event;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.eggbot.domain.player.Player;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/{userId}/event")
@RequiredArgsConstructor
public class EventController {
    public final CommandBus commandBus;

    @Inject
    JsonWebToken jsonWebToken;

    @POST
    @Consumes(APPLICATION_JSON)
    public Response commandCreateNewEvent(@PathParam("userId") Long userId, ScheduledEvent event) {
        var user = Player.<Player>findByIdOptional(jsonWebToken.getSubject());
        if(user.isPresent()) {
            return Response.ok("Not yet implemented but good job.").build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @GET
    public Response queryAllEventsForPlayer(@PathParam("userId") Long userId) {
        var user = Player.<Player>findByIdOptional(jsonWebToken.getSubject());
        if(user.isPresent()) {
            return Response.ok("Not yet implemented but good job.").build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    record ScheduledEvent() {

    }
}
