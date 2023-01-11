package com.unfamilia.application.gateway;

import java.io.IOException;
import java.net.http.HttpResponse;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.unfamilia.eggbot.infrastructure.utilities.DefaultHttpClient;

import io.quarkus.logging.Log;
import io.quarkus.security.Authenticated;

@Path("/api")
@Authenticated
public class ApiGateway {
    @Inject UrlRequestTransformer transformer;
    @Inject DefaultHttpClient client;
    
    @GET
    @POST
    @Path("/{s:.*}")
    public Response proxyRequest(@Context UriInfo uriInfo, @Context HttpHeaders headers, @Context Request request, String body) throws IOException, InterruptedException {
        var proxyRequest = transformer.transform(uriInfo, headers, request, body);
        Log.info(String.format("Forwarding request from API Gateway to: %s", proxyRequest.uri().toString()));
        var proxyResponse = client.getClient()
            .send(proxyRequest, HttpResponse.BodyHandlers.ofString());
        var responseBuilder = Response
            .status(proxyResponse.statusCode())
            .entity(proxyResponse.body());
        
            //Proxy Headers
        proxyResponse.headers().map().entrySet().stream()
            .forEach(header -> responseBuilder.header(header.getKey(), header.getValue().get(0)));

        return responseBuilder.build();
    }   
}
