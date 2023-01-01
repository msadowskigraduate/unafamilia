$(document).ready(function () {
  $.ajax({
    url: "/v1/audit/performance",
    contentType: "application/json",
    success: (data) => parseReport(data),
  });
});

function parseReport(json) {
  encounters = new Map();
  indexes = new Map();
  actors = new Map();
  difficulties = new Map([
    [5, "Mythic"],
    [4, "Heroic"],
    [3, "Normal"],
    [1, "LFR"],
  ]);

  //TODO: date is missing
  for (const report of json.data.reportData.reports.data) {
    for (const encounter of report.rankings.data) {
      indexes.set(encounter.encounter.id, encounter.encounter.name);
      encounter_id =
        encounter.encounter.name + "_" + difficulties.get(encounter.difficulty);
      for (const character of encounter.roles.dps.characters) {
        actors.set(character.id, character.name);

        if (encounters.has(encounter_id)) {
          characterRanks = encounters.get(encounter_id);
          if (characterRanks.has(character.id)) {
            characterRanks.set(character.id, [
              ...characterRanks.get(character.id),
              {
                rankPercent: character.rankPercent,
                bracketPercent: character.bracketPercent,
              },
            ]);
          } else {
            characterRanks.set(character.id, [
              {
                rankPercent: character.rankPercent,
                bracketPercent: character.bracketPercent,
              },
            ]);
          }

          encounters.set(encounter_id, characterRanks);
        } else {
          newCharacterRank = new Map();
          newCharacterRank.set(character.id, [
            {
              rankPercent: character.rankPercent,
              bracketPercent: character.bracketPercent,
            },
          ]);
          encounters.set(encounter_id, newCharacterRank);
        }
      }
    }
  }

  visualizeData(encounters, actors);
}

function visualizeData(encounters, actors) {
  $("#title").html("DPS Rankings for the last 10 reports.");
  encounters.forEach((value, key) => visualizeEncounter(value, key, actors));
}

function visualizeEncounter(value, key, actors) {
  var encounter_key = ("encounter_" + key)
    .replaceAll(" ", "_")
    .replaceAll(",", "_");

  $("#charts").append(
    '<div class="row" id="'+encounter_key+'"><canvas id="' +encounter_key +'_canvas"></canvas></div>'
  );
  const ctx = document.getElementById(encounter_key + "_canvas");

  //Chart definition
  new Chart(ctx, {
    type: "line",
    data: {
      labels: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
      datasets: [...value.entries()].map((encounter) => {
        return {
          label: actors.get(encounter[0]),
          data: encounter[1].map((value) => value.rankPercent),
          borderWidth: 1,
          hidden: true,
        };
      }),
      active: false,
    },
    options: {
      plugins: {
        title: {
          display: true,
          text: key.replaceAll("_", " "),
          padding: {
            top: 10,
            bottom: 30,
          },
        },
      },
      scales: {
        y: {
          beginAtZero: true,
          max: 100,
        },
      },
    },
  });
  // End chart definition
}
