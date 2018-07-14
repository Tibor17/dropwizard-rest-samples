package filters;

import java.util.HashMap;
import java.util.Map;

final class AuthenticationHeaderField {
    private final Map<String, String> parameters = new HashMap<>();
    private final boolean basicAuthentication;
    private final String signature;

    private AuthenticationHeaderField(String signature) {
        basicAuthentication = true;
        this.signature = signature;
    }

    private AuthenticationHeaderField() {
        basicAuthentication = false;
        signature = null;
    }

    static AuthenticationHeaderField basicAuthentication(String signature) {
        return new AuthenticationHeaderField(signature);
    }

    static AuthenticationHeaderField nonBasicAuthentication() {
        return new AuthenticationHeaderField();
    }

    Map<String, String> getParameters() {
        return parameters;
    }

    boolean isBasicAuthentication() {
        return basicAuthentication;
    }

    String getSignature() {
        return signature;
    }
}
