$(document).ready(function () {
  $.ajax({
    url: "/v1/audit/performance",
    contentType: "application/json",
    beforeSend: function() {
      $('#loader').removeClass("visually-hidden");
    },
    success: (data) => parseReport(data)
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

  var dates = new Set()
  //TODO: date is missing
  for (const report of json.data.reportData.reports.data) {
    var reportDate = new Date(report.startTime).toLocaleDateString()

    if(dates.has(reportDate)) {
      continue;
    }
    dates.add(reportDate)

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
                date: reportDate
              },
            ]);
          } else {
            characterRanks.set(character.id, [
              {
                rankPercent: character.rankPercent,
                bracketPercent: character.bracketPercent,
                date: reportDate
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
              date: reportDate
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
  $("#title").html("<h3>DPS Rankings for the last 10 reports.</h3>");
  encounters.forEach((value, key) => visualizeEncounter(value, key, actors));
  hideSpinner();
}

function visualizeEncounter(value, key, actors) {
  var encounter_key = ("encounter_" + key)
    .replaceAll(" ", "_")
    .replaceAll(",", "_");

  $("#charts").append(
    '<div class="row" id="'+encounter_key+'"><canvas id="' +encounter_key +'_canvas"></canvas></div>'
  );
  const ctx = document.getElementById(encounter_key + "_canvas");
  var labels = new Set([...value.entries()].map((encounter) => encounter[1].flatMap((value) => value.date)).flat().sort())
  //Chart definition
  new Chart(ctx, {
    type: "line",
    data: {
      labels: [...labels],
      datasets: [...value.entries()].map((encounter) => {
        return {
          label: actors.get(encounter[0]),
          data: encounter[1].sort((a,b) => a.date.localeCompare(b.date)).map((value) => { return {x: value.date, y: value.rankPercent}}),
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

function hideSpinner() {
  $('#loader').addClass("visually-hidden");
}