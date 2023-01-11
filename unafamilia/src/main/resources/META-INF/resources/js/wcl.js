var difficulties = new Map()
var encounterMap = new Map()

$(document).ready(function () {
  $.ajax({
    url: "/api/audit/v1/expansion?id=5",
    contentType: "application/json",
    beforeSend: function() {
      $('#loader').removeClass("visually-hidden");
    },
    success: (data) => parseMetadata(data)
  });
});

//Metadata Parsing
function parseMetadata(json) {
  $('#metadata-container').append('<h2 class="pb-2 border-bottom" id="metadata-title">' + json.WorldData.Expansion.Name + '</h2>')
  for (const zone of json.WorldData.Expansion.Zones) {
    var title = $('#metadata-container').append('<h4 class="pb-2 border-bottom" id="metadata-title-'+ zone.id+'">' + zone.Name + '</h4>')
    var container = title.append('<div class="col d-flex align-items-start border" id="metadata-' + zone.id + '"></div>');

    for (const difficulty of zone.Difficulties) {
      difficulties.set(difficulty.Id, difficulty.Name)
      container.append('<h4 class="border-bottom">' + difficulty.Name + '</h4>')
    }
    for (const encounter of zone.Encounters) {
      container.append('<div class="col encounter-button"><h4>' + encounter.Name + '</h4></div>');
    }
  }
}

// //Report Parsing
// $(document).ready(function () {
//   $.ajax({
//     url: "/api/audit/v1/raid/rankings/dps?limit=20&zone_id=31",
//     contentType: "application/json",
//     beforeSend: function() {
//       $('#loader').removeClass("visually-hidden");
//     },
//     success: (data) => parseReport(data)
//   });
// });

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
  for (const report of json.reportData.reports.data) {
    var reportDate = new Date(report.startTime).toLocaleDateString()   
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
  encounters.forEach((value, key) => visualizeEncounter(value, key, actors));
  hideSpinner();
}

function visualizeEncounter(value, key, actors) {
  var encounter_key = ("encounter_" + key)
    .replaceAll(" ", "_")
    .replaceAll(",", "_");

  var chart_title = key.replaceAll("_", " ");

  $("#charts").append(
    '<div class="row" id="'+encounter_key+'"><canvas id="' + encounter_key +'_canvas"></canvas></div>'
  );
  const ctx = document.getElementById(encounter_key + "_canvas");
  var labels = new Set([...value.entries()].map((encounter) => encounter[1].flatMap((value) => value.date)).flat().sort((a,b) => {return a.date - b.date}).reverse())
  
  //Chart definition
  new Chart(ctx, {
    type: "line",
    data: {
      labels: [...labels],
      datasets: [...value.entries()].map((encounter) => {
        return {
          label: actors.get(encounter[0]),
          data: encounter[1].sort((a,b) => {return a.date - b.date}).reverse().map((value) => { return {x: value.date, y: value.rankPercent}}),
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
          text: chart_title,
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
      elements: {
        line: {
          borderWidth: 15
        }
      }
    },
  });
  // End chart definition
}

function hideSpinner() {
  $('#loader').addClass("visually-hidden");
}