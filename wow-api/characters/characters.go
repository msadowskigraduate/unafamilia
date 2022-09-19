package characters

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"strings"
	"time"

	"github.com/FuzzyStatic/blizzard/v3"
	"github.com/FuzzyStatic/blizzard/wowp"
	"github.com/go-redis/redis/v8"
	"github.com/nitishm/go-rejson/v4"
)

func AddCharacterToTracking(characterName string, realmSlug string, client *blizzard.Client, handler *rejson.Handler, redis *redis.Client) error {
	fmt.Printf("[APPLICATION] Tracking character %s-%s.", characterName, realmSlug)
	key := fmt.Sprintf("queue:%s:realm:%s", characterName, realmSlug)
	err := redis.Set(context.TODO(), key, time.Now().Format("2006-01-02 15:04:05"), 0).Err()
	return err
}
func QueryCharacter(characterName string, realmSlug string, client *blizzard.Client, redis *rejson.Handler) *wowp.CharacterProfileSummary {
	var key = fmt.Sprintf("character:%s:realm:%s", characterName, realmSlug)
	characterSummaryJSON, err := redis.JSONGet(key, ".")

	if err != nil {
		fmt.Printf("%s does not exist. Querying...", key)
		return queryCharacter(characterName, realmSlug, client, redis)
	}

	characterSummary := wowp.CharacterProfileSummary{}
	err = json.Unmarshal(characterSummaryJSON.([]byte), &characterSummary)
	if err != nil {
		log.Fatalf("Failed to JSON Unmarshal")
	}

	return &characterSummary
}

func queryCharacter(characterName string, realmSlug string, client *blizzard.Client, redis *rejson.Handler) *wowp.CharacterProfileSummary {
	var key = fmt.Sprintf("character:%s:realm:%s", characterName, realmSlug)
	characterSummary, _, err := client.WoWCharacterProfileSummary(context.Background(), realmSlug, characterName)

	if err != nil {
		fmt.Println(err)
	}

	res, err := redis.JSONSet(key, ".", characterSummary)
	fmt.Println(res)
	if err != nil {
		log.Fatalf(err.Error())
	}

	return characterSummary
}

type CharacterTrackingRequest struct {
	CharacterName string `json:"character_name"`
	RealmSlug     string `json:"realm_slug"`
}

func (job CharacterTracking) Run() {
	var ctx = context.Background()
	iter := job.Redis.Scan(ctx, 0, "queue:*", 0).Iterator()
	for iter.Next(ctx) {
		fmt.Println("keys: ", iter.Val())
		values := strings.Split(iter.Val(), ":realm:")
		characterName := strings.TrimPrefix(values[0], "queue:")
		queryCharacter(characterName, values[1], job.Client, job.ReJsonHandler)
	}

	if err := iter.Err(); err != nil {
		panic(err)
	}
}

type CharacterTracking struct {
	Client        *blizzard.Client
	ReJsonHandler *rejson.Handler
	Redis         *redis.Client
}
