package main

import (
	"net/http"
	"os"
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

		for _, character := range *roster {
			cwish := wac.QueryWishlistForCharacter(character.ID)
			cdata := wowaudit.ParseWishlist(cwish)
			if len(cdata.Issues) == 0 {
				continue
			}

			cuser, err := core.QueryUserForCharacter(cdata.Name, cdata.Realm)

			if err != nil {
				coreUsers = append(coreUsers, Report{Name: cuser.Name, CharacterName: cwish.Name, DiscordUserId: cuser.DiscordUserId, BattleNetUserId: cuser.BattleNetUserId, Rank: cuser.Rank, Error: err.Error(), Issues: cdata.Issues})
				continue
			} else {
				coreUsers = append(coreUsers, Report{Name: cuser.Name, CharacterName: cwish.Name, DiscordUserId: cuser.DiscordUserId, BattleNetUserId: cuser.BattleNetUserId, Rank: cuser.Rank, Issues: cdata.Issues})
			}
		}

		ctx.IndentedJSON(http.StatusOK, coreUsers)
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
