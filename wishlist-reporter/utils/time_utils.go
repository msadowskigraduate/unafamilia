package utils

import (
	"time"
)

func GetYearAndWeekdayOfLastReset() (year int, week int) {
	// Start from the middle of the year:
	t := time.Now()
	wd := t.Weekday()
	// Roll back to Wednesday:
	for wd != time.Tuesday {
		t = t.AddDate(0, 0, -1)
		wd = t.Weekday()
	}

	return t.ISOWeek()
}
