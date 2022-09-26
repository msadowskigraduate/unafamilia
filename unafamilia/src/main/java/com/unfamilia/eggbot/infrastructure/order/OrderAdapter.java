package com.unfamilia.eggbot.infrastructure.order;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@RegisterRestClient(configKey = "orders-v1")
public interface OrderAdapter {

    @POST
    Response createNewOrder(OrderDto orderDto);

    @GET
    @Path("/{order_id}")
    OrderDto queryOrder(@PathParam("order_id") String orderId);

    @POST
    @Path("/{order_id}/paid")
    Response markOrderAsPaid(@PathParam("order_id") String orderId);

    @POST
    @Path("/{order_id}/cancel")
    Response cancelOrder(@PathParam("order_id") String orderId);

    @POST
    @Path("/{order_id}/fulfill")
    Response markOrderAsFulfilled(@PathParam("order_id") String orderId);

    @POST
    @Path("/{orderId}/due/{amount}")
    Response addAmountDue(@PathParam("orderId") String orderId, @PathParam("amount") Long amount);
}
