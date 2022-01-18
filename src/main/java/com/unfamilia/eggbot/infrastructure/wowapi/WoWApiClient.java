package com.unfamilia.eggbot.infrastructure.wowapi;

import io.vertx.core.json.Json;

import javax.inject.Inject;
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

abstract class WoWApiClient {
    protected static final String PROTOCOL = "https://";
    protected static final String DEFAULT_REGION = "eu";

    protected static final String ACCESS_TOKEN = "access_token";
    protected static final String NAMESPACE = "namespace";
    protected static final String LOCALE = "locale";


    private final ExecutorService executorService = Executors.newCachedThreadPool();
    protected final HttpClient client = HttpClient.newBuilder()
            .executor(executorService)
            .build();

    @Inject
    protected WoWApiConfig config;
    private WoWApiAccessToken token;

    protected void login() throws Exception {
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
