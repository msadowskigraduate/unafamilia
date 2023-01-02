package utils

import (
	"fmt"
	"testing"
)

func TestStartOfReset(t *testing.T) {
	year, week := GetYearAndWeekdayOfLastReset()
	fmt.Println(year)
	fmt.Println(week)
	if year != 2022 {
		t.Errorf("First week of the year is fucked, year should be 2022")
	}

	if week != 52 {
		t.Errorf("First week of the year is fucked")
	}
}
