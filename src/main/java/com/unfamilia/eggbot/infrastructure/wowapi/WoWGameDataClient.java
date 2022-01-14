package com.unfamilia.eggbot.infrastructure.wowapi;

import com.unfamilia.eggbot.domain.wowtoken.WoWToken;
import io.vertx.core.json.Json;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ApplicationScoped
@RequiredArgsConstructor
public class WoWGameDataClient {
    private final WoWApiClient client;

    public WoWToken getWoWTokenPrice() throws Exception {
        var accessToken = client.getAccessToken();

        var uri = UriBuilder.fromUri("https://eu.api.blizzard.com/data/wow/token/index")
                .queryParam("namespace", "dynamic-eu")
                .queryParam("locale", "en_GB")
                .queryParam("access_token", accessToken)
                .build();

        var request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.getClient().send(request, HttpResponse.BodyHandlers.ofString());
        return Json.decodeValue(response.body(), WoWToken.class);
    }
}
