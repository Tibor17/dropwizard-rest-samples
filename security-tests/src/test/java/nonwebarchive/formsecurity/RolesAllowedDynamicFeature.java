package nonwebarchive.formsecurity;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class RolesAllowedDynamicFeature implements DynamicFeature {
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext configuration) {
        RolesAllowed ra = resourceInfo.getResourceMethod().getAnnotation(RolesAllowed.class);
        if (ra != null) {
            configuration.register(new RolesAllowedFilter(ra));
        }
    }
}
