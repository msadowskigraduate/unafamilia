package com.unfamilia.application.user.model;

import com.unfamilia.eggbot.domain.character.Character;
import com.unfamilia.eggbot.domain.player.Player;
import com.unfamilia.eggbot.domain.player.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class User {
    private String name;
    private List<Character> characters;
    private Long userId;
    private boolean isDiscordUser;
    private boolean isARaider;
    private boolean hasAMainCharacter;
    private Character mainCharacter;
    private List<String> roles;

    public static User from(Player player) {
        return User.builder()
                .name(player.getBattleTag())
                .userId(player.getId())
                .isDiscordUser(player.hasLinkedWithDiscord())
                .isARaider(player.getRole().stream().anyMatch(x -> x.getName().equals("Raider")))
                .hasAMainCharacter(player.getMainCharacter() != null)
                .mainCharacter(player.getMainCharacter())
                .characters(player.getCharacters())
                .roles( player.getRole().stream().map(Role::getName).collect(toList()))
                .build();
    }
}
