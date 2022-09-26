package com.unfamilia.application.user;

import com.unfamilia.application.command.GenericCommandBusException;

public class UserNotFromGuildException extends GenericCommandBusException {
    public UserNotFromGuildException(String message) {
        super(message);
    }
}
