package filters;

import java.security.Principal;

import static java.util.Objects.requireNonNull;

public final class UserPrincipal implements Principal {
    private final int id;
    private final String loginId;
    private final String role;

    UserPrincipal(int id, String loginId, String role) {
        this.id = id;
        this.loginId = requireNonNull(loginId);
        this.role = requireNonNull(role);
    }

    String getRole() {
        return role;
    }

    @Override
    public String getName() {
        return loginId;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this
                || obj instanceof Principal && getName().equals(((Principal) obj).getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
