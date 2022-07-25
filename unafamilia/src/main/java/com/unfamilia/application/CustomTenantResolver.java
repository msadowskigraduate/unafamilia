package com.unfamilia.application;

import io.quarkus.oidc.TenantResolver;
import io.vertx.ext.web.RoutingContext;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class CustomTenantResolver implements TenantResolver {

    @Override
    public String resolve(RoutingContext context) {
        var tenant = Optional.ofNullable(context.get("tenant_id"));
        if (tenant.isPresent()) {
            return tenant.get().toString();
        } else {
            String path = context.request().path();
            String[] parts = path.split("/");

            if (parts.length == 0 || "default".equals(parts[1])) {
                return null;
            }

            return parts[1];
        }
    }
}
