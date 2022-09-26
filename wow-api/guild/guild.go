package guild

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"unafamilia/wow-api/core"
	"unafamilia/wow-api/model"

	"github.com/FuzzyStatic/blizzard/v3"
	"github.com/FuzzyStatic/blizzard/wowp"
	rejson "github.com/nitishm/go-rejson/v4"
)

func QueryGuildRoster(guildName string, guildRealm string, client *blizzard.Client, redisClient *rejson.Handler) *model.GuildRoster {
	var key = fmt.Sprintf("guild:%s:realm:%s", guildName, guildRealm)
	rosterJSON, err := redisClient.JSONGet(key, ".")

	if err != nil {
		fmt.Printf("%s does not exist. Querying...\n", key)
		return queryGuildRoster(guildName, guildRealm, client, redisClient)
	}

	roster := model.GuildRoster{}
	err = json.Unmarshal(rosterJSON.([]byte), &roster)
	if err != nil {
		log.Fatalf("Failed to JSON Unmarshal")
	}

	return &roster
}

func (job GuildUpdate) Run() {
	queryGuildRoster(job.GuildName, job.GuildRealm, job.Client, job.ReJsonHandler)
}

func queryGuildRoster(guildName string, guildRealm string, client *blizzard.Client, redisClient *rejson.Handler) *model.GuildRoster {
	var key = fmt.Sprintf("guild:%s:realm:%s", guildName, guildRealm)
	roster, _, err := client.WoWGuildRoster(context.Background(), guildRealm, guildName)
	parsedRoster := mapGuildRoster(roster)
	if err != nil {
		fmt.Println(err)
	}

	res, err := redisClient.JSONSet(key, ".", parsedRoster)
	fmt.Printf("Quering data for: %s-%s finshed with result: %s. \n", guildName, guildRealm, res)
	if err != nil {
		log.Fatalf(err.Error())
	}

	return parsedRoster
}

func mapGuildRoster(wowroster *wowp.GuildRoster) *model.GuildRoster {
	roster := model.GuildRoster{}
	roster.ID = wowroster.Guild.ID
	roster.Link = wowroster.Links.Self.Href
	roster.Name = wowroster.Guild.Name
	roster.Realm.ID = wowroster.Guild.Realm.ID
	roster.Realm.Name = wowroster.Guild.Realm.Name
	roster.Realm.Slug = wowroster.Guild.Realm.Slug

	var members []model.Member

	for _, member := range wowroster.Members {
		class := model.CharacterClass{
			CharacterClassID: member.Character.PlayableClass.ID,
			CharacterClass:   *core.GetClassNameForID(member.Character.PlayableClass.ID),
		}

		members = append(members, model.Member{
			Name:           member.Character.Name,
			ID:             int64(member.Character.ID),
			Level:          member.Character.Level,
			CharacterClass: class,
			Race:           model.Race{RaceID: member.Character.PlayableRace.ID},
			Rank:           member.Rank,
			RealmSlug:      member.Character.Realm.Slug,
		})
	}
	roster.Members = members
	return &roster
}

type GuildUpdate struct {
	GuildName     string
	GuildRealm    string
	Client        *blizzard.Client
	ReJsonHandler *rejson.Handler
}
