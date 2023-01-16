$(document).ready(function (event) {
    console.log("Ready")
});

//Sidebars
$("#href-report").click(function (event) {
    event.preventDefault();
    QueryReport();
});

$("#href-audit").click(event => {
    event.preventDefault();
    QueryAudit(event);
});

$("#href-performance").click(function (event) {
    event.preventDefault();
    QueryPerformance();
});

//Home
$("#home-report").click(function (event) {
    event.preventDefault();
    QueryReport();
});

$("#home-audit").click(function (event) {
    event.preventDefault();
    QueryAudit();
});

$("#home-rankings").click(function (event) {
    event.preventDefault();
    QueryPerformance();
});

function QueryReport() {
    $.ajax({
        url: "/v2/report",
        headers: { "Content-Type": "text/html" },
        success: function (html) {
            $("#data-container").html(html);
        }
    })
}

function QueryAudit(event) {
    $.ajax({
        url: "/v1/audit",
        headers: { "Content-Type": "text/html" },
        beforeSend: function () {
            $('#data-container').html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Loading...');
        },
        success: function (html) {
            $("#data-container").html(html);
        }
    })
}

function QueryPerformance() {
    $.ajax({
        url: "/v1/audit/performance",
        headers: { "Content-Type": "text/html" },
        beforeSend: function () {
            $('#data-container').html('<div class="container"><span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Loading...</div>');
        },
        success: function (html) {
            $("#data-container").html(html);
        }
    })
}

$("#discord-auth").click(function (event) {
    event.preventDefault();
    $.ajax({
        url: "/discord/login",
        success: function (html) {
            $("#data-container").html(html);
        }
    })
});

