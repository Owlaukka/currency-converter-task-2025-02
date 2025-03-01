package me.owlaukka.filter;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import me.owlaukka.logging.MDCUtils;

import java.util.UUID;

@Provider
@Priority(1)
public class RequestIdFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        var requestId = UUID.randomUUID().toString();
        MDCUtils.setRequestId(requestId);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        MDCUtils.clearRequestId();
    }
}