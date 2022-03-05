package com.unfamilia.application.command;

import lombok.RequiredArgsConstructor;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor
public class CommandBusImpl implements CommandBus {
    private final Instance<CommandHandler> handlers;
    private final Instance<QueryHandler> queries;

    @Override
    public void handle(Command command) {
        handlers.stream()
                .filter(handlers -> handlers.supports(command))
                .forEach(handler -> handler.handle(command));
    }

    @Override
    public Object handle(Query query) {
        return queries.stream()
                .filter(handlers -> handlers.supports(query))
                .findFirst()
                .get().handle(query);
    }
}
