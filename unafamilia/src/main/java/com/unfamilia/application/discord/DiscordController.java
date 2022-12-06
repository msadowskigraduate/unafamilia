package com.unfamilia.application.discord;

import com.unfamilia.application.ErrorResponse;
import com.unfamilia.application.command.CommandBus;
import com.unfamilia.application.command.GenericCommandBusException;
import com.unfamilia.application.user.User;
import com.unfamilia.application.user.command.AddDiscordIdToUserCommand;
import com.unfamilia.eggbot.infrastructure.discord.DiscordAuthenticationClient;
import com.unfamilia.eggbot.infrastructure.discord.DiscordConfigurationProvider;
import com.unfamilia.eggbot.infrastructure.session.InvalidTokenException;
import com.unfamilia.eggbot.infrastructure.session.SessionToken;

import io.quarkus.oidc.IdToken;
import io.quarkus.qute.Template;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Path("/discord")
@RequiredArgsConstructor
public class DiscordController {
    @Inject
    Template login;
    @Inject
    Template error;

    @Inject
    @IdToken
    JsonWebToken idToken;

    @Inject
    @RestClient
    DiscordAuthenticationClient discordAuthenticationClient;

    @Inject CommandBus bus;
    @Inject DiscordConfigurationProvider discordProvider;

    @GET
    public Response linkAccount(@QueryParam("session_token") String token) {
        try {
            var sessionToken = SessionToken.get(token);
            if (sessionToken.isValid()) {
                if (idToken.getSubject() != null) {
                    var command = new AddDiscordIdToUserCommand(Long.valueOf(idToken.getSubject()), sessionToken.getUserId());
                    bus.handle(command);

                    return Response.seeOther(URI.create("/user")).build();
                }


                return Response.seeOther(URI.create("/login?session_token=" + sessionToken.getToken() + "&redirect_uri="
                        + URLEncoder.encode("/user", StandardCharsets.UTF_8))).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).entity(error.data(
                        "login", ErrorResponse.builder()
                                .errorCode(Response.Status.UNAUTHORIZED.getStatusCode())
                                .error("Invalid Token! Return to Discord and restart the process.")
                                .build())
                        .render()).build();
            }
        } catch (InvalidTokenException | GenericCommandBusException e) {
            return Response.status(Response.Status.FORBIDDEN.getStatusCode()).entity(error.data(
                    "login", ErrorResponse.builder()
                            .errorCode(Response.Status.FORBIDDEN.getStatusCode())
                            .error("Invalid Token! Return to Discord and restart the process.")
                            .build())
                    .render()).build();
        }
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response sessionTokenForUser(@NotNull String discordUserId) {
        Long discordId = Long.parseLong(discordUserId);
        if (User.findByOptionalDiscordId(discordId).isEmpty()) {
            return Response.ok(SessionToken.generateForUser(discordId)).build();
        }

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(ErrorResponse.builder().errorCode(400).error("User exists"))
                .build();
    }

    @POST
    @Path("/oauth2/authorize")
    public Response authorizeWithDiscord() {
        return Response.ok(discordProvider.provideAuthenticationUrl())
                .build();
    }

    @GET
    @Path("/oauth2/authorize")
    public Response initAuthorizationFlow(@QueryParam("code") String code) throws GenericCommandBusException {
        var response = discordAuthenticationClient.exchangeToken(discordProvider.provideAuthenticationForm(code));
        var discordMember = discordAuthenticationClient.userInfo("Bearer " + response.accessToken());

        var command = new AddDiscordIdToUserCommand(Long.valueOf(idToken.getSubject()), Long.valueOf(discordMember.user().id()));
        bus.handle(command);

        return Response.seeOther(URI.create("/user")).build();
    }
}
