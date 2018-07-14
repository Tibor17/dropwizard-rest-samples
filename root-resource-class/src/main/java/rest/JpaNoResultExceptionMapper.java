package rest;

import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static java.util.Locale.ENGLISH;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static javax.ws.rs.core.Response.noContent;

@Provider
public class JpaNoResultExceptionMapper
        implements ExceptionMapper<NoResultException> {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ServerRuntimeExceptionMapper.class);

    @Override
    public Response toResponse(NoResultException e) {
        LOG.error(e.getLocalizedMessage(), e);

        return noContent()
                .entity(e.getLocalizedMessage())
                .type(TEXT_PLAIN_TYPE)
                .encoding("UTF-8")
                .language(ENGLISH)
                .build();
    }
}
