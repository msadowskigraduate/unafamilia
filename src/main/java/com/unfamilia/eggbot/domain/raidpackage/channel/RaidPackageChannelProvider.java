package com.unfamilia.eggbot.domain.raidpackage.channel;

import com.unfamilia.application.ApplicationConfigProvider;
import discord4j.core.object.entity.channel.TextChannel;
import lombok.Data;

import javax.enterprise.context.ApplicationScoped;

@Data
@ApplicationScoped
public class RaidPackageChannelProvider {
    private final ApplicationConfigProvider configProvider;
    private TextChannel initializeOrderChannel;
    private TextChannel confirmOrderChannel;

    public String getOrderInitName() {
        return configProvider.discord().raidpackage().initializeOrder();
    }

    public String getOrderConfirmedName() {
        return configProvider.discord().raidpackage().confirmOrder();
    }

    public TextChannel getConfirmOrderChannel() {
        return confirmOrderChannel;
    }

    public void setConfirmOrderChannel(TextChannel confirmOrderChannel) {
        this.confirmOrderChannel = confirmOrderChannel;
    }

    public TextChannel getInitializeOrderChannel() {
        return initializeOrderChannel;
    }

    public void setInitializeOrderChannel(TextChannel initializeOrderChannel) {
        this.initializeOrderChannel = initializeOrderChannel;
    }
}
