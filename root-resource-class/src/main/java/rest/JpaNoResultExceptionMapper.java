package rest;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.persistence.NoResultException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static java.util.Locale.ENGLISH;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.noContent;
import static javax.ws.rs.core.Response.status;

@Provider
public class JpaNoResultExceptionMapper
        implements ExceptionMapper<NoResultException> {

    private static final String HTTP_METHOD_GET = "GET";
    private static final Logger LOG = LoggerFactory.getLogger(ServerRuntimeExceptionMapper.class);

    @Context
    private Request request;

    @Override
    public Response toResponse(NoResultException e) {
        LOG.error(e.getLocalizedMessage(), e);

        if (HTTP_METHOD_GET.equals(request.getMethod())) {
            // The 204 response MUST NOT include a message-body, and thus is always
            // terminated by the first empty line after the header fields.
            // https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.5
            // https://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-19#section-7.4.4
            return noContent().build();
        } else {
            return status(NOT_FOUND)
                    .entity(e.getLocalizedMessage())
                    .type(TEXT_PLAIN_TYPE)
                    .encoding("UTF-8")
                    .language(ENGLISH)
                    .build();
        }
    }
}
