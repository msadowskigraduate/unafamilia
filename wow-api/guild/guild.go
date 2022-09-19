package guild

import (
	"context"
	"encoding/json"
	"fmt"
	"log"

	"github.com/FuzzyStatic/blizzard/v3"
	"github.com/FuzzyStatic/blizzard/wowp"
	rejson "github.com/nitishm/go-rejson/v4"
)

func QueryGuildRoster(guildName string, guildRealm string, client *blizzard.Client, redisClient *rejson.Handler) *wowp.GuildRoster {
	var key = fmt.Sprintf("guild:%s:realm:%s", guildName, guildRealm)
	rosterJSON, err := redisClient.JSONGet(key, ".")

	if err != nil {
		fmt.Printf("%s does not exist. Querying...", key)
		return queryGuildRoster(guildName, guildRealm, client, redisClient)
	}

	roster := wowp.GuildRoster{}
	err = json.Unmarshal(rosterJSON.([]byte), &roster)
	if err != nil {
		log.Fatalf("Failed to JSON Unmarshal")
	}

	return &roster
}

func (job GuildUpdate) Run() {
	queryGuildRoster(job.GuildName, job.GuildRealm, job.Client, job.ReJsonHandler)
}

func queryGuildRoster(guildName string, guildRealm string, client *blizzard.Client, redisClient *rejson.Handler) *wowp.GuildRoster {
	var key = fmt.Sprintf("guild:%s:realm:%s", guildName, guildRealm)
	roster, _, err := client.WoWGuildRoster(context.Background(), guildRealm, guildName)

	if err != nil {
		fmt.Println(err)
	}

	res, err := redisClient.JSONSet(key, ".", roster)
	fmt.Println(res)
	if err != nil {
		log.Fatalf(err.Error())
	}

	return roster
}

type GuildUpdate struct {
	GuildName     string
	GuildRealm    string
	Client        *blizzard.Client
	ReJsonHandler *rejson.Handler
}
