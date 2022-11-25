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
	router.GET("/v1/report", func(ctx *gin.Context) {
		characters := wac.QueryWishlist()

		coreUsers := []core.User{}
		for _, characterData := range characters {
			coreUser := *core.QueryUserForCharacter(characterData.Name, characterData.Realm)
			coreUser.InstanceName = characterData.InstanceName
			coreUser.Difficulty = characterData.Difficulty
			coreUser.Wishlist = characterData.WishlistName
			coreUsers = append(coreUsers, coreUser)
		}
		ctx.IndentedJSON(http.StatusOK, coreUsers)
	})

	router.GET("/v1/team", func(ctx *gin.Context) {
		result := wac.QueryRoster()

		ctx.IndentedJSON(http.StatusOK, result)
	})

	router.GET("/v2/report", func(ctx *gin.Context) {
		roster := wac.QueryRoster()
		// report := make(map[string][]wowaudit.Character)
		coreUsers := []core.User{}

		for _, character := range *roster {
			characterWishlist := wac.QueryWishlistForCharacter(character.ID)
			characterData := wowaudit.ParseWishlist(characterWishlist)
			coreUser := *core.QueryUserForCharacter(characterData.Name, characterData.Realm)
			coreUsers = append(coreUsers, coreUser)
		}

		// coreUsers := []core.User{}
		// for _, characterData := range characters {
		// 	coreUser := *core.QueryUserForCharacter(characterData.Name, characterData.Realm)
		// 	coreUser.InstanceName = characterData.InstanceName
		// 	coreUser.Difficulty = characterData.Difficulty
		// 	coreUser.Wishlist = characterData.WishlistName
		// 	coreUsers = append(coreUsers, coreUser)
		// }
		ctx.IndentedJSON(http.StatusOK, coreUsers)
	})

	router.Run(":8080")
}
