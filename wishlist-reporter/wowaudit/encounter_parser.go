package wowaudit

import (
	"fmt"
	"time"
)

func ParseWishlist(character *CharacterWishlist) (characterData *Character) {
	issues := []Issue{}
	for _, wishlist := range character.Wishlists {
		// There are two wishlists (reports) Single Target and Overall - that is why the records might seem duplicated
		for _, instances := range wishlist.Instances {
			for _, difficulty := range instances.Difficulties {
				hasAtLeastOneItemAsUpgrade := false
				for _, encounter := range difficulty.Wishlist.Wishlist.Encounters {
					for _, item := range encounter.Items {
						hasAtLeastOneItemAsUpgrade = true
						for _, wish := range item.Wishes {
							//Item is Outdated
							if wish.Outdated.New.ID > 0 {
								reason := item.Name + " is outdated for: " + character.Name
								issues = append(issues, Issue{Reason: reason, Timestamp: wish.Timestamp, InstanceName: instances.Name, WishlistName: wishlist.Name, Difficulty: difficulty.Difficulty, Item: item.Name})
								continue
							}

							//Item is not Outdated but check the simulation timestamp
							parsedTimestamp, err := time.Parse(time.RFC3339, wish.Timestamp)

							if err != nil {
								fmt.Println(err.Error())
							} else {
								if parsedTimestamp.Add(time.Hour * 36).Before(time.Now()) {
									reason := character.Name + " should be notified that his wishlist: " + wishlist.Name + " is potentially outdated for item: " + item.Name
									issues = append(issues, Issue{Reason: reason, Timestamp: wish.Timestamp, InstanceName: instances.Name, WishlistName: wishlist.Name, Difficulty: difficulty.Difficulty, Item: item.Name})
									continue
								}
							}
						}
					}
				}

				//If there are no reports on any items - sim not run or no updates
				if !hasAtLeastOneItemAsUpgrade {
					reason := character.Name + " either has no upgrades or sim has not been run for " + instances.Name + " on " + difficulty.Difficulty + " difficulty."
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
