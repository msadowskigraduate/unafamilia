package com.unfamilia.application;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.unfamilia.eggbot.domain.player.RegisterNewPlayerCommand;
import com.unfamilia.eggbot.infrastructure.session.InvalidTokenException;
import com.unfamilia.eggbot.infrastructure.session.SessionToken;
import com.unfamilia.eggbot.infrastructure.wowapi.WoWProfileQuery;
import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.oidc.IdToken;
import io.quarkus.oidc.RefreshToken;
import io.quarkus.oidc.runtime.OidcProvider;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.annotations.cache.NoCache;
import reactor.util.annotation.NonNull;

import javax.inject.Inject;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/")
public class HomeController {
    @Inject
    Template base;
    @Inject
    Template login;
    @Inject
    Template user;
    @Inject Template error;
    @Inject
    ApplicationConfigProvider configProvider;

    @Inject
    @IdToken
    JsonWebToken idToken;
    @Inject
    AccessTokenCredential accessToken;

    @Inject
    RefreshToken refreshToken;

    @GET
    @Authenticated
    public TemplateInstance getHome() {
        return base.instance();
    }

    @GET
    @Path("error")
    public Response forbidden() {
        return Response
                .temporaryRedirect(
                        UriBuilder.fromUri("/login")
                                .build()
                )
                .build();
    }

    @GET
    @Path("login")
    public TemplateInstance login(@NonNull @QueryParam("session_token") String sessionToken,
                                  @QueryParam("redirect_uri") String redirectUri) {
        return login
                .data("baseLoginUrl", "https://eu.battle.net/oauth/authorize")
                .data("client_id", configProvider.wowApi().clientId())
                .data("redirect_uri", URLEncoder.encode("http://localhost:9000/callback?redirect_uri=" + redirectUri + "&session_token=" + sessionToken, StandardCharsets.UTF_8));
    }

    @GET
    @Path("/callback")
    @Produces(APPLICATION_JSON)
    public Response callback(
            @NotNull @QueryParam("code") String code,
            @NotNull @QueryParam("session_token") String sessionToken,
            @NotNull @QueryParam("redirect_uri") String redirectUri) {
        try {
            var token = SessionToken.get(sessionToken);
            if (!token.isValid()) {
                TemplateInstance template = error.data("login.errorCode", "403", "login.errorMessage", "Invalid token!");
                return Response.status(Response.Status.FORBIDDEN).entity(template.render()).build();
            }

            return Response
                    .seeOther(URI.create(URLDecoder.decode(redirectUri, StandardCharsets.UTF_8)))
                    .header(HttpHeaders.SET_COOKIE, "authorization_code=" + code + "; HttpOnly")
                    .build();

        } catch (InvalidTokenException e) {
            TemplateInstance template = error.data("login.errorCode", "401", "login.errorMessage", "Invalid token!");
            return Response.status(Response.Status.UNAUTHORIZED).entity(template.render()).build();
        }
    }
}
