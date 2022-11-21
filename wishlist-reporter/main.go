package main

import (
	"os"
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
		wowaudit.QueryWishlist(wowauditApiKey)
	})
	router.Run(":8080")
}
