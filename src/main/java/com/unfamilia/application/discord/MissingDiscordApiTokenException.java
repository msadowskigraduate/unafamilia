package com.unfamilia.application.discord;

public class MissingDiscordApiTokenException extends RuntimeException {
    public MissingDiscordApiTokenException() {
        super("Missing Discord Token!");
    }
}
