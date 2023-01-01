package com.unfamilia.application.audit.query;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.unfamilia.application.ApplicationConfigProvider;
import com.unfamilia.application.query.QueryHandler;
import com.unfamilia.eggbot.infrastructure.wcl.WarcraftLogsAdapter;
import com.unfamilia.eggbot.infrastructure.wcl.WclWebToken;

import io.quarkus.logging.Log;


@ApplicationScoped
public class QueryGuildReportRankingHandler implements QueryHandler<Response, QueryGuildReportRanking> {
    @Inject @RestClient WarcraftLogsAdapter adapter;
    @Inject ApplicationConfigProvider config;
    private WclWebToken token;

    @Override
    public Class<QueryGuildReportRanking> supports() {
        return QueryGuildReportRanking.class;
    }

    @Override
    public Response handle(QueryGuildReportRanking query) {
        if(token == null || token.isExpired()) {
            var form = new Form()
                .param("grant_type", "client_credentials")
                .asMap();
            token = adapter.generateToken(String.format("Basic %s", encodeBasicAuth()), form);
        }

        return adapter.queryGraphQlClient(String.format("Bearer %s", token.getAccessToken()), query.QUERY);
    }
    
    private String encodeBasicAuth() {
        return Base64.getEncoder().encodeToString((config.wclProvider().clientId() + ":" + config.wclProvider().clientSecret()).getBytes(StandardCharsets.UTF_8));
    }
}
