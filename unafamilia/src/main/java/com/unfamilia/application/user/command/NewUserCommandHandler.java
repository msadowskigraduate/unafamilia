package com.unfamilia.application.user.command;

import com.unfamilia.application.command.CommandHandler;
import com.unfamilia.application.user.Character;
import com.unfamilia.application.user.User;
import com.unfamilia.application.user.UserNotFromGuildException;
import com.unfamilia.eggbot.infrastructure.WoWProfileClient;
import com.unfamilia.eggbot.infrastructure.wowguild.WoWApiGuildAdapter;
import com.unfamilia.eggbot.infrastructure.wowguild.model.WoWGuildRosterResponse;
import io.quarkus.logging.Log;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class NewUserCommandHandler implements CommandHandler<NewUserCommand> {
    @Inject
    WoWProfileClient client;

    @Inject
    @RestClient
    WoWApiGuildAdapter guild;

    @Override
    public boolean supports(NewUserCommand command) {
        return command != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.unfamilia.application.command.CommandHandler#handle(com.unfamilia.
     * application.command.Command)
     * Creates new User record.
     */
    @Override
    @Transactional
    public void handle(NewUserCommand command) throws UserNotFromGuildException {
        var wowProfile = client.queryWowProfile(command.accessToken());
        var response = guild.queryGuildRoster("magtheridon", "una-familia");
        Optional<WoWGuildRosterResponse.Member> min = response.members().stream()
                .filter(member -> wowProfile.getWowAccounts().stream()
                        .flatMap(wowAccount -> wowAccount.getCharacters().stream())
                        .anyMatch(character -> character.getId().equals(member.id())))
                // .peek(member -> Log.info(member.name()))
                .min(Comparator.comparing(WoWGuildRosterResponse.Member::rank));

        if (min.isPresent()) {
            Log.info("Creating user: " + command.battleTag());

            var characters = wowProfile.getWowAccounts().stream()
                    .flatMap(wowAccount -> wowAccount.getCharacters().stream())
                    .map(character -> new Character(character.getId(), character.getName(),
                            character.getRealm().getSlug()))
                    .peek(entity -> entity.persist()).collect(Collectors.toList());

            var user = new User();
            user.setBattleNetUserId(command.wowProfileId());
            user.setName(command.battleTag());
            user.setDiscordUserId(command.discordUserId());
            user.setRank(min.get().rank());
            user.setCharacters(characters);
            user.persist();
        } else {
            Log.info("User not from the guild!");
            throw new UserNotFromGuildException("Not Authorized");
        }
    }
}
