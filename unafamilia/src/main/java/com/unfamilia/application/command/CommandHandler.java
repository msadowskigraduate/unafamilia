package com.unfamilia.application.command;

public interface CommandHandler<T extends Command> {
    boolean supports(Command command);
    void handle(T command) throws GenericCommandBusException;
}
