package rest;

import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static java.util.Locale.ENGLISH;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static javax.ws.rs.core.Response.status;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {
    @Override
    public Response toResponse(ValidationException e) {
        // https://stackoverflow.com/questions/16133923/400-vs-422-response-to-post-of-data
        return status(422)
                .entity(e.getLocalizedMessage())
                .type(TEXT_PLAIN_TYPE)
                .encoding("UTF-8")
                .language(ENGLISH)
                .build();
    }
}
