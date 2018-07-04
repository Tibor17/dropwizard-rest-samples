package rest;

import data.XyzReport;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.glassfish.jersey.test.grizzly.GrizzlyTestContainerFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * http://www.dropwizard.io/0.9.0/docs/manual/validation.html
 *
 */
public class ResourceTest {

    private final ReportAdapterRestResource resource = new ReportAdapterRestResource(new MyService());

    @Rule
    public final ResourceTestRule server = ResourceTestRule.builder()
            //.setTestContainerFactory(new GrizzlyWebTestContainerFactory())
            .setTestContainerFactory(new GrizzlyTestContainerFactory())
            .addProvider(ResourceExceptionMapper.class)
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
    public void doError() {
        Response response = server.target("/report-adapter")
                .path("do/error")
                .request()
                .get();

        response.close();

        assertThat(response.getStatus())
                .isEqualTo(INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void noContent() {
        Response response = server.target("/report-adapter")
                .path("no/content")
                .request()
                .get();

        response.close();

        assertThat(response.getStatus())
                .isEqualTo(NO_CONTENT.getStatusCode());
    }

    @Test
    public void filterXyzReport() {
        Response response = server.target("/report-adapter")
                .path("xyz/{id}")
                .resolveTemplate("id", 3)
                .request()
                .get();

        assertThat(response.getStatus())
                .isEqualTo(OK.getStatusCode());

        assertThat(response.readEntity(XyzReport.class))
                .isEqualTo(new XyzReport().setSomeData(3L));

        response.close();
    }

    @Test
    public void filterXyzReportNull() {
        Response response = server.target("/report-adapter")
                .path("xyz/{id}")
                .resolveTemplate("id", 1)
                .request()
                .get();

        response.close();

        assertThat(response.getStatus())
                .isEqualTo(NO_CONTENT.getStatusCode());
    }
}
