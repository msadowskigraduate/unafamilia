package com.unfamilia.eggbot.infrastructure.event;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@RegisterRestClient(configKey = "events-v1")
public interface EventAdapter {
    @GET
    Response queryEvents();

    @GET
    @Path("/{event_id}")
    Response queryEvent(@PathParam("event_id") String eventId);

    @POST
    Response createNewEvent();

    @POST
    @Path("/{event_id}")
    Response updateEvent(@PathParam("event_id") String eventId);
}
