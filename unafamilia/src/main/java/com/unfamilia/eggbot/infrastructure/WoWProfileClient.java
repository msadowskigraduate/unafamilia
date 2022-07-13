package com.unfamilia.eggbot.infrastructure;

import com.unfamilia.eggbot.infrastructure.wowapi.model.Character;
import com.unfamilia.eggbot.infrastructure.wowapi.model.CharacterMedia;
import com.unfamilia.eggbot.infrastructure.wowapi.model.WowProfile;

public interface WoWProfileClient {

    /**
     * Returns a profile summary for a character.
     * @param realmSlug name of the realm.
     * @param characterName name of the character.
     * @return summary of character.
     */
    Character queryCharacterProfile(String realmSlug, String characterName);

    /**
     * Returns a summary of the media assets available for a character (such as an avatar render).
     * @param realmSlug name of the realm.
     * @param characterName name of the character.
     * @return character media.
     */
    CharacterMedia queryCharacterMedia(String realmSlug, String characterName);

    /**
     * Returns a summary of the media assets available for a character (such as an avatar render).
     * @param character model from {@link WoWProfileClient#queryCharacterProfile(String, String)}.
     * @return character media.
     */
    CharacterMedia queryCharacterMedia(Character character);

    /**
     * Returns a summary of the media assets available for a character (such as an avatar render).
     * @param authCode authorization Flow Code.
     * @return WoW Profile.
     */
    WowProfile queryWowProfile(String authCode);
}
