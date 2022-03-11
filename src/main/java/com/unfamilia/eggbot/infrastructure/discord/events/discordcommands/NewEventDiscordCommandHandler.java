package com.unfamilia.eggbot.infrastructure.discord.events.discordcommands;

import com.unfamilia.application.query.QueryBus;
import com.unfamilia.application.scheduler.NotifyUsersTimer;
import com.unfamilia.eggbot.domain.event.EventQuery;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class NewEventDiscordCommandHandler extends DiscordCommandHandler {
    private final QueryBus queryBus;

    @Override
    @Transactional
    ApplicationCommandRequest build() {
        var name = ApplicationCommandOptionData.builder()
                .name("name")
                .description("Event Name")
                .type(ApplicationCommandOption.Type.STRING.getValue())
                .required(true)
                .build();
        var activity = ApplicationCommandOptionData.builder()
                .name("activity")
                .description("Activity Name")
                .type(ApplicationCommandOption.Type.STRING.getValue())
                .required(true)
                .build();
        var role = ApplicationCommandOptionData.builder()
                .name("role")
                .description("Role name")
                .type(ApplicationCommandOption.Type.STRING.getValue())
                .required(true)
                .build();
        var date = ApplicationCommandOptionData.builder()
                .name("date")
                .description("dd-mm-yyyy")
                .type(ApplicationCommandOption.Type.STRING.getValue())
                .required(true)
                .build();
        var time = ApplicationCommandOptionData.builder()
                .name("time")
                .description("19:30")
                .type(ApplicationCommandOption.Type.STRING.getValue())
                .required(true)
                .build();

        return ApplicationCommandRequest.builder()
                .name(getCommand())
                .description("Create raid or any other event!")
                .addOption(name)
                .addOption(activity)
                .addOption(role)
                .addOption(date)
                .addOption(time)
                .build();
    }

    @Override
    public String getCommand() {
        return "event";
    }

    @Override
    public Mono handle(Event event) {
        var slashCommand = (ChatInputInteractionEvent) event;
        var optionMap = slashCommand.getOptions().stream()
                .collect(Collectors.toMap(ApplicationCommandInteractionOption::getName, ApplicationCommandInteractionOption::getValue));

        var eventCommand = EventQuery.of(
                getValue(optionMap, "name"),
                getValue(optionMap, "activity"),
                getValue(optionMap, "role"),
                parseTime(
                        getValue(optionMap, "date"),
                        getValue(optionMap, "time")
                ),
                slashCommand.getInteraction().getUser()
        );

        var entity = queryBus.handle(eventCommand);

        return slashCommand
                .reply()
                .withEmbeds(EmbedCreateSpec.builder()
                        .title(entity.getName())
                        .description(entity.getActivity())
                        .addField("Date:", entity.getDate().toString(), true)
                        .addAllFields(
                                entity.getCharacters().stream()
                                        .map(x -> EmbedCreateFields.Field.of(x.getName(), x.getCharacterClassName(), false))
                                        .collect(Collectors.toList())
                        )
                        .build());
    }

    private String getValue(Map<String, Optional<ApplicationCommandInteractionOptionValue>> map, String key) {
        if (map.get(key).isPresent()) {
            return map.get(key).get().asString().trim();
        }

        return null;
    }

    private LocalDateTime parseTime(String date, String time) {
        if (date == null || time == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return LocalDateTime.parse(date + " " + time, formatter);
    }
}
