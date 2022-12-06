package com.unfamilia.eggbot.infrastructure.discord;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient(configKey = "discord-auth")
public interface DiscordAuthenticationClient {

    @Path("oauth2/token")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    DiscordAccessToken exchangeToken(MultivaluedMap<String,String> requestAuthToken);

    @Path("oauth2/@me")
    @GET
    DiscordMember userInfo(@HeaderParam("Authorization") String code);
}
