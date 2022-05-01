package com.unfamilia.application;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.eggbot.domain.player.Player;
import com.unfamilia.eggbot.domain.player.command.RegisterNewPlayerCommand;
import com.unfamilia.eggbot.domain.player.command.RegisterNewPlayerFromDiscordCommand;
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
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
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

    @Inject
    CommandBus commandBus;

    @GET
    public Response getHome() {
        return Response.seeOther(URI.create("/login")).build();
    }

    @GET
    @Path("error")
    public Response forbidden() {
        return Response.temporaryRedirect(UriBuilder.fromUri("/login").build()).build();
    }

    @GET
    @Path("login")
    public Response login(@QueryParam("session_token") String sessionToken,
                          @QueryParam("redirect_uri") String redirectUri) {
        var sessionToken1 = SessionToken.get(sessionToken);

        if(idToken.getSubject() != null && !sessionToken1.isValid()) {
            return Response.seeOther(UriBuilder.fromUri(DEFAULT_REDIRECT_PAGE).build()).build();
        }

        if (idToken.getSubject() != null && sessionToken1.isValid()) {
            this.commandBus.handle(RegisterNewPlayerFromDiscordCommand.of(
                    accessToken.getToken(),
                    sessionToken1,
                    this.idToken.<String>getClaim("battle_tag"),
                    Long.valueOf(idToken.getSubject())
                )
            );
            return Response
                    .seeOther(URI.create(redirectUri == null ? DEFAULT_REDIRECT_PAGE : redirectUri))
                    .build();
        }

        UriBuilder redirectUriBuilder = UriBuilder.fromUri("http://localhost:9000/callback")
                .queryParam("redirect_uri", redirectUri == null ? DEFAULT_REDIRECT_PAGE : redirectUri);

        if (sessionToken != null) {
            redirectUriBuilder.queryParam("session_token", sessionToken);
        }

        return Response
                .ok(login
                        .data("baseLoginUrl", "https://eu.battle.net/oauth/authorize")
                        .data("client_id", configProvider.wowApi().clientId())
                        .data("redirect_uri", URLEncoder.encode(redirectUriBuilder.toTemplate(), StandardCharsets.UTF_8))
                        .render())
                .build();
    }

    @GET
    @Path("callback")
    @Produces(APPLICATION_JSON)
    @Authenticated
    public Response callback(
            @NotNull @QueryParam("code") String authorizationCode,
            @QueryParam("session_token") String sessionToken,
            @NotNull @QueryParam("redirect_uri") String redirectUri) {
        try {
            var sessionToken1 = SessionToken.get(sessionToken);
            var wowProfileId = Long.valueOf(this.idToken.getClaim("sub"));
            var battleTag = this.idToken.<String>getClaim("battle_tag");
            this.commandBus.handle(
                    sessionToken1.isValid() ?
                            RegisterNewPlayerFromDiscordCommand.of(accessToken.getToken(), sessionToken1, battleTag, wowProfileId) :
                            RegisterNewPlayerCommand.of(accessToken.getToken(), battleTag, wowProfileId)
            );


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
