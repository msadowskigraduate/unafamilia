package core

import (
	"fmt"
)

var classes = map[int]string{
	1:  "Warrior",
	2:  "Paladin",
	3:  "Hunter",
	4:  "Rogue",
	5:  "Priest",
	6:  "Death Knight",
	7:  "Shaman",
	8:  "Mage",
	9:  "Warlock",
	10: "Monk",
	11: "Druid",
	12: "Demon Hunter",
	13: "Evoker",
}

type RestError struct {
	Message string `json:"message"`
}

func GetClassNameForID(ID int) *string {
	class, ok := classes[ID]
	if !ok {
		fmt.Printf("No class for given ID: %s", string(rune(ID)))
	}

	return &class
}
