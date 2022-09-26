package com.unfamilia.application.command;

import lombok.RequiredArgsConstructor;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor
public class CommandBusImpl implements CommandBus {
    private final Instance<CommandHandler<? extends Command>> handlers;

    @Override
    public void handle(Command command) throws GenericCommandBusException {
        for (CommandHandler handler : handlers) {
            if (handler.supports(command)) {
                handler.handle(command);
            }
        }
    }
}
