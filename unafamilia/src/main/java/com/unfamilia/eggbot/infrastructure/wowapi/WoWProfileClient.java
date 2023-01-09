package com.unfamilia.eggbot.infrastructure.wowapi;

import com.unfamilia.eggbot.infrastructure.wowapi.model.WowProfile;

public interface WoWProfileClient {
    /**
     * Returns a summary of the media assets available for a character (such as an avatar render).
     * @param authCode authorization Flow Code.
     * @return WoW Profile.
     */
    WowProfile queryWowProfile(String authCode);
}
