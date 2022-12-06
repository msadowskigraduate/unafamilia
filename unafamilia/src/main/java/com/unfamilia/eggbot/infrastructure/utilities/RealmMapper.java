package com.unfamilia.eggbot.infrastructure.utilities;

public class RealmMapper {
    public static String toSlug(String realmName) {
        return realmName.toLowerCase().replace(" ", "-");
    }
}
