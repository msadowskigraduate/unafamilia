package com.unfamilia.eggbot.infrastructure.wowapi;

import com.unfamilia.eggbot.infrastructure.utilities.DefaultHttpClient;
import com.unfamilia.eggbot.infrastructure.wowapi.model.WowProfile;
import io.vertx.core.json.Json;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ApplicationScoped
public class WoWProfileApiClient implements WoWProfileClient {
    protected static final String PROTOCOL = "https://";
    protected static final String DEFAULT_REGION = "eu";
    protected static final String PROFILE_DATA_BASE = PROTOCOL + DEFAULT_REGION + ".api.blizzard.com/profile/";

    protected static final String ACCESS_TOKEN = "access_token";
    protected static final String NAMESPACE = "namespace";
    protected static final String LOCALE = "locale";
    protected static final String HTTPS_EU_BATTLE_NET_OAUTH_TOKEN = "https://eu.battle.net/oauth/token";
    
    @Inject DefaultHttpClient client;
    
    @Override
    public WowProfile queryWowProfile(String authCode) throws WebApplicationException {
        try {
            var uri = UriBuilder.fromUri(PROFILE_DATA_BASE + "user")
                    .path("wow")
                    .queryParam(NAMESPACE, "profile-eu")
                    .queryParam(LOCALE, "en_GB")
                    .queryParam(ACCESS_TOKEN, authCode)
                    .build();
            HttpResponse<String> json = get(uri);

            if(json.statusCode() > 300) {
                System.out.println(json.statusCode());
                throw new WebApplicationException(Response.status(json.statusCode()).entity(json.body()).build());
            }
            return Json.decodeValue(json.body(), WowProfile.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    protected HttpResponse<String> get(URI uri) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        return this.client.getClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
}
