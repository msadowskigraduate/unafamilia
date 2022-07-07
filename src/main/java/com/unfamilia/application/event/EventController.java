package com.unfamilia.application.event;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.eggbot.domain.event.Event;
import com.unfamilia.eggbot.domain.player.Player;
import com.unfamilia.eggbot.domain.player.Role;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/{userId}/event")
@RequiredArgsConstructor
public class EventController {
    public final CommandBus commandBus;

    @Inject
    JsonWebToken jsonWebToken;

    @POST
    public Response commandCreateNewEvent(@PathParam("userId") Long userId, ScheduledEvent event) {
        var user = Player.<Player>findByIdOptional(jsonWebToken.getSubject());
        if(user.isPresent()) {
            return Response.ok("Not yet implemented but good job.").build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @GET
    public Response queryAllEventsForPlayer() {
        var user = Player.<Player>findByIdOptional(jsonWebToken.getSubject());
        if(user.isPresent()) {
            return Response.ok("Not yet implemented but good job.").build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    private static record ScheduledEvent() {

    }
}
