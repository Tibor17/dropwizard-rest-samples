package rest;

import javax.persistence.NoResultException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.*;

@Provider
public class ResourceExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable e) {
        if (e instanceof WebApplicationException) {
            // in case of HTTP 404 or server errors
            return ((WebApplicationException) e).getResponse();
        } else if (e instanceof NoResultException) {
            return noContent()
                    .build();
        } else if (e instanceof RuntimeException || e instanceof Error) {
            return serverError()
                    .build();
        } else {
            return status(BAD_REQUEST)
                    .build();
        }
    }
}
