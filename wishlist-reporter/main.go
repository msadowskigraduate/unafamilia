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

	router.GET("/report", func(ctx *gin.Context) {
		characters := *wowaudit.QueryWishlist(wowauditApiKey)

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
	router.Run(":8080")
}
