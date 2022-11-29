package main

import (
	"fmt"
	"net/http"
	"os"
	"strconv"
	"unafamilia/wishlist-reporter/core"
	"unafamilia/wishlist-reporter/wowaudit"

	"github.com/gin-gonic/gin"
)

func main() {
	router := gin.Default()

	wowauditApiKey := os.Getenv("WOWAUDIT_API_KEY")

	if wowauditApiKey == "" {
		panic("Missing Environment variable WOWAUDIT_API_KEY!")
	}

	wac := wowaudit.NewWowAuditClient(wowauditApiKey)

	router.GET("/v1/team", func(ctx *gin.Context) {
		result := wac.QueryRoster()

		ctx.IndentedJSON(http.StatusOK, result)
	})

	router.GET("/v1/report", func(ctx *gin.Context) {
		roster := wac.QueryRoster()
		coreUsers := []Report{}

		req := ctx.Request
		req.ParseForm()
		r := req.Form["exclude"]
		var exclude map[string]bool
		for _, difficulty := range r {
			exclude[difficulty] = true
		}

		for _, character := range *roster {
			cwish := wac.QueryWishlistForCharacter(character.ID)
			cdata := wowaudit.ParseWishlist(cwish, exclude)
			if len(cdata.Issues) == 0 {
				continue
			}

			cuser, err := core.QueryUserForCharacter(cdata.Name, core.RealmToSlug(cdata.Realm))

			if err != nil {
				coreUsers = append(coreUsers, Report{Name: cuser.Name, CharacterName: cwish.Name, DiscordUserId: cuser.DiscordUserId, BattleNetUserId: cuser.BattleNetUserId, Rank: cuser.Rank, Error: err.Error(), Issues: cdata.Issues})
				continue
			} else {
				coreUsers = append(coreUsers, Report{Name: cuser.Name, CharacterName: cwish.Name, DiscordUserId: cuser.DiscordUserId, BattleNetUserId: cuser.BattleNetUserId, Rank: cuser.Rank, Issues: cdata.Issues})
			}
		}

		ctx.IndentedJSON(http.StatusOK, coreUsers)
	})

	router.GET("/v2/report", func(ctx *gin.Context) {
		characterId := ctx.Query("character_id")
		intVar, err := strconv.Atoi(characterId)
		if err != nil {
			panic(err.Error())
		}

		req := ctx.Request
		req.ParseForm()
		r := req.Form["exclude"]
		exclude := make(map[string]bool)
		for _, difficulty := range r {
			fmt.Println("Parsing Query parameter: " + difficulty)
			exclude[difficulty] = true
		}

		cwish := wac.QueryWishlistForCharacter(intVar)
		cdata := wowaudit.ParseWishlist(cwish, exclude)
		cuser, err := core.QueryUserForCharacter(cdata.Name, core.RealmToSlug(cdata.Realm))
		if err != nil {
			ctx.IndentedJSON(http.StatusOK, Report{Name: cuser.Name, CharacterName: cwish.Name, DiscordUserId: cuser.DiscordUserId, BattleNetUserId: cuser.BattleNetUserId, Rank: cuser.Rank, Error: err.Error(), Issues: cdata.Issues})
		} else {
			ctx.IndentedJSON(http.StatusOK, Report{Name: cuser.Name, CharacterName: cwish.Name, DiscordUserId: cuser.DiscordUserId, BattleNetUserId: cuser.BattleNetUserId, Rank: cuser.Rank, Issues: cdata.Issues})
		}
	})

	router.Run(":8080")
}

type Report struct {
	Name            string           `json:"user_name,omitempty"`
	CharacterName   string           `json:"name"`
	DiscordUserId   int              `json:"discord_user_id"`
	BattleNetUserId int              `json:"battle_net_user_id,omitempty"`
	Rank            int              `json:"rank,omitempty"`
	Issues          []wowaudit.Issue `json:"issues"`
	Error           string           `json:"error,omitempty"`
}
