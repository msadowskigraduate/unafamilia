package wowaudit

import (
	"encoding/json"
	"fmt"
	"io"
	"log"
	"net/http"
	"time"
)

type Wishlist struct {
	Characters []struct {
		ID        int    `json:"id"`
		Name      string `json:"name"`
		Realm     string `json:"realm"`
		Wishlists []struct {
			Name         string `json:"name"`
			FightStyle   string `json:"fight_style,omitempty"`
			AllowSockets bool   `json:"allow_sockets,omitempty"`
			Weight       int    `json:"weight,omitempty"`
			Instances    []struct {
				ID           int    `json:"id"`
				Name         string `json:"name"`
				Difficulties []struct {
					Difficulty string `json:"difficulty"`
					Wishlist   struct {
						Wishlist struct {
							TotalPercentage float32      `json:"total_percentage"`
							Encounters      []Encounters `json:"encounters"`
						} `json:"wishlist"`
					} `json:"wishlist"`
				} `json:"difficulties"`
			} `json:"instances"`
		} `json:"wishlists"`
	} `json:"characters"`
}

type Encounters struct {
	Name                string  `json:"name"`
	EncounterPercentage float32 `json:"encounter_percentage"`
	Items               []struct {
		Name string `json:"name"`
		ID   int    `json:"id"`
		Slot string `json:"slot"`
		// Score       float64 `json:"score"`
		Alternative struct {
			Item      int     `json:"item"`
			Upgrade   float32 `json:"upgrade"`
			ItemLevel int     `json:"item_level"`
		} `json:"alternative"`
		Wishes []struct {
			Specialization string  `json:"specialization"`
			Timestamp      string  `json:"timestamp"`
			Weight         int     `json:"weight"`
			Outdated       bool    `json:"outdated"`
			Score          float64 `json:"score"`
		} `json:"wishes"`
	} `json:"items"`
}

type Character struct {
	Name         string
	Realm        string
	InstanceName string
	WishlistName string
	Difficulty   string
}

func QueryWishlist(wowauditApiKey string) *[]Character {
	resp, err := http.Get("https://wowaudit.com/v1/wishlists?api_key=" + wowauditApiKey)
	fmt.Println("Requesting wishlist...")
	if err != nil {
		log.Fatalf(err.Error())
	}

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		log.Fatalf(err.Error())
	}

	var wishlist Wishlist
	err = json.Unmarshal(body, &wishlist)

	if err != nil {
		log.Fatalf(err.Error())
	}

	return generateReport(&wishlist)
}

func generateReport(wishlist *Wishlist) *[]Character {
	charactersToBeNotified := []Character{}
	for _, character := range wishlist.Characters {
		for _, wishlist := range character.Wishlists {
			// There are two wishlists (reports) Single Target and Overall - that is why the records might seem duplicated
			for _, instances := range wishlist.Instances {
				for _, difficulty := range instances.Difficulties {
					if parseEncounters(&difficulty.Wishlist.Wishlist.Encounters, character.Name+"-"+character.Realm) {
						charactersToBeNotified = append(charactersToBeNotified, Character{Name: character.Name, Realm: character.Realm, InstanceName: instances.Name, WishlistName: wishlist.Name, Difficulty: difficulty.Difficulty})
					}
				}
			}
		}
	}

	return &charactersToBeNotified
}

func parseEncounters(encounters *[]Encounters, characterName string) bool {
	hasAtLeastOneItemAsUpgrade := false
	for _, encounter := range *encounters {
		for _, item := range encounter.Items {
			hasAtLeastOneItemAsUpgrade = true
			for _, wish := range item.Wishes {
				if wish.Outdated {
					fmt.Println(item.Name + " is outdated for: " + characterName)
				}

				fmt.Println(item.Name + " timestamp: " + wish.Timestamp)
				parsedTimestamp, err := time.Parse(time.RFC3339, wish.Timestamp)

				if err != nil {
					fmt.Println(err.Error())
				} else {
					if parsedTimestamp.Add(time.Hour * 36).Before(time.Now()) {
						fmt.Println(characterName + " should be notified that his simc is potentially outdated!")
					}
				}
			}
		}
	}

	if !hasAtLeastOneItemAsUpgrade {
		fmt.Println(characterName + " either has no upgrades or sim has not been run!")
	}

	return hasAtLeastOneItemAsUpgrade
}