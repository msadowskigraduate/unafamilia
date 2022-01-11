package com.unfamilia.eggbot.infrastructure.discord;

import discord4j.core.GatewayDiscordClient;
import io.quarkus.logging.Log;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DiscordApplicationProvider {
    private final GatewayDiscordClient client;

    private Long applicationId;

    public DiscordApplicationProvider(GatewayDiscordClient client) {
        this.client = client;
    }

    public Long getApplicationId() {
        Long applicationId = this.client.getRestClient().getApplicationId().block();

        if (applicationId == null) {
            Log.warn("Application Id resolved as null! Something smells fishy here...");
        }

        if(this.applicationId == null) {
            this.applicationId = applicationId;
        }

        return applicationId;
    }
}
