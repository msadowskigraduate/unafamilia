package com.unfamilia.application.command;

public abstract class GenericCommandBusException extends Exception {
    public GenericCommandBusException(String message) {
        super(message);
    }
}
