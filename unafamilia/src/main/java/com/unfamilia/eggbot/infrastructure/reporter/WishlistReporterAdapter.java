package com.unfamilia.eggbot.infrastructure.reporter;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient(configKey = "wishlist-reporter")
public interface WishlistReporterAdapter {
    
    @Path("v1/report")
    @GET
    List<WishlistReport> queryReportForRoster();
    
    @Path("v1/team")
    @GET
    List<Roster> queryRoster();
    
    @Path("v2/report")
    @GET
    WishlistReport queryReportForRosterV2(@QueryParam("character_id") String characterId, @QueryParam("include") List<String> include);
}
