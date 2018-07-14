package rest;

import data.XyzReport;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.test.grizzly.GrizzlyTestContainerFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.client.Entity.xml;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.UriBuilder.fromPath;
import static org.assertj.core.api.Assertions.assertThat;

public class HttpPatchTest {

    private final ReportAdapterRestResource resource = new ReportAdapterRestResource(new MyService());

    @Rule
    public final ResourceTestRule server = ResourceTestRule.builder()
            .setTestContainerFactory(new GrizzlyTestContainerFactory())
            .addProvider(JpaNoResultExceptionMapper.class)
            .addProvider(ServerRuntimeExceptionMapper.class)
            .addProvider(ResourceExceptionMapper.class)
            .addProvider(ServerErrorMapper.class)
            .addProvider(ObjectMapperProvider.class)
            .addProvider(RequestLogger.class)
            .addResource(() -> resource)
            .build();

    private int serverPort;

    @Before
    public void lookupFreeServerPort() {
        serverPort = server.target("/report-adapter")
                .getUri()
                .getPort();
    }

    @Test
    public void shouldPatch() {
        Response response = server.target("/report-adapter")
                .path("report/{id}")
                .resolveTemplate("id", 1)
                .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
                .request()
                .method("PATCH", xml(new XyzReport().setSomeData(5L)));
        response.close();

        assertThat(response.getStatus())
                .isEqualTo(OK.getStatusCode());
    }

    @Test
    public void shouldPatchDTOasJson() {
        Response response = server.target("/report-adapter")
                .path("report/{id}")
                .resolveTemplate("id", 1)
                .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
                .request()
                .method("PATCH", json(new XyzReport().setSomeData(5L)));
        response.close();

        assertThat(response.getStatus())
                .isEqualTo(OK.getStatusCode());
    }

    @Test
    public void defaultDropwizardClient() {
        Response response = ClientBuilder.newBuilder()
                .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
                .build()
                .target(fromPath("/report-adapter/report/1")
                                .scheme("http")
                                .host("localhost")
                                .port(serverPort)
                )
                .request()
                .method("PATCH", xml(new XyzReport().setSomeData(5L)));

        assertThat(response.getStatus())
                .isEqualTo(OK.getStatusCode());
    }

    /**
     * JavaScript Object Notation (JSON) Patch <br>
     * {@link <a href="https://tools.ietf.org/html/rfc6902">RFC-6902</a>}
     *
     * <pre>
     * [
     *     { "op": "test", "path": "/a/b/c", "value": "foo" },
     *     { "op": "remove", "path": "/a/b/c" },
     *     { "op": "add", "path": "/a/b/c", "value": [ "foo", "bar" ] },
     *     { "op": "replace", "path": "/a/b/c", "value": 42 },
     *     { "op": "move", "from": "/a/b/c", "path": "/a/b/d" },
     *     { "op": "copy", "from": "/a/b/d", "path": "/a/b/e" }
     * ]
     * </pre>
     */
    @Test
    public void jsonPatch() {
        JsonArray report = Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                        .add("op", "add")
                        .add("path", "/path/to/add/resource")
                        .add("value", "5"))
                .add(Json.createObjectBuilder()
                        .add("op", "remove")
                        .add("path", "/path/to/remove/resource"))
                .add(Json.createObjectBuilder()
                        .add("op", "move")
                        .add("from", "/path/to/move/resource/from")
                        .add("path", "/path/to/move/resource/to"))
                .build();

        Response response = server.target("/report-adapter")
                .path("report/{id}")
                .resolveTemplate("id", 1)
                .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
                .request()
                .method("PATCH", entity(report.toString(), "application/json-patch+json")); // here make an experiment with report.toString()

        assertThat(response.getStatus())
                .isEqualTo(OK.getStatusCode());
    }

    /**
     * JSON Merge Patch <br>
     * {@link <a href="https://tools.ietf.org/html/rfc7396">RFC-7396</a>}
     *
     * <pre>
     * {
     *     "title": "Hello!",
     *     "author" : {
     *         "givenName" : "John"
     *     },
     *     "tags": [ "example" ],
     *     "content": "This will be unchanged",
     *     "phoneNumber": "+01-123-456-7890"
     * }
     * </pre>
     */
    @Test
    public void jsonMergePatch() {
        JsonObject report = Json.createObjectBuilder()
                .add("title", "Hello!")
                .add("author",
                        Json.createObjectBuilder()
                                .add("givenName", "John"))
                .add("tags",
                        Json.createArrayBuilder()
                                .add("example"))
                .add("content", "This will be unchanged")
                .add("phoneNumber", "+01-123-456-7890")
                .build();

        Response response = server.target("/report-adapter")
                .path("report/{id}")
                .resolveTemplate("id", 1)
                .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
                .request()
                .method("PATCH", entity(report.toString(), "application/merge-patch+json")); // here make an experiment with report.toString()

        assertThat(response.getStatus())
                .isEqualTo(OK.getStatusCode());
    }
}
