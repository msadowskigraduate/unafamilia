package com.unfamilia.application.event;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.eggbot.domain.player.Player;
import io.quarkus.security.Authenticated;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Authenticated
@Path("/{tenant_id}/event")
@RequiredArgsConstructor
public class EventController {
    public final CommandBus commandBus;

    @POST
    @Consumes(APPLICATION_JSON)
    public Response commandCreateNewEvent(ScheduledEvent event) {
        var user = Player.<Player>findByIdOptional(event.organizerId);
        if(user.isPresent()) {
            return Response.ok("Not yet implemented but good job.").build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @GET
    public Response queryAllEventsForPlayer(@QueryParam("userId") Long userId) {
        var user = Player.<Player>findByIdOptional(userId);
        if(user.isPresent()) {
            return Response.ok("Not yet implemented but good job.").build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    record ScheduledEvent(Long organizerId) {

    }
}
