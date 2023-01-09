package warcraftlogs

import (
	"context"
	"encoding/json"

	"github.com/go-resty/resty/v2"
	"github.com/hasura/go-graphql-client"
	"golang.org/x/oauth2"
)

type WowAuditClient struct {
	httpClient    *resty.Client
	graphQlClient *graphql.Client
	token         *WarcraftLogsToken
	clientID      string
	clientSecret  string
}

func New(ClientID string, ClientSecret string) (*WowAuditClient, *ApiError) {
	client := resty.New()

	resp, err := client.R().
		SetBasicAuth(ClientID, ClientSecret).
		SetMultipartFormData(map[string]string{"grant_type": "client_credentials"}).
		SetHeader("Content-Type", "multipart/form-data").
		SetResult(&WarcraftLogsToken{}).
		SetError(&ApiError{}).
		Post("https://www.warcraftlogs.com/oauth/token")

	if err != nil {
		return nil, resp.Error().(*ApiError)
	}

	token := resp.Result().(*WarcraftLogsToken)

	src := oauth2.StaticTokenSource(
		&oauth2.Token{AccessToken: token.AccessToken},
	)

	httpClient := oauth2.NewClient(context.Background(), src)
	graphQlClient := graphql.NewClient("https://www.warcraftlogs.com/api/v2/client", httpClient)
	return &WowAuditClient{
		httpClient:    client,
		clientID:      ClientID,
		clientSecret:  ClientSecret,
		token:         token,
		graphQlClient: graphQlClient,
	}, nil
}

func (client *WowAuditClient) QueryGuildReports(zoneId int, limit int) any {
	var Query struct {
		ReportData struct {
			Reports struct {
				Data []struct {
					Code      graphql.String
					Title     graphql.String
					StartTime graphql.String
					EndTime   graphql.String
				}
			} `graphql:"reports(guildName: \"Una Familia\", guildServerSlug: \"magtheridon\", guildServerRegion: \"eu\", zoneID: $zoneId, limit: $limit)"`
		}
	}

	variables := map[string]interface{}{
		"zoneId": graphql.Int(zoneId),
		"limit":  graphql.Int(limit),
	}
	resp, err := client.graphQlClient.QueryRaw(context.Background(), &Query, variables)

	if err != nil {
		panic(err)
	}

	var data map[string]interface{}
	json.Unmarshal(resp, &data)
	return data
}

func (client *WowAuditClient) QueryGuildReportsRankingsDps(zoneId int, limit int, page int) ReportRankings {
	var Query struct {
		ReportData struct {
			Reports struct {
				Last_Page graphql.Int
				Data      []struct {
					Code      graphql.String
					Title     graphql.String
					StartTime graphql.String
					Rankings  json.RawMessage `graphql:"rankings(compare: Parses, playerMetric: dps)"`
				}
			} `graphql:"reports(guildName: \"Una Familia\", guildServerSlug: \"magtheridon\", guildServerRegion: \"eu\", zoneID: $zoneId, limit: $limit, page: $page)"`
		}
	}

	variables := map[string]interface{}{
		"zoneId": graphql.Int(zoneId),
		"limit":  graphql.Int(limit),
		"page":   graphql.Int(page),
	}
	resp, err := client.graphQlClient.WithDebug(true).QueryRaw(context.Background(), &Query, variables)

	if err != nil {
		panic(err)
	}
	var data ReportRankings
	json.Unmarshal(resp, &data)
	return data
}

func (client *WowAuditClient) QueryFightsForReport(report string) any {
	var Query struct {
		ReportData struct {
			Report struct {
				Fights []struct {
					Id          graphql.Int
					EncounterID graphql.Int
					Difficulty  graphql.Int
				}
			} `graphql:"report(code: $report)"`
			Zone struct {
				Id         graphql.Int
				Encounters []struct {
					Id   graphql.Int
					Name graphql.String
				}
			}
		}
	}

	variables := map[string]interface{}{
		"report": graphql.String(report),
	}

	resp, err := client.graphQlClient.WithDebug(true).QueryRaw(context.Background(), &Query, variables)

	if err != nil {
		return err
	}

	var data map[string]interface{}
	json.Unmarshal(resp, &data)
	return data
}

func (client *WowAuditClient) QueryDeathsForReport(deathId int, fightIds string, zoneId int) any {
	var Query struct {
		ReportData struct {
			Reports struct {
				Data []struct {
					Code      graphql.String
					Title     graphql.String
					StartTime graphql.String
					Events    struct {
						Data json.RawMessage
					} `graphql:"events(dataType: Deaths, death: $deathId, fightIDs: $fightIds, useAbilityIDs: false, useActorIDs: false)"`
				}
			} `graphql:"reports(guildName: \"Una Familia\", guildServerSlug: \"magtheridon\", guildServerRegion: \"eu\", zoneID: $zoneId, limit: 1)"`
		}
	}

	variables := map[string]interface{}{
		"deathId":  graphql.Int(deathId),
		"fightIDs": graphql.String(fightIds),
		"zoneId":   graphql.Int(zoneId),
	}

	resp, err := client.graphQlClient.WithDebug(true).QueryRaw(context.Background(), &Query, variables)

	if err != nil {
		return err
	}

	var data map[string]interface{}
	json.Unmarshal(resp, &data)
	return data
}
