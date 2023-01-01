package com.unfamilia.application.audit;

import java.io.IOException;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.unfamilia.application.audit.query.GenerateNewAuditQuery;
import com.unfamilia.application.audit.query.QueryGuildReportRanking;
import com.unfamilia.application.query.QueryBus;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

@Path("/v1/audit")
public class AuditController {
    @Inject 
    @Location("partials/audit")
    Template audit;

    @Inject
    @Location("partials/performance")
    Template performance;

    @Inject
    QueryBus queryBus;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    public TemplateInstance queryAudit() throws StreamReadException, DatabindException, IOException {
        return audit.data("data", queryBus.handle(GenerateNewAuditQuery.of()));
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public Response queryAuditAsJson() {
        var result = queryBus.handle(GenerateNewAuditQuery.of());
        return Response.ok(result).build();
    }

    @GET
    @Path("/performance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryWarcraftlogsData() throws IOException {
        var wclResponse = queryBus.handle(new QueryGuildReportRanking());
        return Response.ok(wclResponse.getEntity()).build();
    }

    @GET
    @Path("/performance")
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance queryWarcraftlogsTemplate() throws IOException {
        return performance.instance();
    }
}
