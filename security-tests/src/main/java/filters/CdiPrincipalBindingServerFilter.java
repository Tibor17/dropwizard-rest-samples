package filters;

import annotations.SecuredBasic;
import annotations.SecuredBearer;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import java.io.IOException;
import java.security.Principal;

import static javax.ws.rs.Priorities.AUTHENTICATION;

@SecuredBearer
@SecuredBasic
@Provider
@Priority(AUTHENTICATION + 1)
public class CdiPrincipalBindingServerFilter implements ContainerRequestFilter {
    @Inject
    private PrincipalBackingObject backingObject;

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        Principal principal = ctx.getSecurityContext().getUserPrincipal();
        backingObject.setPrincipal(principal);
    }
}
