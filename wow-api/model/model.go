package model

type Realm struct {
	Name string `json:"name"`
	Slug string `json:"slug"`
	ID   int    `json:"id"`
}

type Member struct {
	Name           string         `json:"name"`
	ID             int64          `json:"id"`
	RealmSlug      string         `json:"realm_slug"`
	Level          int            `json:"level"`
	CharacterClass CharacterClass `json:"playable_class"`
	Race           Race           `json:"race"`
	Rank           int            `json:"rank"`
}

type GuildRoster struct {
	Link    string   `json:"_link"`
	Name    string   `json:"name"`
	ID      int      `json:"id"`
	Realm   Realm    `json:"realm"`
	Members []Member `json:"members"`
}

type CharacterProfile struct {
	ID                int            `json:"id"`
	Name              string         `json:"name"`
	Gender            string         `json:"gender"`
	Faction           string         `json:"faction"`
	Race              Race           `json:"race"`
	CharacterClass    CharacterClass `json:"character_class"`
	ActiveSpec        ActiveSpec     `json:"active_spec"`
	Realm             Realm          `json:"realm"`
	Level             int            `json:"level"`
	AchievementPoint  int            `json:"achievement_points"`
	AverageItemLevel  int            `json:"average_item_level"`
	EquippedItemLevel int            `json:"equipped_item_level"`
	Equipment         Equipement     `json:"equipment"`
}

type Equipement struct {
	EquippedItems    []EquipmentSlot   `json:"equipped_items"`
	EquippedItemSets []EquippedItemSet `json:"equipped_item_sets,omitempty"`
}

type EquipmentSlot struct {
	ItemID       int          `json:"id"`
	Name         string       `json:"name"`
	Socket       Socket       `json:"socket,omitempty"`
	SlotType     string       `json:"slot_type"`
	Quality      string       `json:"quality"`
	BonusList    []int        `json:"bonus_list"`
	ItemSubclass ItemSubClass `json:"item_subclass"`
	ItemClass    ItemClass    `json:"item_class"`
	ItemLevel    int          `json:"item_level"`
	Enchantment  Enchantment  `json:"enchantments,omitempty"`
}

type EquippedItemSet struct {
	ItemID int         `json:"id"`
	Name   string      `json:"name"`
	Items  ItemSetItem `json:"items"`
}

type ItemSetItem struct {
	ItemID int    `json:"id"`
	Name   string `json:"name"`
}

type Socket struct {
	ItemID        int    `json:"socket_id"`
	Name          string `json:"name"`
	MediaID       int    `json:"media"`
	DisplayString string `json:"display_string"`
}

type ItemClass struct {
	ID   int    `json:"id"`
	Name string `json:"name"`
}

type Enchantment struct {
	ID             int    `json:"id"`
	DisplayString  string `json:"display_string"`
	SourceItemID   int    `json:"source_item_id"`
	SourceItemName string `json:"source_item_name"`
	EnchantmentID  int    `json:"enchantment_id"`
}

type ItemSubClass struct {
	ID   int    `json:"id"`
	Name string `json:"name"`
}

type CharacterClass struct {
	CharacterClass   string `json:"class_name"`
	CharacterClassID int    `json:"class_id"`
}

type Race struct {
	RaceID int    `json:"id"`
	Race   string `json:"name"`
}

type ActiveSpec struct {
	ActiveSpec   string `json:"name"`
	ActiveSpecID int    `json:"id"`
}

type GuildSummary struct {
	Name  string `json:"name"`
	ID    int    `json:"id"`
	Realm Realm  `json:"realm"`
}
