package items

import (
	"context"
	"fmt"

	"github.com/FuzzyStatic/blizzard/v3"
	"github.com/FuzzyStatic/blizzard/wowgd"
	"github.com/FuzzyStatic/blizzard/wowsearch"
)

type Item struct {
	Id           int    `json:"id"`
	Name         string `json:"name"`
	ItemSubClass string `json:"subclass"`
	ItemClass    string `json:"class"`
	Media        int    `json:"media"`
	ReqLevel     int    `json:"req_level"`
}

type ItemList struct {
	Page              int    `json:"page"`
	PageSize          int    `json:"pageSize"`
	MaxPageSize       int    `json:"maxPageSize"`
	PageCount         int    `json:"pageCount"`
	ResultCountCapped bool   `json:"resultCountCapped"`
	Results           []Item `json:"results"`
}

func QueryConsumables(client *blizzard.Client) *ItemList {
	consumables, _, err := client.WoWItemSearch(context.Background(),
		wowsearch.Page(1),
		wowsearch.PageSize(200),
		wowsearch.OrderBy("level:desc"),
		wowsearch.Field().
			AND("item_class.id", "0").
			AND("item_subclass.id", "5||7||8||2||3||0||1||9").
			MIN("required_level", 50).
			NOT("sell_price", "0").
			NOT("inventory_type.type", "ON_EQUIP||ON_ACQUIRE||QUEST||ON_USE||TO_ACCOUNT||TO_BNETACCOUNT"))

	if err != nil {
		fmt.Println(err)
	}

	if err != nil {
		fmt.Println(err)
	}

	var response = convertItemSearchToItem(consumables)
	return response
}

func convertItemSearchToItem(consumables *wowgd.ItemSearch) (list *ItemList) {
	var response ItemList

	response.Page = consumables.Page
	response.PageCount = consumables.PageCount
	response.MaxPageSize = consumables.MaxPageSize
	response.ResultCountCapped = consumables.ResultCountCapped
	response.PageSize = consumables.PageSize

	var items []Item

	for _, item := range consumables.Results {
		items = append(items, Item{item.Data.ID, item.Data.Name.EnGB, item.Data.ItemSubclass.Name.EnGB, item.Data.ItemClass.Name.EnGB, item.Data.Media.ID, item.Data.RequiredLevel})
	}

	response.Results = items
	return &response
}
