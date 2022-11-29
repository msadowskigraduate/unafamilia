package wowaudit

import (
	"fmt"
	"strings"
	"time"
)

func ParseWishlist(character *CharacterWishlist, exclude map[string]bool) (characterData *Character) {
	issues := []Issue{}
	for _, wishlist := range character.Wishlists {
		if wishlist.Name == "Overall" {
			continue
		}
		// There are two wishlists (reports) Single Target and Overall - that is why the records might seem duplicated
		for _, instances := range wishlist.Instances {
			for _, difficulty := range instances.Difficulties {
				//if the difficulty is excluded
				_, ok := exclude[strings.ToLower(difficulty.Difficulty)]

				if ok {
					fmt.Println("Excluding " + difficulty.Difficulty + " difficulty from parse.")
					continue
				}

				hasAtLeastOneItemAsUpgrade := false
				for _, encounter := range difficulty.Wishlist.Wishlist.Encounters {
					for _, item := range encounter.Items {
						hasAtLeastOneItemAsUpgrade = true
						for _, wish := range item.Wishes {
							//Item is Outdated
							if wish.Outdated.New.ID > 0 {
								reason := "A different item is equipped compared to simc profile."
								issues = append(issues, Issue{Reason: reason, Timestamp: wish.Timestamp, InstanceName: instances.Name, WishlistName: wishlist.Name, Difficulty: difficulty.Difficulty, Item: item.Name})
								continue
							}

							//Item is not Outdated but check the simulation timestamp
							parsedTimestamp, err := time.Parse(time.RFC3339, wish.Timestamp)

							if err != nil {
								fmt.Println(err.Error())
							} else {
								if parsedTimestamp.Add(time.Hour * 36).Before(time.Now()) {
									reason := "Outdated simulation for given item."
									issues = append(issues, Issue{Reason: reason, Timestamp: wish.Timestamp, InstanceName: instances.Name, WishlistName: wishlist.Name, Difficulty: difficulty.Difficulty, Item: item.Name})
									continue
								}
							}
						}
					}
				}

				//If there are no reports on any items - sim not run or no updates
				if !hasAtLeastOneItemAsUpgrade {
					reason := "No upgrades or simulation not run."
					issues = append(issues, Issue{Reason: reason, InstanceName: instances.Name, WishlistName: wishlist.Name, Difficulty: difficulty.Difficulty})
				}
			}
		}
	}

	if len(issues) > 0 {
		return &Character{Name: character.Name, Realm: character.Realm, Issues: issues}
	}

	return &Character{}
}
