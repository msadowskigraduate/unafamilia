package main

import (
	"net/http"
	"os"
	"strconv"
	"unafamilia/raid-audit/rankings"
	"unafamilia/raid-audit/warcraftlogs"

	"github.com/labstack/echo/v4"
)

func main() {
	warcraftLogsId := os.Getenv("APPLICATION_WCL_ID")
	warcraftlogsSecret := os.Getenv("APPLICATION_WCL_SECRET")

	e := echo.New()
	client, _ := warcraftlogs.New(warcraftLogsId, warcraftlogsSecret)

	e.GET("/v1/reports", func(c echo.Context) error {
		zoneIdParam := c.QueryParam("zone_id")
		limitParam := c.QueryParam("limit")

		var limit, zoneId int
		if limitParam != "" {
			limitValue, error := strconv.Atoi(limitParam)
			if error != nil {
				return c.JSON(http.StatusBadRequest, Error{Error: "Bad Request", ErrorDescription: error.Error(), Hint: "limit must be a valid integer!"})
			}
			limit = limitValue
		}

		if zoneIdParam != "" {
			zoneIdValue, error := strconv.Atoi(zoneIdParam)
			if error != nil {
				return c.JSON(http.StatusBadRequest, Error{Error: "Bad Request", ErrorDescription: error.Error(), Hint: "zone_id must be a valid integer!"})
			}
			zoneId = zoneIdValue
		}

		result := client.QueryGuildReports(zoneId, limit)
		return c.JSON(http.StatusOK, result)
	})

	// GET fights for given report
	e.GET("/v1/fights/:report", func(c echo.Context) error {
		report := c.Param("report")

		result := client.QueryFightsForReport(report)
		return c.JSON(http.StatusOK, result)
	})

	e.GET("/v1/raid/rankings/dps", func(c echo.Context) error {
		zoneIdParam := c.QueryParam("zone_id")
		limitParam := c.QueryParam("limit")

		var limit, zoneId int
		if limitParam != "" {
			limitValue, error := strconv.Atoi(limitParam)
			if error != nil {
				return c.JSON(http.StatusBadRequest, Error{Error: "Bad Request", ErrorDescription: error.Error(), Hint: "limit must be a valid integer!"})
			}
			limit = limitValue
		}

		if zoneIdParam != "" {
			zoneIdValue, error := strconv.Atoi(zoneIdParam)
			if error != nil {
				return c.JSON(http.StatusBadRequest, Error{Error: "Bad Request", ErrorDescription: error.Error(), Hint: "zone_id must be a valid integer!"})
			}
			zoneId = zoneIdValue
		}

		result := rankings.QueryDpsRankingResults(zoneId, limit, client)
		return c.JSON(http.StatusOK, result)
	})

	e.Logger.Fatal(e.Start(":8080"))
}

type Error struct {
	Error            string `json:"error"`
	ErrorDescription string `json:"error_description"`
	Hint             string `json:"hint"`
	Message          string `json:"message"`
}
