package com.unfamilia.application.discord;

import com.unfamilia.application.ErrorResponse;
import com.unfamilia.application.user.User;
import com.unfamilia.eggbot.infrastructure.session.InvalidTokenException;
import com.unfamilia.eggbot.infrastructure.session.SessionToken;
import io.quarkus.qute.Template;
import io.quarkus.security.Authenticated;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Path("/discord")
@RequiredArgsConstructor
public class DiscordController {
    @Inject Template login;
    @Inject Template error;

    @GET
    public Response linkAccount(@QueryParam("session_token") String token) {
        try {
            var sessionToken = SessionToken.get(token);
            if(sessionToken.isValid()) {
                return Response.seeOther(URI.create("/login?session_token=" + sessionToken.getToken() + "&redirect_uri=" + URLEncoder.encode("/user", StandardCharsets.UTF_8))).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).entity(error.data(
                        "login", ErrorResponse.builder()
                        .errorCode(Response.Status.UNAUTHORIZED.getStatusCode())
                        .error("Invalid Token! Return to Discord and restart the process.")
                        .build()
                ).render()).build();
            }
        } catch (InvalidTokenException e) {
            return Response.status(Response.Status.FORBIDDEN.getStatusCode()).entity(error.data(
                    "login", ErrorResponse.builder()
                            .errorCode(Response.Status.FORBIDDEN.getStatusCode())
                            .error("Invalid Token! Return to Discord and restart the process.")
                            .build()
            ).render()).build();
        }
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response sessionTokenForUser(@NotNull String discordUserId) {
        Long discordId = Long.parseLong(discordUserId);
        if(User.findByOptionalDiscordId(discordId).isEmpty()) {
            return Response.ok(SessionToken.generateForUser(discordId)).build();
        }

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(ErrorResponse.builder().errorCode(400).error("User exists"))
                .build();
    }
}
