package com.unfamilia.eggbot.infrastructure.session;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super("Invalid Token. Return to Discord and restart the process.");
    }
}
