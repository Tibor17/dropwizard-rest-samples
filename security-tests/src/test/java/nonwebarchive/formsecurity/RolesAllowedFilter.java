package nonwebarchive.formsecurity;

import javax.annotation.Priority;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static filters.BasicAuthenticationServerFilter.abortUnauthorizedUser;
import static javax.ws.rs.Priorities.AUTHORIZATION;

@Priority(AUTHORIZATION) // authorization filter - should go after any authentication filters
@Provider
public class RolesAllowedFilter implements ContainerRequestFilter {
    private final RolesAllowed annotation;

    RolesAllowedFilter(RolesAllowed annotation) {
        this.annotation = annotation;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        SecurityContext securityContext = requestContext.getSecurityContext();

        if (annotation != null) {
            boolean roleMatched = false;
            for (String role : annotation.value()) {
                roleMatched |= securityContext.isUserInRole(role);
            }

            if (!roleMatched) {
                abortUnauthorizedUser(requestContext);
            }
        }
    }
}
