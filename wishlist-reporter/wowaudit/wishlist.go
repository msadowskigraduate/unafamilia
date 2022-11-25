package wowaudit

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"log"
	"net/http"
	"strconv"
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

type CharacterWishlist struct {
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
						TotalPercentage float64 `json:"total_percentage"`
						Encounters      []struct {
							Name                string  `json:"name"`
							EncounterPercentage float64 `json:"encounter_percentage"`
							Items               []struct {
								Name   string  `json:"name"`
								ID     int     `json:"id"`
								Slot   string  `json:"slot"`
								Score  float64 `json:"score"`
								Wishes []struct {
									Specialization string      `json:"specialization"`
									Timestamp      string      `json:"timestamp"`
									Weight         int         `json:"weight"`
									Upgrade        interface{} `json:"upgrade"`
									Score          float64     `json:"score"`
									ManuallyEdited bool        `json:"manually_edited"`
									Comment        string      `json:"comment"`
									Outdated       struct {
										Old struct {
											ItemLevel int `json:"item_level"`
											ID        int `json:"id"`
										} `json:"old"`
										New struct {
											ItemLevel int `json:"item_level"`
											ID        int `json:"id"`
										} `json:"new"`
									} `json:"outdated"`
								} `json:"wishes"`
								Alternative struct {
									Item      int     `json:"item"`
									Upgrade   float64 `json:"upgrade"`
									ItemLevel int     `json:"item_level"`
								} `json:"alternative"`
							} `json:"items"`
						} `json:"encounters"`
					} `json:"wishlist"`
				} `json:"wishlist"`
			} `json:"difficulties"`
		} `json:"instances"`
	} `json:"wishlists"`
}

type Roster []struct {
	ID            int         `json:"id"`
	Name          string      `json:"name"`
	Realm         string      `json:"realm"`
	Class         string      `json:"class"`
	Role          string      `json:"role"`
	Rank          string      `json:"rank"`
	Status        string      `json:"status"`
	Note          interface{} `json:"note"`
	BlizzardID    int         `json:"blizzard_id"`
	TrackingSince time.Time   `json:"tracking_since"`
}

type Character struct {
	Name         string
	Realm        string
	InstanceName string
	WishlistName string
	Difficulty   string
}

type WowAuditClient struct {
	httpClient *http.Client
	BaseUrl    string
	ApiKey     string
}

func NewWowAuditClient(ApiKey string) (wowAuditClient *WowAuditClient) {
	var wac WowAuditClient
	wac.httpClient = http.DefaultClient
	wac.BaseUrl = "https://wowaudit.com/v1"
	wac.ApiKey = ApiKey
	return &wac
}

func (wac *WowAuditClient) getStructData(ctx context.Context, pathAndQuery string, dat interface{}) (interface{}, error) {
	req, err := http.NewRequestWithContext(ctx, "GET", wac.BaseUrl+pathAndQuery, nil)
	if err != nil {
		return dat, err
	}

	req.Header.Set("Accept", "application/json")

	q := req.URL.Query()
	q.Set("api_key", wac.ApiKey)
	req.URL.RawQuery = q.Encode()

	res, err := wac.httpClient.Do(req)
	if err != nil {
		return dat, err
	}
	defer res.Body.Close()

	body, err := io.ReadAll(res.Body)
	if err != nil {
		return dat, err
	}

	if res.StatusCode != http.StatusOK {
		return dat, errors.New(res.Status)
	}

	err = json.Unmarshal(body, &dat)
	if err != nil {
		return dat, err
	}

	return dat, nil
}

func (wac *WowAuditClient) QueryWishlistForCharacter(characterId int) (wishlist *CharacterWishlist) {
	dat, err := wac.getStructData(context.Background(), "/wishlists/"+strconv.Itoa(characterId), &CharacterWishlist{})

	if err != nil {
		fmt.Errorf(err.Error())
	}

	return dat.(*CharacterWishlist)
}

func (wac *WowAuditClient) QueryRoster() *Roster {
	dat, err := wac.getStructData(context.Background(), "/characters", &Roster{})

	if err != nil {
		fmt.Errorf(err.Error())
	}

	return dat.(*Roster)
}

func (wac *WowAuditClient) QueryWishlist() (characters []Character) {
	resp, err := http.Get("https://wowaudit.com/v1/wishlists?api_key=" + wac.ApiKey)
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
