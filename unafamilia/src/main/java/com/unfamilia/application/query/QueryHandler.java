package com.unfamilia.application.query;

public interface QueryHandler<R, Q extends Query<R>> {
    Class<Q> supports();
    R handle(Q query);
}
