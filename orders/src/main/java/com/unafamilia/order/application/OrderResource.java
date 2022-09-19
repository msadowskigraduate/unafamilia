package com.unafamilia.order.application;

import com.unafamilia.order.Order;
import com.unafamilia.order.infrastructure.OrderMapper;
import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;

@Path("/v1/order")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class OrderResource {

    private final OrderMapper orderMapper;

    @POST
    @Transactional
    public Response createNewOrder(OrderDto orderDto) {
        var orderId = UUID.randomUUID();
        var order = orderMapper.from(orderId, orderDto);

        order.getOrderItems().forEach(orderItem -> orderItem.persist());
        order.persist();


        var location = URI.create("/v1/order/" + orderId);
        return Response.created(location).build();
    }

    @GET
    @Path("/{orderId}")
    @Transactional
    public Response queryOrder(@PathParam("orderId") UUID orderId) {
        var order = Order.<Order>findByIdOptional(orderId);

        if(order.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(orderMapper.from(order.get())).build();
    }

    @POST
    @Path("/{orderId}/paid")
    @Transactional
    public Response markOrderAsPaid(@PathParam("orderId") UUID orderId) {
        var order = Order.<Order>findByIdOptional(orderId);

        if(order.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        order.get().paid();
        order.get().persist();

        return Response.ok().build();
    }

    @POST
    @Path("/{orderId}/cancel")
    @Transactional
    public Response cancelOrder(@PathParam("orderId") UUID orderId) {
        var order = Order.<Order>findByIdOptional(orderId);

        if(order.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        order.get().cancel();
        order.get().persist();

        return Response.ok().build();
    }

    @POST
    @Path("/{orderId}/fulfill")
    @Transactional
    public Response markOrderAsFulfilled(@PathParam("orderId") UUID orderId) {
        var order = Order.<Order>findByIdOptional(orderId);

        if(order.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        order.get().fulfill();
        order.get().persist();

        return Response.ok().build();
    }

    @POST
    @Path("/{orderId}/due/{amount}")
    @Transactional
    public Response addAmountDue(@PathParam("orderId") UUID orderId, Long amount) {
        var order = Order.<Order>findByIdOptional(orderId);

        if (order.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        order.get().setAmountDue(amount);
        order.get().persist();

        return Response.ok().build();
    }
}