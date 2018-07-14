package rest;

import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static java.util.Locale.ENGLISH;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static javax.ws.rs.core.Response.serverError;

/**
 * This is implicit, and you do not have to do it.
 */
@Provider
public class ServerErrorMapper implements ExceptionMapper<Error> {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ServerRuntimeExceptionMapper.class);

    @Override
    public Response toResponse(Error e) {
        LOG.error(e.getLocalizedMessage(), e);

        return serverError()
                .entity(e.getLocalizedMessage())
                .type(TEXT_PLAIN_TYPE)
                .encoding("UTF-8")
                .language(ENGLISH)
                .build();
    }
}
