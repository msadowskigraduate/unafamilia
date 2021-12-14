package com.unfamilia.application.command;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class CommandRegistry {
    private final Map<Class, CommandHandler> commandCache;

    public CommandRegistry(Instance<CommandHandler> commands) {
        this.commandCache = commands.stream()
                .collect(Collectors.toMap(CommandHandler::getClass, e -> e));
    }

    public CommandHandler get(Class commandClass) {
        CommandHandler handler = commandCache.get(commandClass);
        if(handler != null) {
            return handler;
        }

        throw new RuntimeException(String.format("Unsupported Command: %s", commandClass.getName()));
    }
}
