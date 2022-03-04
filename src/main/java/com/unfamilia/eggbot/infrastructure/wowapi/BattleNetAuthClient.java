package com.unfamilia.eggbot.infrastructure.wowapi;

import io.vertx.core.json.Json;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@RequiredArgsConstructor
public class BattleNetAuthClient extends WoWApiClient {
    private final WoWApiConfig config;
    private WoWApiAccessToken accessToken;

    public URI getAuthorizationCode() {
        var redirectUri = UriBuilder.fromUri("https://eu.battle.net/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", config.clientId())
                .queryParam("redirect_uri", "http://localhost:9000/wow/callback")
                .queryParam("scope", "wow.profile");

        return redirectUri.build();
    }

    public String getLogin(String code) throws Exception {
        try {

            var values = new HashMap<String, String>();
            values.put("grant_type", "authorization_code");
            values.put("redirect_uri", "http://localhost:9000/wow/callback");
            values.put("scope", "wow.profile");
            values.put(URLEncoder.encode("code", StandardCharsets.UTF_8), URLEncoder.encode(code, StandardCharsets.UTF_8));

            var request = HttpRequest.newBuilder()
                    .uri(URI.create("https://eu.battle.net/oauth/token"))
                    .header("Authorization", String.format("Basic %s", basicAuth()))
                    .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
                    .POST(buildFormDataFromMap(values))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 300) {
                throw new Exception(response.body());
            }

            this.accessToken = Json.decodeValue(response.body(), WoWApiAccessToken.class);
            return this.accessToken.getAccessToken();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<String, String> data) {
        var builder = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}
