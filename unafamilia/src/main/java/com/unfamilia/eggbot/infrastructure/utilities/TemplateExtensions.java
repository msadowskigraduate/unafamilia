package com.unfamilia.eggbot.infrastructure.utilities;

import com.unfamilia.application.user.UserDto;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class TemplateExtensions {
    public static Boolean hasDiscordId(UserDto user) {
        return user.discordUserId() == null;
    }
}
