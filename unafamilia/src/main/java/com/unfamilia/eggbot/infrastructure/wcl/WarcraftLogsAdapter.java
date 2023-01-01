package com.unfamilia.eggbot.infrastructure.wcl;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient(configKey = "wcl")
public interface WarcraftLogsAdapter {

    @POST
    @Path("oauth/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    WclWebToken generateToken(@HeaderParam("Authorization") String authString, MultivaluedMap<String,String> requestAuthToken);
    
    @POST
    @Path("api/v2/client")
    Response queryGraphQlClient(@HeaderParam("Authorization") String bearerToken, String queryString);
}
