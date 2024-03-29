package com.unfamilia.application;

import com.unfamilia.application.command.CommandBus;
import com.unfamilia.application.command.GenericCommandBusException;
import com.unfamilia.application.user.User;
import com.unfamilia.application.user.command.NewUserCommand;
import com.unfamilia.eggbot.infrastructure.session.InvalidTokenException;
import com.unfamilia.eggbot.infrastructure.session.SessionToken;
import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.oidc.IdToken;
import io.quarkus.oidc.OidcSession;
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
    OidcSession oidcSession;

    @Inject
    @IdToken
    JsonWebToken idToken;
    @Inject
    AccessTokenCredential accessToken;

    @Inject
    CommandBus commandBus;

    @GET
    public Response getHome() {
        if(idToken.getSubject() != null) {
            var user = User.findByOptionalBattleNetId(Long.valueOf(idToken.getSubject()));
            if(user.isPresent()) {
                return Response.seeOther(URI.create("/user")).build();
            }
        }
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
        if(idToken.getSubject() != null) {
            var user = User.findByOptionalBattleNetId(Long.valueOf(idToken.getSubject()));
            if(user.isPresent()) {
                return Response.seeOther(URI.create("/user")).build();
            }
        }
        
        UriBuilder redirectUriBuilder = UriBuilder.fromUri(this.configProvider.hostname() + "/callback")
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
    @Authenticated
    @Path("logout")
    public Response logout(@QueryParam("session_token") String sessionToken,
                          @QueryParam("redirect_uri") String redirectUri) {
        oidcSession.logout().await().indefinitely();
        return Response.seeOther(URI.create("/login")).build();
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
            var wowProfileId = Long.valueOf(idToken.getSubject());
            var battleTag = this.idToken.<String>getClaim("battle_tag");
            

            if(idToken.getSubject() != null) {
                var user = User.findByOptionalBattleNetId(Long.valueOf(idToken.getSubject()));
                if(user.isPresent()) {
                    return Response.seeOther(URI.create("/user")).build();
                }
            }

            commandBus.handle(new NewUserCommand(accessToken.getToken(), sessionToken1.getUserId(), wowProfileId, battleTag));

            return Response
                    .seeOther(URI.create(URLDecoder.decode(redirectUri, StandardCharsets.UTF_8)))
                    .header(HttpHeaders.SET_COOKIE, "authorization_code=" + authorizationCode + "; HttpOnly")
                    .build();

        } catch (InvalidTokenException | GenericCommandBusException e) {
            TemplateInstance template = error.data("login.errorCode", "401", "login.errorMessage", "Invalid token!");
            return Response.status(Response.Status.UNAUTHORIZED).entity(template.render()).build();
        }
    }
}
