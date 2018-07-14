package filters;

import annotations.SecuredBearer;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static javax.ws.rs.Priorities.AUTHENTICATION;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.WWW_AUTHENTICATE;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static javax.ws.rs.core.Response.status;

@SecuredBearer
@Provider
@Priority(AUTHENTICATION)
public class BearerAuthenticationServerFilter implements ContainerRequestFilter {
    private static final String REALM = "example";
    private static final String AUTHENTICATION_SCHEME = "Bearer";

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        String clientAuthorization = ctx.getHeaderString(AUTHORIZATION);

        int indexOfToken = 1 + AUTHENTICATION_SCHEME.length();

        boolean authorizationVerified =
                isBearer(clientAuthorization)
                        && isTokenVerified(clientAuthorization.substring(indexOfToken));

        if (!authorizationVerified) {
            // here can be CSV of messages
            String unauthorizedResponseMessage = AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"";

            Response unauthorized = status(UNAUTHORIZED)
                    .header(WWW_AUTHENTICATE, unauthorizedResponseMessage)
                    .build();

            ctx.abortWith(unauthorized);
        }
    }

    private boolean isTokenVerified(String token) {
        return token.contains("4fdwsd54ws8w67wwe87w987ww64w317gw");
    }

    private static boolean isBearer(String authorizationHeaderField) {
        return authorizationHeaderField != null
                && authorizationHeaderField.trim()
                .startsWith(AUTHENTICATION_SCHEME + " ");
    }
}
