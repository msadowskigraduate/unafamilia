package com.unfamilia.eggbot.infrastructure.discord.events.discordcommands;

import com.unfamilia.eggbot.infrastructure.WoWProfileClient;
import com.unfamilia.eggbot.infrastructure.wowapi.model.Character;
import com.unfamilia.eggbot.infrastructure.wowapi.model.CharacterMedia;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import io.quarkus.logging.Log;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class AddCharacterCommandHandler extends DiscordCommandHandler {
    private final WoWProfileClient woWProfileClient;

    @Override
    ApplicationCommandRequest build() {
        return ApplicationCommandRequest.builder()
                .name(getCommand())
                .description("Add your character to tracking!")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("realm")
                        .description("Realm Slug")
                        .required(true)
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name("name")
                        .description("Character Name")
                        .required(true)
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .build())
                .build();
    }

    @Override
    public String getCommand() {
        return "addchar";
    }

    @Override
    public Mono handle(Event event) {
        ChatInputInteractionEvent slashCommand = (ChatInputInteractionEvent) event;
        Log.info("Handling event: " + event);
        slashCommand.reply("Processing...").subscribe();

        Map<String, String> options = slashCommand.getOptions().stream()
                .collect(Collectors.toMap(ApplicationCommandInteractionOption::getName, value -> value.getValue().get().asString()));

        try {
            Character character = woWProfileClient.queryCharacterProfile(options.get("realm"), options.get("name"));
            CharacterMedia media = woWProfileClient.queryCharacterMedia(character);

            return slashCommand.editReply(
                    InteractionReplyEditSpec.builder()
                            .contentOrNull(null)
                            .addEmbed(embedBuilder(character, media)
                                    .build())
                            .build());
        } catch (RuntimeException e) {
            return slashCommand.deleteReply();
        }
    }

    private EmbedCreateSpec.Builder embedBuilder(Character character, CharacterMedia characterMedia) {
        String imageUrl = characterMedia.getAssets()
                .stream()
                .filter(asset -> asset.getKey().equalsIgnoreCase("inset"))
                .findFirst().get().getValue();

        var builder = EmbedCreateSpec.builder()
                .title(character.getName())
                .color(Color.BISMARK)
                .timestamp(Instant.now())
                .image(imageUrl)
                .addField("Class", character.getCharacterClass().getName(), false)
                .addField("Specialization", character.getSpecializations().getHref(), false)
                .addField("Level", character.getLevel().toString(), false);
        return builder;
    }
}
