package com.unfamilia.eggbot.infrastructure.wowguild;

import javax.ws.rs.GET;
import javax.ws.rs.Path;


import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import com.unfamilia.eggbot.infrastructure.wowguild.model.WoWGuildRosterResponse;

@Path("/roster")
@RegisterRestClient(configKey = "wow-api")
public interface WoWApiGuildAdapter {
    @GET
    WoWGuildRosterResponse queryGuildRoster(@QueryParam("guild_realm") String realmSlug, @QueryParam("guild_name") String guildNameSlug);
}
