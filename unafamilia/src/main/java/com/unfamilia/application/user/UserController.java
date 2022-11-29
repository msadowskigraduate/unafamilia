package com.unfamilia.application.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.logging.Log;
import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.oidc.IdToken;
import io.quarkus.panache.common.Parameters;
import io.quarkus.security.Authenticated;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class UserController {
    @Inject @IdToken JsonWebToken idToken;
    @Inject AccessTokenCredential accessTokenCredential;


    @GET
    @NoCache
    @Authenticated
    @Transactional
    public Response getPlayerInfo() {
        var user = User.findByOptionalBattleNetId(Long.valueOf(idToken.getSubject()));
        Log.info("User: " + idToken.getSubject());

        if(user.isEmpty()) {
            var redirectUrl = URLEncoder.encode("/user",  StandardCharsets.UTF_8);
            return Response.seeOther(URI.create("/login?redirect_uri=" + redirectUrl)).build();
        }

        try {
            return Response.ok(user.get())
                    .header(HttpHeaders.SET_COOKIE, "authorization_code=" + accessTokenCredential.getToken() + "; HttpOnly")
                    .build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        }
    }

    @GET
    @Transactional
    @Path("/character")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCharacterOwner(@QueryParam("character_name") String characterName, @QueryParam("character_realm") String realmSlug) {
        System.out.println("Requesting user info for character: " + characterName + "-" + realmSlug);

        try {
        var result = User.<User>find(
            "select u from Users u inner join u.characters c where c.name = :name and c.realm = :realm",
             Parameters.with("name", characterName).and("realm", realmSlug)).singleResult();

        return Response.ok(new UserDto(result.getName(), result.getDiscordUserId(), result.getBattleNetUserId(), result.getRank())).build();
        } catch (NoResultException exception) {
            return Response.noContent().build();
        }
    }
}

record UserDto(@JsonProperty("name") String name, @JsonProperty("discord_user_id") Long discordUserId, @JsonProperty("battle_net_user_id") Long battleNetUserId, @JsonProperty("rank") Integer rank) {}