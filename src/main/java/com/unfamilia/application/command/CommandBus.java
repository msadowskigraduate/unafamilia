package com.unfamilia.application.command;

public interface CommandBus {
    void handle(Command command);
    Object handle(Query command);
}
