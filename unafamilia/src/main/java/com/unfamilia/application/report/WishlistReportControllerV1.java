package com.unfamilia.application.report;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfamilia.eggbot.infrastructure.reporter.WishlistReport;
import com.unfamilia.eggbot.infrastructure.reporter.WishlistReporterAdapter;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

@Path("/v1/report")
@Produces(MediaType.APPLICATION_JSON)
public class WishlistReportControllerV1 {
    @Inject 
    @Location("partials/reports")
    Template reportPartials;

    @Inject
    Template report;

    // @GET
    // @Authenticated
    // public TemplateInstance queryReport() {
    //     return report.data("reports", adapter.queryReportForRoster());
    // }

    @GET
    public TemplateInstance queryReportUI() {
        return report.instance();
    }

    @GET
    @Path("/partial")
    public TemplateInstance queryReportDataReactive(@QueryParam("mythic") String mythic, @QueryParam("heroic") String heroic, @QueryParam("mythic") String normal) throws StreamReadException, DatabindException, IOException {
        System.out.println("Received input from form: " + mythic);
        System.out.println("Received input from form: " + heroic);
        System.out.println("Received input from form: " + normal);
            ObjectMapper mapper = new ObjectMapper();
            List<WishlistReport> readValue = Arrays.asList(mapper.readValue(Paths.get("result.json").toFile(), WishlistReport[].class));
            return reportPartials.data("reports", readValue);
    }
}
