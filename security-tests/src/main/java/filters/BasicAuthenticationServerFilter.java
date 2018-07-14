package filters;

import annotations.SecuredBasic;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static filters.AuthenticationHeaderField.basicAuthentication;
import static filters.AuthenticationHeaderField.nonBasicAuthentication;
import static java.lang.Character.isWhitespace;
import static javax.ws.rs.Priorities.AUTHENTICATION;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.WWW_AUTHENTICATE;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static javax.ws.rs.core.Response.status;

@SecuredBasic
@Provider
@Priority(AUTHENTICATION)
public class BasicAuthenticationServerFilter implements ContainerRequestFilter {
    private static final String REALM = "example";
    private static final String AUTHENTICATION_SCHEME = "Basic";

    private final Map<String, UserPrincipal> artificialStorage = new ConcurrentHashMap<>();

    public BasicAuthenticationServerFilter() {
        // signature :: john.smith@lycos.com:js**
        artificialStorage.put("am9obi5zbWl0aEBseWNvcy5jb206anMqKg==", new UserPrincipal(1, "john.smith@lycos.com", "admin"));

        // signature :: susan.johnson@yahoo.com:sj123
        artificialStorage.put("c3VzYW4uam9obnNvbkB5YWhvby5jb206c2oxMjM=", new UserPrincipal(2, "susan.johnson@yahoo.com", "user"));
    }

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        String clientAuthorization = ctx.getHeaderString(AUTHORIZATION);

        AuthenticationHeaderField field = parseAuthResponse(clientAuthorization);
        boolean isSecure = ctx.getSecurityContext().isSecure();
        String realm = field.getParameters().get("realm");

        if (field.isBasicAuthentication()) {
            UserPrincipal principal = artificialStorage.get(field.getSignature());

            boolean authorizationVerified = principal != null && (realm == null || realm.equals(REALM));

            if (authorizationVerified) {
                ctx.setSecurityContext(new SecurityContext() {
                    @Override
                    public Principal getUserPrincipal() {
                        return principal;
                    }

                    @Override
                    public boolean isUserInRole(String role) {
                        return role == null || role.equals(principal.getRole());
                    }

                    @Override
                    public boolean isSecure() {
                        return isSecure;
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        return AUTHENTICATION_SCHEME;
                    }
                });
                return;
            }
        }

        abortUnauthorizedUser(ctx);
    }

    public static void abortUnauthorizedUser(ContainerRequestContext ctx) {
        // here can be CSV of messages, see RFC-2616 - http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.3
        String unauthorizedResponseMessage = AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"";

        Response unauthorized = status(UNAUTHORIZED)
                .header(WWW_AUTHENTICATE, unauthorizedResponseMessage)
                .build();

        ctx.abortWith(unauthorized);
    }

    private static AuthenticationHeaderField parseAuthResponse(String authentication) {
        int i = 0;
        if (authentication.length() > 5
                && authentication.charAt(i++) == 'B'
                && authentication.charAt(i++) == 'a'
                && authentication.charAt(i++) == 's'
                && authentication.charAt(i++) == 'i'
                && authentication.charAt(i++) == 'c') {
            StringBuilder signature = new StringBuilder();
            i = extractSignature(authentication, signature, i);
            AuthenticationHeaderField parsed = basicAuthentication(signature.toString());
            extractAuthParams(authentication, i, parsed);
            return parsed;
        }
        return nonBasicAuthentication();
    }

    private static int extractSignature(String authentication, StringBuilder signature, int i) {
        boolean isSignatureStarted = false;
        while (i < authentication.length()) {
            char c = authentication.charAt(i++);
            if (isSignatureStarted) {
                if (isWhitespace(c)) {
                    break;
                }
            } else {
                if (!isWhitespace(c)) {
                    isSignatureStarted = true;
                }
            }

            if (isSignatureStarted) {
                signature.append(c);
            }
        }
        return i;
    }

    private static void extractAuthParams(String authentication, int i, AuthenticationHeaderField parsed) {
        String key = "", value = "";
        boolean isKey = true, isValueStarted = false, isValueFinished = false;
        while (i < authentication.length()) {
            char c = authentication.charAt(i++);
            if (isKey) {
                if (c == '=') {
                    isKey = false;
                } else if (!isWhitespace(c)) {
                    key += c;
                }
                continue;
            }

            if (c == '"') {
                if (isValueStarted) {
                    isValueFinished = true;
                } else {
                    isValueStarted = true;
                    continue;
                }
            }

            if (isValueStarted) {
                if (isValueFinished) {
                    parsed.getParameters().put(key.trim(), value);
                    isValueStarted = isValueFinished = false;
                    isKey = true;
                    key = value = "";
                } else {
                    value += c;
                }
            }
        }
    }
}
