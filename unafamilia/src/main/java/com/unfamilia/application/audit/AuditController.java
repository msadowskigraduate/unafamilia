package com.unfamilia.application.audit;

import java.io.IOException;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfamilia.application.audit.query.GenerateNewAuditQuery;
import com.unfamilia.application.query.QueryBus;
import com.unfamilia.eggbot.infrastructure.wowguild.model.CharacterProfileResponse;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

@Path("/v1/audit")
public class AuditController {
    @Inject 
    @Location("partials/audit")
    Template audit;

    @Inject
    QueryBus queryBus;

    @GET
    public TemplateInstance queryAudit() throws StreamReadException, DatabindException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        var readValue = mapper.readValue(Paths.get("roster-audit.json").toFile(), CharacterProfileResponse[].class);
        return audit.data("data", readValue);
    }

    // @GET
    // @Consumes(MediaType.APPLICATION_JSON)
    // public Response queryAuditAsJson() {
    //     var result = queryBus.handle(GenerateNewAuditQuery.of());
    //     return Response.ok(result).build();
    // }
}
