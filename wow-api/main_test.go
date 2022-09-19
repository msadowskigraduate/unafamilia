package main

import (
	"bytes"
	"net/http"
	"net/http/httptest"
	"testing"

	"unafamilia/wow-api/characters"
	"unafamilia/wow-api/core"
	"unafamilia/wow-api/guild"
	"unafamilia/wow-api/items"

	"github.com/gin-gonic/gin"
	"gopkg.in/go-playground/assert.v1"
)

func SetUpRouter() *gin.Engine {
	router := gin.Default()
	return router
}

func TestConsumable(t *testing.T) {
	r := SetUpRouter()
	r.GET("/consumables", func(ctx *gin.Context) {
		consumables := items.QueryConsumables(client)
		ctx.IndentedJSON(http.StatusOK, consumables)
	})

	req, _ := http.NewRequest("GET", "/consumables", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)
}

func TestRoster(t *testing.T) {
	r := SetUpRouter()
	r.GET("/roster", func(ctx *gin.Context) {
		roster := guild.QueryGuildRoster("una-familia", "magtheridon", client, rh)
		ctx.IndentedJSON(http.StatusOK, roster)
	})

	req, _ := http.NewRequest("GET", "/roster", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)
}

func TestCharacterTracking(t *testing.T) {
	//GIVEN
	var jsonData = []byte(`{
		"character_name": "Lockedupnyly",
		"realm_slug": "magtheridon"
	}`)

	r := SetUpRouter()
	r.POST("/character", func(ctx *gin.Context) {
		var characterRequest characters.CharacterTrackingRequest

		if err := ctx.BindJSON(&characterRequest); err != nil {
			ctx.IndentedJSON(http.StatusBadRequest, core.RestError{Message: "JSON binding error."})
		}

		if err := characters.AddCharacterToTracking(characterRequest.CharacterName, characterRequest.RealmSlug, client, rh, rdb); err != nil {
			ctx.IndentedJSON(http.StatusBadRequest, core.RestError{Message: err.Error()})
		}

		ctx.Status(http.StatusCreated)
	})

	req, _ := http.NewRequest("POST", "/character", bytes.NewBuffer(jsonData))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)

	assert.Equal(t, http.StatusCreated, w.Code)
}
