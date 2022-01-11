package com.unfamilia.application.command;

import lombok.RequiredArgsConstructor;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor
public class CommandBusImpl implements CommandBus {
    private final Instance<CommandHandler> handlers;

    @Override
    public void handle(Command command) {
        handlers.stream()
                .filter(handlers -> handlers.supports(command))
                .forEach(handler -> handler.handle(command));
    }
}
