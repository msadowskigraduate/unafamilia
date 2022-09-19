package com.unafamilia;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path(EventResource.EVENT_PATH)
public class EventResource {
    static final String EVENT_PATH = "/v1/event";

    @POST
    @Transactional
    @Consumes(APPLICATION_JSON)
    public Response createNewEvent(EventDto eventDto) {
        var invitees = eventDto.draftedPlayers().stream()
                                .map(inviteeDto -> {
                                    var invitee = new Invitee();
                                    invitee.characterId = inviteeDto.characterId();
                                    invitee.status = inviteeDto.status();
                                    return invitee;
                                })
                                .peek(invitee -> invitee.persist())
                                .collect(Collectors.toList());
        var event = Event.create(eventDto.date(), eventDto.organizerId(), eventDto.eventName(), eventDto.activityName(), invitees);
        event.persist();

        return Response.created(createLocationForId(event.id)).build();
    }

    @GET
    @Path("/{event_id}")
    @Transactional
    public Response queryEvent(@PathParam("event_id") UUID eventId) {
        var event = Event.<Event>findByIdOptional(eventId);

        if (event.isPresent()) {
            return Response.ok(event.get()).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Transactional
    public Response queryAllEvents() {
        var events = Event.<Event>listAll();
        return Response.ok(events).build();
    }

    private URI createLocationForId(UUID id) {
        return URI.create(EVENT_PATH + "/" + id);
    }
}