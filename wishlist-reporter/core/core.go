package core

import (
	"encoding/json"
	"errors"
	"io"
	"log"
	"net/http"
	"os"
	"strings"
)

type User struct {
	Name            string `json:"name"`
	DiscordUserId   int    `json:"discord_user_id"`
	BattleNetUserId int    `json:"battle_net_user_id"`
	Rank            int    `json:"rank"`
	InstanceName    string `json:"instance_name"`
	Difficulty      string `json:"difficulty"`
	Wishlist        string `json:"wishlist"`
}

func QueryUserForCharacter(characterName string, characterRealm string) (*User, error) {
	coreUrl := os.Getenv("CORE_URL")

	if coreUrl == "" {
		panic("Missing Environment variable CORE_URL!")
	}

	resp, err := http.Get(coreUrl + "/user/character?character_name=" + characterName + "&character_realm=" + characterRealm)
	if err != nil {
		log.Fatalf(err.Error())
	}

	if resp.StatusCode == 200 {
		body, err := io.ReadAll(resp.Body)
		if err != nil {
			log.Fatalf(err.Error())
		}
		var user User
		err = json.Unmarshal(body, &user)
		log.Println(user.Name)
		if err != nil {
			log.Fatalf(err.Error())
		}

		return &user, nil
	}

	if resp.StatusCode == 204 {
		return &User{}, errors.New("User not registered within core")
	}

	return &User{}, err
}

func RealmToSlug(realmName string) string {
	if realmName == "" {
		panic("Realm Name cannot be empty")
	}

	lowerRealm := strings.ToLower(realmName)

	return strings.ReplaceAll(lowerRealm, " ", "-")
}
