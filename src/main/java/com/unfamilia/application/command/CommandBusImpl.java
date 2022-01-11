package com.unfamilia.application.command;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;

@Singleton
public class CommandBusImpl implements CommandBus {
    private final Instance<CommandHandler> handlers;

    public CommandBusImpl(Instance<CommandHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void handle(Command command) {
        handlers.stream()
                .filter(handlers -> handlers.supports(command))
                .forEach(handler -> handler.handle(command));
    }
}
