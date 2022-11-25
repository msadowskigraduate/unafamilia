package com.unfamilia.application.report;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.unfamilia.eggbot.infrastructure.reporter.WishlistReporterAdapter;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;

@Path("/report")
@Produces(MediaType.APPLICATION_JSON)
public class WishlistReportController {
    
    @Inject
    @RestClient
    private WishlistReporterAdapter adapter;

    @Inject Template report;

    @GET
    @Authenticated
    public TemplateInstance queryReport() {
        return report.data("reports", adapter.queryReportForRoster());
    }
}
