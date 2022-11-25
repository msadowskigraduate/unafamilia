package com.unfamilia.eggbot.infrastructure.reporter;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/v1")
@RegisterRestClient(configKey = "wishlist-reporter")
public interface WishlistReporterAdapter {
    
    @Path("/report")
    @GET
    List<WishlistReport> queryReportForRoster();
}
