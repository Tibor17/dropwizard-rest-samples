package rest;

import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Logger
public class RequestLogger implements ContainerRequestFilter {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(RequestLogger.class);

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        LOG.info("Calling resource {} {} HTTP/1.1", ctx.getMethod(), ctx.getUriInfo().getPath());
    }
}
