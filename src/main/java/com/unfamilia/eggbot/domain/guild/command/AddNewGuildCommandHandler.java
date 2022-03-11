package com.unfamilia.eggbot.domain.guild.command;

import com.unfamilia.application.command.Command;
import com.unfamilia.application.command.CommandHandler;
import com.unfamilia.eggbot.domain.guild.Guild;
import com.unfamilia.eggbot.domain.player.Role;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class AddNewGuildCommandHandler implements CommandHandler {
    @Override
    public boolean supports(Command command) {
        return command instanceof AddNewGuildCommand;
    }

    @Override
    @Transactional
    public void handle(Command command) {
        Optional<Guild> guild = Guild.findByIdOptional(((AddNewGuildCommand) command).getGuildId());

        if (guild.isPresent()) {
            return;
        }

        new Guild(
                ((AddNewGuildCommand) command).getGuildId(),
                ((AddNewGuildCommand) command).getGuildName(),
                ((AddNewGuildCommand) command).getIsOriginGuild(),
                ((AddNewGuildCommand) command).getRoles().stream()
                        .map(role -> Role.of(role.getId().asLong(), role.getName()))
                        .collect(Collectors.toList())
        ).persistAndFlush();
    }
}
