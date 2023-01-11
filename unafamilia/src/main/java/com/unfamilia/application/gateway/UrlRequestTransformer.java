package com.unfamilia.application.gateway;

import java.net.MalformedURLException;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.unfamilia.application.ApplicationConfigProvider;

@ApplicationScoped
class UrlRequestTransformer {
    @Inject ApplicationConfigProvider provider;

    public HttpRequest transform(UriInfo uriInfo, HttpHeaders headers, Request request, String bodyString) throws MalformedURLException {
        var pathParameters = uriInfo.getPathSegments();
        var systemName = provider.services().get(pathParameters.get(1).getPath());
        Optional.ofNullable(systemName).orElseThrow(() -> new MalformedURLException());
        
        UriBuilder uriBuilder = UriBuilder.fromPath(systemName);
        pathParameters.subList(2, pathParameters.size())
            .forEach(segment -> uriBuilder.path(segment.getPath()));
        
        uriInfo.getQueryParameters()
            .forEach((key, values) -> {
                values.forEach(value -> uriBuilder.queryParam(key, value));
            });

        var requestBuilder = HttpRequest.newBuilder(uriBuilder.build());

        switch (request.getMethod()) {
            case "GET" -> requestBuilder.GET();
            case "POST" -> requestBuilder.POST(BodyPublishers.ofString(bodyString));
        }

        headers.getRequestHeaders()
            .entrySet()
            .stream()
            .filter(entry -> entry.getKey().startsWith("Authentication") || entry.getKey().startsWith("Content-Type") || entry.getKey().startsWith("Accepts"))
            .forEach(entry-> requestBuilder.header(entry.getKey(), entry.getValue().stream().reduce("", String::concat)));

        return requestBuilder.build();
    }
}
