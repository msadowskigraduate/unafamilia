package com.unfamilia.application;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class HomeController {

    @Inject
    Template base;

    @GET
    public TemplateInstance getHome() {
        return base.instance();
    }
}
