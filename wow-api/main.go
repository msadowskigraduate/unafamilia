package main

import (
	"context"
	"fmt"
	"net/http"
	"os"
	"time"
	"unafamilia/wow-api/characters"
	"unafamilia/wow-api/core"
	"unafamilia/wow-api/guild"
	"unafamilia/wow-api/items"

	"github.com/DeanThompson/ginpprof"
	"github.com/FuzzyStatic/blizzard/v3"
	"github.com/bamzi/jobrunner"
	"github.com/gin-gonic/gin"
	"github.com/go-redis/redis/v8"
	"github.com/nitishm/go-rejson/v4"
)

var (
	client *blizzard.Client
	rdb    *redis.Client
	rh     *rejson.Handler
)

func main() {
	router := gin.Default()
	jobrunner.Start()
	jobrunner.Every(time.Minute, guild.GuildUpdate{GuildName: "una-familia", GuildRealm: "magtheridon", Client: client, ReJsonHandler: rh})
	jobrunner.Every(time.Minute, characters.CharacterTracking{Client: client, ReJsonHandler: rh, Redis: rdb})

	//Get Consumables
	router.GET("/consumables", func(ctx *gin.Context) {
		consumables := items.QueryConsumables(client)
		ctx.IndentedJSON(http.StatusOK, consumables)
	})

	//Get Guild Roster
	router.GET("/roster", func(ctx *gin.Context) {
		guildName := ctx.Query("guild_name")
		guildRealm := ctx.Query("guild_realm")
		roster := guild.QueryGuildRoster(guildName, guildRealm, client, rh)
		ctx.IndentedJSON(http.StatusOK, &roster)
	})

	//Add character to Scheduler
	router.POST("/character", func(ctx *gin.Context) {
		var characterRequest characters.CharacterTrackingRequest

		if err := ctx.BindJSON(&characterRequest); err != nil {
			ctx.IndentedJSON(http.StatusBadRequest, core.RestError{Message: "JSON binding error."})
			return
		}

		if err := characters.AddCharacterToTracking(characterRequest.CharacterName, characterRequest.RealmSlug, client, rh, rdb); err != nil {
			ctx.IndentedJSON(http.StatusBadRequest, core.RestError{Message: err.Error()})
			return
		}

		ctx.Status(http.StatusCreated)
	})

	//Query Character Profile Summary
	router.GET("/character", func(ctx *gin.Context) {
		characterName := ctx.Query("character_name")
		characterRealm := ctx.Query("character_realm")
		profile := characters.QueryCharacter(characterName, characterRealm, client, rh)
		ctx.IndentedJSON(http.StatusOK, &profile)
	})

	router.LoadHTMLGlob("./views/Status.html")
	router.GET("/jobs", JobHtml)
	ginpprof.Wrap(router)
	router.Run(":8080")
}

func init() {
	var err error
	rh = rejson.NewReJSONHandler()
	fmt.Println("[APPLICATION] Initializing Redis Client...")
	rdb = redis.NewClient(&redis.Options{
		Addr:     os.Getenv("REDIS_URL"),
		Password: "", // no password set
		DB:       0,  // use default DB
	})

	rh.SetGoRedisClient(rdb)
	fmt.Println("[APPLICATION] Initializing Battle.Net Client...")

	clientId := os.Getenv("APPLICATION_WOW_ID")
	clientSecret := os.Getenv("APPLICATION_WOW_SECRET")

	if clientId == "" || clientSecret == "" {
		panic("Export APPLICATION_WOW_ID and APPLICATION_WOW_SECRET!")
	}

	client, err = blizzard.NewClient(blizzard.Config{
		ClientID:     clientId,
		ClientSecret: clientSecret,
		HTTPClient:   http.DefaultClient,
		Region:       blizzard.EU,
		Locale:       blizzard.EnGB,
	})

	if err != nil {
		fmt.Println(err)
	}

	client.AccessTokenRequest(context.Background())
}

func JobHtml(c *gin.Context) {
	c.HTML(200, "Status.html", jobrunner.StatusPage())
}
