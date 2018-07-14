package nonwebarchive.formsecurity;

import filters.BasicAuthenticationServerFilter;
import filters.BearerAuthenticationServerFilter;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.glassfish.jersey.test.inmemory.InMemoryTestContainerFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import resource.RootResource;

import javax.json.JsonObject;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import static javax.ws.rs.client.Entity.form;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.WWW_AUTHENTICATE;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static javax.ws.rs.core.UriBuilder.fromPath;
import static org.assertj.core.api.Assertions.assertThat;

public class NonWebArchiveHttpFormAuthenticationTest {

    private final RootResource resource = new RootResource();

    @Rule
    public final ResourceTestRule server = ResourceTestRule.builder()
            .bootstrapLogging(true)
            .setTestContainerFactory(new InMemoryTestContainerFactory())
            .addProvider(RolesAllowedDynamicFeature.class)
            .addProvider(BearerAuthenticationServerFilter.class)
            .addProvider(BasicAuthenticationServerFilter.class)
            .addProvider(ObjectMapperProvider.class)
            .addResource(() -> resource)
            .build();

    private int serverPort;

    @Before
    public void lookupFreeServerPort() {
        serverPort = server.target("/secure/car/components")
                .getUri()
                .getPort();
    }

    @Test
    public void shouldLoginWithForm() {
        Form login = new Form()
                .param("j_username", "nissan@car")
                .param("j_password", "tr454");

        Response response = server.client()
                .register(ObjectMapperProvider.class)
                .target(fromPath("/secure/car/components")
                        .scheme("http")
                        .host("localhost")
                        .port(serverPort))
                .path("motor")
                .request()
                .post(form(login));

        JsonObject obj = response.readEntity(JsonObject.class);

        response.close();

        assertThat(obj.getString("reportName"))
                .isEqualTo("car-motor");

        assertThat(obj.getString("tradeoff"))
                .isEqualTo("Nissan");

        assertThat(obj.getInt("numberOfParts"))
                .isEqualTo(1);

        assertThat(response.getStatus())
                .isEqualTo(OK.getStatusCode());
    }

    @Test
    public void shouldLoginWithBearer() {
        Response response = server.client()
                .register(ObjectMapperProvider.class)
                .target(fromPath("/")
                        .scheme("http")
                        .host("localhost")
                        .port(serverPort))
                .path("/secure/car/components/motor")
                .request()
                .header(AUTHORIZATION, "Bearer 4fdwsd54ws8w67wwe87w987ww64w317gw realm=\"example\"")
                .get();

        JsonObject obj = response.readEntity(JsonObject.class);

        response.close();

        assertThat(response.getStatus())
                .isEqualTo(OK.getStatusCode());

        assertThat(obj.getString("reportName"))
                .isEqualTo("car-motor");

        assertThat(obj.getString("tradeoff"))
                .isEqualTo("Nissan");

        assertThat(obj.getInt("numberOfParts"))
                .isEqualTo(1);
    }

    @Test
    public void shouldNotLoginWithWrongAuthSchema() {
        Response response = server.client()
                .register(ObjectMapperProvider.class)
                .target(fromPath("/")
                        .scheme("http")
                        .host("localhost")
                        .port(serverPort))
                .path("/secure/car/components/motor")
                .request()
                .header(AUTHORIZATION, "Bear 4fdwsd54ws8w67wwe87w987ww64w317gw realm=\"example\"")
                .get();

        response.close();

        assertThat(response.getStatus())
                .isEqualTo(UNAUTHORIZED.getStatusCode());

        assertThat(response.getHeaderString(WWW_AUTHENTICATE))
                .isEqualTo("Bearer realm=\"example\"");
    }

    @Test
    public void shouldNotLoginWithWrongDigest() {
        Response response = server.client()
                .register(ObjectMapperProvider.class)
                .target(fromPath("/")
                        .scheme("http")
                        .host("localhost")
                        .port(serverPort))
                .path("/secure/car/components/motor")
                .request()
                .header(AUTHORIZATION, "Bearer 123 realm=\"example\"")
                .get();

        response.close();

        assertThat(response.getStatus())
                .isEqualTo(UNAUTHORIZED.getStatusCode());

        assertThat(response.getHeaderString(WWW_AUTHENTICATE))
                .isEqualTo("Bearer realm=\"example\"");
    }

