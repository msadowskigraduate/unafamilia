package com.unfamilia.application.query;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
@ApplicationScoped
public class QueryBus {
    private Map<Class<? extends Query>, QueryHandler> registry;

    public QueryBus(Instance<QueryHandler<?, ? extends Query<?>>> queries) {
        registry = new HashMap<>();
        queries.stream()
                .forEach(queryHandler -> register(queryHandler.supports(), queryHandler));
    }

    public void register(Class<? extends Query> query, QueryHandler queryHandler) {
        registry.put(query, queryHandler);
    }

    public <R, Q extends Query<R>> R handle(Q command) {
        QueryHandler<R,Q> handler = registry.get(command.getClass());
        return handler.handle(command);
    }
}
