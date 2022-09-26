package characters

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"strings"
	"time"
	"unafamilia/wow-api/model"

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
func QueryCharacter(characterName string, realmSlug string, client *blizzard.Client, redis *rejson.Handler) *model.CharacterProfile {
	var key = fmt.Sprintf("character:%s:realm:%s", characterName, realmSlug)
	characterSummaryJSON, err := redis.JSONGet(key, ".")

	if err != nil {
		fmt.Printf("%s is yet cached. Querying...\n", key)
		return queryCharacter(characterName, realmSlug, client, redis)
	}

	characterSummary := model.CharacterProfile{}
	err = json.Unmarshal(characterSummaryJSON.([]byte), &characterSummary)
	if err != nil {
		log.Fatalf("Failed to JSON Unmarshal: %s\n", err.Error())
	}

	return &characterSummary
}

func queryCharacter(characterName string, realmSlug string, client *blizzard.Client, redis *rejson.Handler) *model.CharacterProfile {
	characterSummary, _, err := client.WoWCharacterProfileSummary(context.Background(), realmSlug, characterName)

	if err != nil {
		fmt.Println(err)
		return nil
	}

	characterEquipment, _, err := client.WoWCharacterEquipmentSummary(context.Background(), realmSlug, characterName)

	if err != nil {
		fmt.Println(err)
		return nil
	}

	parsedCharacterSummary := parseCharacterData(characterSummary, characterEquipment)

	var key = fmt.Sprintf("character:%s:realm:%s", characterName, realmSlug)
	res, err := redis.JSONSet(key, ".", parsedCharacterSummary)
	fmt.Println(res)
	if err != nil {
		log.Fatalf(err.Error())
	}

	return parsedCharacterSummary
}

func parseCharacterData(characterSummary *wowp.CharacterProfileSummary, equipment *wowp.CharacterEquipmentSummary) *model.CharacterProfile {
	var ProfileSummary = model.CharacterProfile{}
	ProfileSummary.ID = characterSummary.ID
	ProfileSummary.Name = characterSummary.Name
	ProfileSummary.Race = model.Race{RaceID: characterSummary.Race.ID, Race: characterSummary.Race.Name}
	ProfileSummary.Gender = characterSummary.Gender.Type
	ProfileSummary.Level = characterSummary.Level
	ProfileSummary.EquippedItemLevel = characterSummary.EquippedItemLevel
	ProfileSummary.AverageItemLevel = characterSummary.AverageItemLevel
	ProfileSummary.AchievementPoint = characterSummary.AchievementPoints
	ProfileSummary.Faction = characterSummary.Faction.Type
	ProfileSummary.Realm = model.Realm{Name: characterSummary.Realm.Name, ID: characterSummary.Realm.ID}
	ProfileSummary.CharacterClass = model.CharacterClass{CharacterClass: characterSummary.CharacterClass.Name, CharacterClassID: characterSummary.CharacterClass.ID}
	ProfileSummary.ActiveSpec = model.ActiveSpec{ActiveSpecID: characterSummary.ActiveSpec.ID, ActiveSpec: characterSummary.ActiveSpec.Name}

	var items []model.EquipmentSlot

	for _, item := range equipment.EquippedItems {
		fmt.Printf("%d-%s-%s \n", item.Item.ID, item.Name, item.Slot.Type)
		parsedItem := &model.EquipmentSlot{
			ItemID:       item.Item.ID,
			Name:         item.Name,
			SlotType:     item.Slot.Type,
			Quality:      item.Quality.Type,
			BonusList:    item.BonusList,
			ItemSubclass: model.ItemSubClass{ID: item.ItemSubclass.ID, Name: item.ItemSubclass.Name},
			ItemClass:    model.ItemClass{ID: item.ItemClass.ID, Name: item.ItemSubclass.Name},
			ItemLevel:    item.Level.Value,
		}

		if item.Sockets != nil {
			parsedItem.Socket = model.Socket{
				ItemID:        item.Sockets[0].Item.ID,
				Name:          item.Sockets[0].Item.Name,
				DisplayString: item.Sockets[0].DisplayString,
				MediaID:       item.Sockets[0].Media.ID,
			}
		}

		if item.Enchantments != nil {
			parsedItem.Enchantment = model.Enchantment{
				ID:             item.Enchantments[0].EnchantmentID,
				DisplayString:  item.Enchantments[0].DisplayString,
				SourceItemID:   item.Enchantments[0].SourceItem.ID,
				SourceItemName: item.Enchantments[0].SourceItem.Name,
				EnchantmentID:  item.Enchantments[0].EnchantmentID,
			}
		}

		items = append(items, *parsedItem)
	}

	//Not yet implemented
	var itemSets []model.EquippedItemSet
	ProfileSummary.Equipment = model.Equipement{EquippedItems: items, EquippedItemSets: itemSets}

	return &ProfileSummary
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
