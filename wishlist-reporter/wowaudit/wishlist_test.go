package wowaudit

import (
	"os"
	"testing"
)

// func TestGenerateReport(t *testing.T) {
// 	jsonFile, err := os.Open("../resources/results.json")
// 	if err != nil {
// 		fmt.Println(err)
// 	}
// 	defer jsonFile.Close()

// 	var wishlist Wishlist
// 	err = json.Unmarshal(body, &wishlist)

// 	if err != nil {
// 		log.Fatalf(err.Error())
// 	}

// 	characters := generateReport(wishlist)

// 	if len(Characters) == 0 {
// 		t.Errorf("Yikes! Test failed!")
// 	}
// }

func TestQueryRoster(t *testing.T) {
	wowauditApiKey := os.Getenv("WOWAUDIT_API_KEY")

	client := NewWowAuditClient(wowauditApiKey)

	result := client.QueryRoster()

	if len(*result) == 0 {
		t.Errorf("Failed!")
	}
}
