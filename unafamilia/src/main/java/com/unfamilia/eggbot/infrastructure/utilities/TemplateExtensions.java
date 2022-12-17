package com.unfamilia.eggbot.infrastructure.utilities;

import com.unfamilia.application.user.UserDto;
import com.unfamilia.eggbot.infrastructure.reporter.WishlistReport;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class TemplateExtensions {
    public static Boolean hasDiscordId(WishlistReport report) {
        return report.discordUserId() == null;
    }

    public static Boolean hasDiscordId(UserDto userDto) {
        return userDto.discordUserId() == null;
    }

    public static Integer numberOfIssues(WishlistReport report) {
        return report.issues() == null ? 0 : report.issues().size();
    }
}
