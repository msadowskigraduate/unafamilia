package com.unfamilia.application.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import com.unfamilia.eggbot.infrastructure.reporter.WishlistReport;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;

@Path("/v2/report")
@Produces(MediaType.APPLICATION_JSON)
public class WishlistReportControllerV2 {
    @Inject QueryBus bus;

    @Inject @Location("report") Template reportTemplate;

    @Inject 
    @Location("partials/reports")
    Template reportPartials;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    public TemplateInstance queryReportUI() {
        return reportTemplate.instance();
    }

    @GET
    @Path("/partial")
    @Authenticated
    public TemplateInstance queryReportData(@QueryParam("mythic") String mythic, @QueryParam("heroic") String heroic, @QueryParam("normal") String normal) {
        List<String> toBeIncluded = new ArrayList<>();
        
        if(Objects.nonNull(mythic)) {
            toBeIncluded.add("mythic");
        }
        
        if(Objects.nonNull(heroic)) {
            toBeIncluded.add("heroic");
        }
        
        if(Objects.nonNull(normal)) {
            toBeIncluded.add("normal");
        }
        
        return reportPartials.data("reports", generateWishlistReport(toBeIncluded));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public Response queryReportJson(@QueryParam("mythic") String mythic, @QueryParam("heroic") String heroic, @QueryParam("normal") String normal) {
        List<String> toBeIncluded = new ArrayList<>();
        
        if(Objects.nonNull(mythic)) {
            toBeIncluded.add("mythic");
        }
        
        if(Objects.nonNull(heroic)) {
            toBeIncluded.add("heroic");
        }
        
        if(Objects.nonNull(normal)) {
            toBeIncluded.add("normal");
        }
        
        return Response.ok(generateWishlistReport(toBeIncluded)).build();
    }
    
    private List<WishlistReport> generateWishlistReport(List<String> toBeIncluded) {
        return bus.handle(new NewWishlistReportQuery(toBeIncluded));
    }
}
