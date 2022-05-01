package com.unfamilia.eggbot.domain.guild.command;

import com.unfamilia.application.command.Command;
import com.unfamilia.application.command.CommandHandler;
import com.unfamilia.eggbot.domain.guild.Guild;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.NotFoundException;
import java.util.NoSuchElementException;

@ApplicationScoped
@RequiredArgsConstructor
public class SetGuildAsOriginCommandHandler implements CommandHandler {
    @Override
    public boolean supports(Command command) {
        return command instanceof SetGuildAsOriginCommand;
    }

    @Override
    public void handle(Command command) {
        final var id = ((SetGuildAsOriginCommand) command).getGuildId();
        var entityBase = Guild.findByIdOptional(id);

        if(entityBase.isEmpty()) {
            throw new NoSuchElementException("Guild with Id: " + id + " not found!");
        }

        Guild guild = (Guild) entityBase.get();
        guild.setIsOriginGuild(true);
        guild.persist();
    }
}
