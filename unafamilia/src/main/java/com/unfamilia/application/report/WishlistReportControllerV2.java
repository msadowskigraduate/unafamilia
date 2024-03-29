package com.unfamilia.application.report;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.unfamilia.application.query.QueryBus;
import com.unfamilia.application.report.query.NewWishlistReportQuery;
import com.unfamilia.eggbot.infrastructure.reporter.ReportRepository;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;

@Path("/v2/report")
@Produces(MediaType.TEXT_HTML)
public class WishlistReportControllerV2 {
    @Inject QueryBus bus;

    @Inject @Location("report") Template reportTemplate;

    @Inject 
    @Location("partials/reports")
    Template reportPartials;

    @Inject ReportRepository reportRepository;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    public TemplateInstance queryReportUI() {
        return reportTemplate.instance();
    }

    @GET
    @Path("/partial")
    @Authenticated
    public Response queryReportData() {
        String reportBody = reportRepository.readReport();
        return Response.ok(reportBody).build();
    }

    @GET
    @Path("/refresh")
    @Authenticated
    public TemplateInstance refreshReportData(@QueryParam("difficulty") List<String> difficulty, @QueryParam("role") List<String> role) {
        Set<String> roles = role.stream().collect(Collectors.toSet());       
        var template = reportPartials
            .data("reports", bus.handle(new NewWishlistReportQuery(difficulty, roles)))
            .data("timestamp", ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));
        reportRepository.writeReport(template.render());
        return template;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public Response queryReportJson(@QueryParam("difficulty") List<String> difficulty, @QueryParam("role") List<String> role) {
        Set<String> roles = role.stream().collect(Collectors.toSet()); 
        return Response.ok(bus.handle(new NewWishlistReportQuery(difficulty, roles))).build();
    }
}
