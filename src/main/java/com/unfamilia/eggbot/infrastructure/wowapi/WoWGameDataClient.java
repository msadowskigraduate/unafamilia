package com.unfamilia.eggbot.infrastructure.wowapi;

import com.unfamilia.eggbot.domain.wowtoken.WoWToken;
import com.unfamilia.eggbot.infrastructure.wowapi.model.WowItem;
import io.vertx.core.json.Json;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@ApplicationScoped
public class WoWGameDataClient extends WoWApiClient {
    private static final String WOW_GAME_DATA_BASE = PROTOCOL + DEFAULT_REGION + ".api.blizzard.com/data/wow";

    public WoWToken getWoWTokenPrice() throws Exception {
        var accessToken = this.getAccessToken();

        var uri = UriBuilder.fromUri(WOW_GAME_DATA_BASE + "/token/index")
                .queryParam(NAMESPACE, "dynamic-eu")
                .queryParam(LOCALE, "en_GB")
                .queryParam(ACCESS_TOKEN, accessToken)
                .build();

        return Json.decodeValue(get(uri).body(), WoWToken.class);
    }

    public WowItem getWowItem(Long id) {
        String accessToken = null;
        try {
            accessToken = this.getAccessToken();
            var uri = UriBuilder.fromUri(WOW_GAME_DATA_BASE + "/item/" + id)
                    .queryParam(NAMESPACE, "static-eu")
                    .queryParam(LOCALE, "en_GB")
                    .queryParam(ACCESS_TOKEN, accessToken)
                    .build();
            return Json.decodeValue(get(uri).body(), WowItem.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private HttpResponse<String> get(URI uri) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        return this.client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
