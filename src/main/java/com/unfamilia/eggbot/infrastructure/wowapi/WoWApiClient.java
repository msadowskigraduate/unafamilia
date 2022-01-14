package com.unfamilia.eggbot.infrastructure.wowapi;

import io.vertx.core.json.Json;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@ApplicationScoped
@RequiredArgsConstructor
class WoWApiClient {
    private final WoWApiConfig config;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    @Getter
    private final HttpClient client = HttpClient.newBuilder()
            .executor(executorService)
            .build();

    private WoWApiAccessToken token;

    void login() throws Exception {
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://eu.battle.net/oauth/token"))
                .header("Authorization", String.format("Basic %s", basicAuth()))
                .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
                .POST(HttpRequest.BodyPublishers.ofString(
                        URLEncoder.encode("grant_type", StandardCharsets.UTF_8) +
                                "=" +
                                URLEncoder.encode("client_credentials", StandardCharsets.UTF_8)
                ))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 300) {
            throw new Exception(response.body());
        }

        token = Json.decodeValue(response.body(), WoWApiAccessToken.class);
    }

    public String getAccessToken() throws Exception {
        if (token == null || token.isExpired()) {
            login();
        }
        return token.getAccessToken();
    }

    private String basicAuth() {
        return Base64.getEncoder()
                .encodeToString((config.clientId() + ":" + config.clientSecret())
                        .getBytes(StandardCharsets.UTF_8));
    }
}
