package com.unfamilia.application.command;

import javax.inject.Singleton;

@Singleton
public class CommandBusImpl implements CommandBus {
    private CommandRegistry registry;


    @Override
    public void handle(Command command) {

    }
}
