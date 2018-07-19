package services;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.security.Principal;

@ApplicationScoped
public class MyService {
    @Inject
    private Principal principal;

    public void doSomething() {

    }
}
