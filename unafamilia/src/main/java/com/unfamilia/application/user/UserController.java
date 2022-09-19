package com.unfamilia.application.user;

import com.unfamilia.application.command.CommandBus;

import io.quarkus.logging.Log;
import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.oidc.IdToken;
import io.quarkus.security.Authenticated;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Produces(MediaType.APPLICATION_JSON)
@Path("/user")
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
            return Response.seeOther(URI.create("/login?redirect_uri=/user")).build();
        }

        try {
            return Response.ok(user.get())
                    .header(HttpHeaders.SET_COOKIE, "authorization_code=" + accessTokenCredential.getToken() + "; HttpOnly")
                    .build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        }
    }
}
