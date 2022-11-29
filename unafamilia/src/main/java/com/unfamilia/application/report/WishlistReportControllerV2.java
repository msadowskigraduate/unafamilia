package com.unfamilia.application.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.unfamilia.eggbot.infrastructure.reporter.Roster;
import com.unfamilia.eggbot.infrastructure.reporter.WishlistReporterAdapter;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;

@Path("/v2/report")
@Produces(MediaType.APPLICATION_JSON)
public class WishlistReportControllerV2 {
    @Inject
    @RestClient
    WishlistReporterAdapter adapter;

    @Inject @Location("report") Template reportTemplate;

    @Inject 
    @Location("partials/reports")
    Template reportPartials;

    @GET
    public TemplateInstance queryReportUI() {
        return reportTemplate.instance();
    }

    @GET
    @Path("/partial")
    @Authenticated
    public TemplateInstance queryReportData(@QueryParam("mythic") String mythic, @QueryParam("heroic") String heroic, @QueryParam("normal") String normal) {
        List<String> toBeExcluded = new ArrayList<>();
        
        if(Objects.nonNull(mythic)) {
            toBeExcluded.add("mythic");
        }
        
        if(Objects.nonNull(heroic)) {
            toBeExcluded.add("heroic");
        }
        
        if(Objects.nonNull(normal)) {
            toBeExcluded.add("normal");
        }
        System.out.println(toBeExcluded);
        List<Roster> roster = adapter.queryRoster();
        var reports = roster.stream().map(character -> adapter.queryReportForRosterV2(String.valueOf(character.id()), toBeExcluded))
            .collect(Collectors.toList());
        return reportPartials.data("reports", reports);
    }
}
