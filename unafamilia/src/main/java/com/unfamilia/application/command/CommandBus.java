package com.unfamilia.application.command;

public interface CommandBus {
    void handle(Command command) throws GenericCommandBusException;
}
