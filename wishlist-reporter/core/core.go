package core

import (
	"encoding/json"
	"fmt"
	"io"
	"log"
	"net/http"
	"os"
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

func QueryUserForCharacter(characterName string, characterRealm string) *User {
	coreUrl := os.Getenv("CORE_URL")

	if coreUrl == "" {
		panic("Missing Environment variable CORE_URL!")
	}

	resp, err := http.Get(coreUrl + "/user/character?character_name=" + characterName + "&character_realm=" + characterRealm)
	fmt.Println("Requesting user list...")
	if err != nil {
		log.Fatalf(err.Error())
	}

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		log.Fatalf(err.Error())
	}

	var user User
	err = json.Unmarshal(body, &user)

	if err != nil {
		log.Fatalf(err.Error())
	}

	return &user
}
