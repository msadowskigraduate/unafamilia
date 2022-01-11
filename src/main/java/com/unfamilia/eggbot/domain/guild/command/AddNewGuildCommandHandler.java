package com.unfamilia.eggbot.domain.guild.command;

import com.unfamilia.application.command.Command;
import com.unfamilia.application.command.CommandHandler;
import com.unfamilia.eggbot.domain.guild.Guild;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class AddNewGuildCommandHandler implements CommandHandler {
    @Override
    public boolean supports(Command command) {
        return command instanceof AddNewGuildCommand;
    }

    @Override
    public void handle(Command command) {
        Optional<PanacheEntityBase> guild = Guild.findByIdOptional(((AddNewGuildCommand) command).getGuildId());

        if(guild.isPresent()) {
            return;
        }

        new Guild(
                ((AddNewGuildCommand) command).getGuildId(),
                ((AddNewGuildCommand) command).getGuildName(),
                ((AddNewGuildCommand) command).getOriginGuild()
        ).persistAndFlush();
    }
}
