package com.unfamilia.eggbot.infrastructure.utilities;

import java.net.http.HttpClient;

import javax.inject.Singleton;

@Singleton
public class DefaultHttpClient {
    private final HttpClient client = HttpClient.newBuilder().build();

    public HttpClient getClient() {
        return this.client;
    }
}
