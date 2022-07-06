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
import javax.ws.rs.core.Response;

@Path("/event")
@RequiredArgsConstructor
public class EventController {
    public final CommandBus commandBus;

    @Inject
    JsonWebToken jsonWebToken;

    @POST
    public Response commandCreateNewEvent() {
        var user = Player.<Player>findByIdOptional(jsonWebToken.getSubject());
    }

    @GET
    public Response queryAllEventsForPlayerRoles() {
        var user = Player.<Player>findByIdOptional(jsonWebToken.getSubject());
        if(user.isPresent()) {
            var role = user.get().getRole();
            var events = Event.findAllForRole();
            return Response.ok(events).build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
