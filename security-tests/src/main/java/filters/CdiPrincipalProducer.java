package filters;

import annotations.RestPrincipal;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.security.Principal;

@ApplicationScoped
public class CdiPrincipalProducer {
    @Inject
    private PrincipalBackingObject backingObject;

    @Produces
    @RestPrincipal
    public Principal producePrincipal() {
        return backingObject.getPrincipal();
    }
}
