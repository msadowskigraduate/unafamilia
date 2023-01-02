package main

import (
	"fmt"
	"net/http"
	"os"
	"strconv"
	"unafamilia/wishlist-reporter/utils"
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

	router.GET("/", func(ctx *gin.Context) {
		ctx.Status(200)
	})

	router.GET("/v1/team", func(ctx *gin.Context) {
		result := wac.QueryRoster()

		ctx.IndentedJSON(http.StatusOK, result)
	})

	router.GET("/v1/audit", func(ctx *gin.Context) {
		year, week := utils.GetYearAndWeekdayOfLastReset()
		auditData := wac.QueryHistoricDataForCurrentWeek(year, week)
		ctx.IndentedJSON(http.StatusOK, auditData)
	})

	router.GET("/v2/report", func(ctx *gin.Context) {
		characterId := ctx.Query("character_id")
		intVar, err := strconv.Atoi(characterId)
		if err != nil {
			panic(err.Error())
		}

		req := ctx.Request
		req.ParseForm()
		r := req.Form["include"]
		include := make(map[string]bool)
		for _, difficulty := range r {
			fmt.Println("Parsing Query parameter: " + difficulty)
			include[difficulty] = true
		}

		cwish := wac.QueryWishlistForCharacter(intVar)
		cdata := wowaudit.ParseWishlist(cwish, include)
		if err != nil {
			ctx.IndentedJSON(http.StatusOK, Report{CharacterName: cwish.Name, Error: err.Error(), Issues: cdata.Issues, Realm: cwish.Realm})
		} else {
			ctx.IndentedJSON(http.StatusOK, Report{CharacterName: cwish.Name, Issues: cdata.Issues, Realm: cwish.Realm})
		}
	})

	router.Run(":8080")
}

type Report struct {
	Name            string           `json:"user_name,omitempty"`
	CharacterName   string           `json:"name"`
	Realm           string           `json:"realm"`
	DiscordUserId   int              `json:"discord_user_id,omitempty"`
	BattleNetUserId int              `json:"battle_net_user_id,omitempty"`
	Rank            int              `json:"rank,omitempty"`
	Issues          []wowaudit.Issue `json:"issues"`
	Error           string           `json:"error,omitempty"`
}
