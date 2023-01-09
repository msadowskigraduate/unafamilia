package rankings

import (
	"time"
	"unafamilia/raid-audit/warcraftlogs"
)

const (
	DDMMYYYY = "02/01/2006"
)

func QueryDpsRankingResults(zoneId int, limit int, client *warcraftlogs.WowAuditClient) *warcraftlogs.ReportRankings {
	page := 1
	var result warcraftlogs.ReportRankings
	date := make(map[string]interface{})

	//Recursion be careful!
	for {
		initial_result := client.QueryGuildReportsRankingsDps(zoneId, limit, page)
		for _, data := range initial_result.ReportData.Reports.Data {
			date_formatted := time.UnixMilli(data.StartTime).Format(DDMMYYYY)

			//If empty data then skip
			if len(data.Rankings.Data) == 0 {
				continue
			}

			//If the same date then continue
			_, exists := date[date_formatted]

			if exists {
				continue
			}

			//If not on raid days then skip
			startTime := time.UnixMilli(data.StartTime).Weekday()

			if startTime != time.Wednesday && startTime != time.Monday {
				continue
			}

			date[date_formatted] = data.Code
			result.ReportData.Reports.Data = append(result.ReportData.Reports.Data, data)

			if len(result.ReportData.Reports.Data) == limit {
				break
			}
		}

		if len(result.ReportData.Reports.Data) >= limit {
			break
		}

		page++

		if initial_result.ReportData.Reports.Last_Page+1 == page {
			break
		}
	}
	return &result
}
