package com.unfamilia.eggbot.infrastructure.wowapi;

import com.unfamilia.eggbot.infrastructure.WoWProfileClient;
import com.unfamilia.eggbot.infrastructure.wowapi.model.Character;
import com.unfamilia.eggbot.infrastructure.wowapi.model.CharacterMedia;
import com.unfamilia.eggbot.infrastructure.wowapi.model.WowProfile;
import io.vertx.core.json.Json;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.http.HttpResponse;

@ApplicationScoped
public class WoWProfileApiClient extends WoWApiClient implements WoWProfileClient {
    private static final String WOW_PROFILE_DATA_BASE = PROFILE_DATA_BASE + "wow";

    @Override
    public Character queryCharacterProfile(String realmSlug, String characterName) {
        String accessToken = null;
        try {
            accessToken = this.getAccessToken();
            var uri = UriBuilder.fromUri(WOW_PROFILE_DATA_BASE + "/character/")
                    .path(realmSlug)
                    .path(characterName)
                    .queryParam(NAMESPACE, "profile-eu")
                    .queryParam(LOCALE, "en_GB")
                    .queryParam(ACCESS_TOKEN, accessToken)
                    .build();
            return Json.decodeValue(get(uri).body(), Character.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CharacterMedia queryCharacterMedia(String realmSlug, String characterName) {
        String accessToken = null;
        try {
            accessToken = this.getAccessToken();
            var uri = UriBuilder.fromUri(WOW_PROFILE_DATA_BASE + "/character/")
                    .path(realmSlug)
                    .path(characterName)
                    .path("/character-media")
                    .queryParam(NAMESPACE, "profile-eu")
                    .queryParam(LOCALE, "en_GB")
                    .queryParam(ACCESS_TOKEN, accessToken)
                    .build();
            return Json.decodeValue(get(uri).body(), CharacterMedia.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public CharacterMedia queryCharacterMedia(Character character) {
        return queryCharacterMedia(character.getRealm().getSlug().toLowerCase(), character.getName().toLowerCase());
    }

    @Override
    public WowProfile queryWowProfile(String authCode) {
        try {
            var uri = UriBuilder.fromUri(PROFILE_DATA_BASE + "user")
                    .path("wow")
                    .queryParam(NAMESPACE, "profile-eu")
                    .queryParam(LOCALE, "en_GB")
                    .queryParam(ACCESS_TOKEN, authCode)
                    .build();
            HttpResponse<String> json = get(uri);
            return Json.decodeValue(json.body(), WowProfile.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
