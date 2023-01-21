var difficulties = new Map()
var encounterMap = new Map()

$(document).on('click', '.choice', function() {
  $('.choice').removeClass('choice-selected');
  $( this ).addClass('choice-selected');

  var isDisabled = $( this ).prop("disabled");
  if(isDisabled === 'true') {return;}
  
  var zoneId = $( this ).attr("zoneId");
  $('.choice').prop("disabled", "false");
  $(this).prop("disabled", "true");
  queryRankingsData(zoneId);
});

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
  $('#title').append('<h2 class="pb-2 border-bottom" id="metadata-title">' + json.WorldData.Expansion.Name + '</h2>')
  for (const zone of json.WorldData.Expansion.Zones) {
    $('#metadata-container').append('<div class="dark-button col shadow rounded zone-option border border-2 mx-5 text-center text-white align-middle choice" id=metadata-form-container-'+ zone.Id+'" type="button" zoneId='+ zone.Id + '><p class="my-2">' + zone.Name + '</p></div>');
    difficulties.set(zone.Id, new Map());
    for (const difficulty of zone.Difficulties) {
      difficulties.get(zone.Id).set(difficulty.Id, difficulty.Name);
    }
  }
  hideSpinner();
}

//Report Parsing
function queryRankingsData(zoneid) {
  $.ajax({
    url: "/api/audit/v1/raid/rankings/dps?limit=20&zone_id="+zoneid,
    contentType: "application/json",
    beforeSend: function() {
      $('#loader').removeClass("visually-hidden");
    },
    success: (data) => parseReport(data, zoneid)
  });
}

function parseReport(json, zoneid) {
  encounters = new Map();
  actors = new Map();

  for (const report of json.reportData.reports.data) {
    var reportDate = new Date(report.startTime).toLocaleDateString()   
    for (const encounter of report.rankings.data) {
      encounter_id = encounter.encounter.name + "_" + encounter.difficulty;
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

  visualizeData(encounters, actors, zoneid);
}

function visualizeData(encounters, actors, zoneid) {
  $("#charts").empty();
  $('#scroll-spy-menu').removeClass('visually-hidden');
  difficulties.get(parseInt(zoneid)).forEach((v,k) => {
    $("#charts").append('<div class="row my-3 text-white" id="difficulty-'+k+'"><h5>' + v + '</h5></div>');
    $('#difficulty-example').append('<a class="p-1 shadow rounded dark-button btn btn-secondary border border-2" href="#difficulty-'+k+'">' + v + '</a>');
  });
  encounters.forEach((value, key) => visualizeEncounter(value, key, actors, zoneid));

  const dataSpyList = document.querySelectorAll('[data-bs-spy="scroll"]')
  dataSpyList.forEach(dataSpyEl => {
    bootstrap.ScrollSpy.getInstance(dataSpyEl).refresh()
  })
  hideSpinner();
}

function visualizeEncounter(value, key, actors, zoneid) {
  var encounter_key = ("encounter_" + key)
    .replaceAll(" ", "_")
    .replaceAll(",", "_");

  var difficulty = key.split("_").at(-1);
  var chart_title = key.split("_")[0]+ " " + difficulties.get(parseInt(zoneid)).get(parseInt(key.split("_").at(-1)));
console.log(difficulty)
  $("#difficulty-" + difficulty).append(
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
          color: 'white',
          padding: {
            top: 10,
            bottom: 30,
          },
        },
      legend: {
        labels: {
          color: "white",
        }
      }
      },
      scales: {
        y: {
          beginAtZero: true,
          max: 100,
          ticks: {
            color: 'white',
          },
        },
        x: {
          ticks: {
            color: 'white',
          },
        },
      },
      elements: {
        line: {
          borderWidth: 5,
          spanGaps: true,
        },
      }
    },
  });
  // End chart definition
}

function hideSpinner() {
  $('#loader').addClass("visually-hidden");
}