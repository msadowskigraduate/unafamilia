package warcraftlogs

type ApiError struct {
	Error            string `json:"error"`
	ErrorDescription string `json:"error_description"`
	Hint             string `json:"hint"`
	Message          string `json:"message"`
}

type WarcraftLogsToken struct {
	TokenType   string `json:"token_type"`
	ExpiresIn   int    `json:"expires_in"`
	AccessToken string `json:"access_token"`
}

type ClassResources struct {
	Amount int `json:"amount"`
	Max    int `json:"max"`
	Type   int `json:"type"`
}

type DeathEvent struct {
	Timestamp      int            `json:"timestamp"`
	Type           string         `json:"type"`
	SourceI        Source         `json:"source"`
	Target         Target         `json:"target"`
	Ability        Ability        `json:"ability"`
	Fight          int            `json:"fight"`
	KillerID       int            `json:"killerID"`
	KillingAbility KillingAbility `json:"killingAbility"`
}

type Source struct {
	Name string `json:"name"`
	ID   int    `json:"id"`
	GUID int    `json:"guid"`
	Type string `json:"type"`
	Icon string `json:"icon"`
}

type Target struct {
	Name   string `json:"name"`
	ID     int    `json:"id"`
	GUID   int    `json:"guid"`
	Type   string `json:"type"`
	Icon   string `json:"icon"`
	Server string `json:"server"`
}

type KillingAbility struct {
	Name        string `json:"name"`
	GUID        int    `json:"guid"`
	Type        int    `json:"type"`
	AbilityIcon string `json:"abilityIcon"`
}

type DamageEvent struct {
	Timestamp         int              `json:"timestamp"`
	Type              string           `json:"type"`
	SourceID          int              `json:"sourceID"`
	TargetID          int              `json:"targetID"`
	AbilityGameID     int              `json:"abilityGameID"`
	Fight             int              `json:"fight"`
	Buffs             string           `json:"buffs"`
	HitType           int              `json:"hitType"`
	Amount            int              `json:"amount"`
	Mitigated         int              `json:"mitigated"`
	UnmitigatedAmount int              `json:"unmitigatedAmount"`
	Overkill          int              `json:"overkill"`
	ResourceActor     int              `json:"resourceActor"`
	ClassResources    []ClassResources `json:"classResources"`
	HitPoints         int              `json:"hitPoints"`
	MaxHitPoints      int              `json:"maxHitPoints"`
	AttackPower       int              `json:"attackPower"`
	SpellPower        int              `json:"spellPower"`
	Armor             int              `json:"armor"`
	Absorb            int              `json:"absorb"`
	X                 int              `json:"x"`
	Y                 int              `json:"y"`
	Facing            int              `json:"facing"`
	MapID             int              `json:"mapID"`
	ItemLevel         int              `json:"itemLevel"`
}

type HealEvent struct {
	Timestamp      int              `json:"timestamp"`
	Type           string           `json:"type"`
	SourceID       int              `json:"sourceID"`
	TargetID       int              `json:"targetID"`
	AbilityGameID  int              `json:"abilityGameID"`
	Fight          int              `json:"fight"`
	Buffs          string           `json:"buffs"`
	HitType        int              `json:"hitType"`
	Amount         int              `json:"amount"`
	ResourceActor  int              `json:"resourceActor"`
	ClassResources []ClassResources `json:"classResources"`
	HitPoints      int              `json:"hitPoints"`
	MaxHitPoints   int              `json:"maxHitPoints"`
	AttackPower    int              `json:"attackPower"`
	SpellPower     int              `json:"spellPower"`
	Armor          int              `json:"armor"`
	Absorb         int              `json:"absorb"`
	X              int              `json:"x"`
	Y              int              `json:"y"`
	Facing         int              `json:"facing"`
	MapID          int              `json:"mapID"`
	ItemLevel      int              `json:"itemLevel"`
}

type AbsorbEvent struct {
	Timestamp          int          `json:"timestamp"`
	Type               string       `json:"type"`
	SourceID           int          `json:"sourceID"`
	SourceIsFriendly   bool         `json:"sourceIsFriendly"`
	TargetID           int          `json:"targetID"`
	TargetIsFriendly   bool         `json:"targetIsFriendly"`
	Ability            Ability      `json:"ability"`
	Fight              int          `json:"fight"`
	Buffs              string       `json:"buffs"`
	AttackerID         int          `json:"attackerID"`
	AttackerIsFriendly bool         `json:"attackerIsFriendly"`
	Amount             int          `json:"amount"`
	ExtraAbility       ExtraAbility `json:"extraAbility"`
}

type Ability struct {
	Name        string `json:"name"`
	GUID        int    `json:"guid"`
	Type        int    `json:"type"`
	AbilityIcon string `json:"abilityIcon"`
}

type ExtraAbility struct {
	Name        string `json:"name"`
	GUID        int    `json:"guid"`
	Type        int    `json:"type"`
	AbilityIcon string `json:"abilityIcon"`
}

type ReportRankings struct {
	ReportData struct {
		Reports struct {
			Data []struct {
				Code     string `json:"code"`
				Rankings struct {
					Data []interface{} `json:"data"`
				} `json:"rankings"`
				StartTime int64  `json:"startTime"`
				Title     string `json:"title"`
			} `json:"data"`
			Last_Page int `json:"last_page,omitempty"`
		} `json:"reports"`
	} `json:"reportData"`
}