    @Test
    public void shouldNotLoginWithBasicAuth() {
        Response response = server.client()
                .register(ObjectMapperProvider.class)
                .target(fromPath("/")
                        .scheme("http")
                        .host("localhost")
                        .port(serverPort))
                .path("/secure/car/components/nisan")
                .request()
                .header(AUTHORIZATION, "Basic am9obi5zbWl0aEBseWNvcy5jb206anMqKg== realm=\"example\"")
                .get();

        JsonObject obj = response.readEntity(JsonObject.class);

        response.close();

        assertThat(response.getStatus())
                .isEqualTo(OK.getStatusCode());

        assertThat(obj.getString("reportName"))
                .isEqualTo("car-motor");

        assertThat(obj.getString("tradeoff"))
                .isEqualTo("Nissan");

        assertThat(obj.getInt("numberOfParts"))
                .isEqualTo(1);
    }

    @Test
    public void shouldNotLoginWithBasicWrongAuthSchema() {
        Response response = server.client()
                .register(ObjectMapperProvider.class)
                .target(fromPath("/")
                        .scheme("http")
                        .host("localhost")
                        .port(serverPort))
                .path("/secure/car/components/nisan")
                .request()
                .header(AUTHORIZATION, "Basc am9obi5zbWl0aEBseWNvcy5jb206anMqKg== realm=\"example\"")
                .get();

        response.close();

        assertThat(response.getStatus())
                .isEqualTo(UNAUTHORIZED.getStatusCode());

        assertThat(response.getHeaderString(WWW_AUTHENTICATE))
                .isEqualTo("Basic realm=\"example\"");
    }

    @Test
    public void shouldNotLoginWithBasicWrongDigest() {
        Response response = server.client()
                .register(ObjectMapperProvider.class)
                .target(fromPath("/")
                        .scheme("http")
                        .host("localhost")
                        .port(serverPort))
                .path("/secure/car/components/nisan")
                .request()
                .header(AUTHORIZATION, "Basic 123 realm=\"example\"")
                .get();

        response.close();

        assertThat(response.getStatus())
                .isEqualTo(UNAUTHORIZED.getStatusCode());

        assertThat(response.getHeaderString(WWW_AUTHENTICATE))
                .isEqualTo("Basic realm=\"example\"");
    }

    @Test
    public void shouldNotLoginWithNonAdminRole() {
        Response response = server.client()
                .register(ObjectMapperProvider.class)
                .target(fromPath("/")
                        .scheme("http")
                        .host("localhost")
                        .port(serverPort))
                .path("/secure/car/components/nisan")
                .request()
                .header(AUTHORIZATION, "Basic c3VzYW4uam9obnNvbkB5YWhvby5jb206c2oxMjM= realm=\"example\"")
                .get();

        response.close();

        assertThat(response.getStatus())
                .isEqualTo(UNAUTHORIZED.getStatusCode());

        assertThat(response.getHeaderString(WWW_AUTHENTICATE))
                .isEqualTo("Basic realm=\"example\"");
    }

    @Test
    public void shouldNotLoginWithAdminRoleButWrongRealm() {
        Response response = server.client()
                .register(ObjectMapperProvider.class)
                .target(fromPath("/")
                        .scheme("http")
                        .host("localhost")
                        .port(serverPort))
                .path("/secure/car/components/nisan")
                .request()
                .header(AUTHORIZATION, "Basic am9obi5zbWl0aEBseWNvcy5jb206anMqKg== realm=\"wrong realm\"")
                .get();

        response.close();

        assertThat(response.getStatus())
                .isEqualTo(UNAUTHORIZED.getStatusCode());

        assertThat(response.getHeaderString(WWW_AUTHENTICATE))
                .isEqualTo("Basic realm=\"example\"");
    }
}
