package com.unfamilia.application;

import com.unfamilia.eggbot.infrastructure.session.InvalidTokenException;
import com.unfamilia.eggbot.infrastructure.session.SessionToken;
import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.oidc.IdToken;
import io.quarkus.oidc.RefreshToken;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/")
public class HomeController {
    private final String DEFAULT_REDIRECT_PAGE = "/user";
    @Inject
    Template base;
    @Inject
    Template login;
    @Inject
    Template error;
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
        return Response.temporaryRedirect(UriBuilder.fromUri("/login").build()).build();
    }

    @GET
    @Path("login")
    public TemplateInstance login(@QueryParam("session_token") String sessionToken,
                                  @QueryParam("redirect_uri") String redirectUri) {
        UriBuilder redirectUriBuilder = UriBuilder.fromUri("http://localhost:9000/callback")
                .queryParam("redirect_uri", redirectUri == null ? DEFAULT_REDIRECT_PAGE : redirectUri);

        if (sessionToken != null) {
            redirectUriBuilder.queryParam("session_token", sessionToken);
        }

        return login
                .data("baseLoginUrl", "https://eu.battle.net/oauth/authorize")
                .data("client_id", configProvider.wowApi().clientId())
                .data("redirect_uri", redirectUriBuilder.build());
    }

    @GET
    @Path("callback")
    @Produces(APPLICATION_JSON)
    public Response callback(
            @NotNull @QueryParam("code") String authorizationCode,
            @QueryParam("session_token") String sessionToken,
            @NotNull @QueryParam("redirect_uri") String redirectUri) {
        try {
            if (sessionToken != null && !SessionToken.get(sessionToken).isValid()) {
                TemplateInstance template = error.data("login.errorCode", "403", "login.errorMessage", "Invalid token!");
                return Response.status(Response.Status.FORBIDDEN).entity(template.render()).build();

            }

            return Response
                    .seeOther(URI.create(URLDecoder.decode(redirectUri, StandardCharsets.UTF_8)))
                    .header(HttpHeaders.SET_COOKIE, "authorization_code=" + authorizationCode + "; HttpOnly")
                    .build();

        } catch (InvalidTokenException e) {
            TemplateInstance template = error.data("login.errorCode", "401", "login.errorMessage", "Invalid token!");
            return Response.status(Response.Status.UNAUTHORIZED).entity(template.render()).build();
        }
    }
}
