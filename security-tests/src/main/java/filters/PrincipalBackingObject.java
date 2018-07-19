package filters;

import javax.enterprise.context.RequestScoped;
import java.security.Principal;

@RequestScoped
public class PrincipalBackingObject {
    private Principal principal;

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }
}
