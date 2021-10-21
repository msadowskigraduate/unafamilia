package com.unfamilia.application.discord;

public class MissingDiscordApiToken extends RuntimeException {
    public MissingDiscordApiToken() {
        super("Missing Discord Token!");
    }
}
