package com.unfamilia.application.command;

public interface CommandHandler {
    boolean supports(Command command);
    void handle(Command command);
}
