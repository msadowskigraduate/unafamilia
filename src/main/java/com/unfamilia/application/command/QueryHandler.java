package com.unfamilia.application.command;

public interface QueryHandler {
    boolean supports(Query command);
    Query handle(Query query);
}
