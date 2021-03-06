package rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static java.util.Locale.ENGLISH;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.status;

@Provider
public class ResourceExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        return status(BAD_REQUEST)
                .entity(e.getLocalizedMessage())
                .type(TEXT_PLAIN_TYPE)
                .encoding("UTF-8")
                .language(ENGLISH)
                .build();
    }
}
