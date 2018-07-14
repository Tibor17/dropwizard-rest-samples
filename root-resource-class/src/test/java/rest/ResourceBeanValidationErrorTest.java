package rest;

import data.XyzReport;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.glassfish.jersey.test.grizzly.GrizzlyTestContainerFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static javax.ws.rs.client.Entity.xml;
import static org.assertj.core.api.Assertions.assertThat;

public class ResourceBeanValidationErrorTest {

    private final ReportAdapterRestResource resource = new ReportAdapterRestResource(new MyService());

    @Rule
    public final ResourceTestRule server = ResourceTestRule.builder()
            .setTestContainerFactory(new GrizzlyTestContainerFactory())
            .addProvider(JpaNoResultExceptionMapper.class)
            .addProvider(ServerRuntimeExceptionMapper.class)
            .addProvider(ResourceExceptionMapper.class)
            .addProvider(ServerErrorMapper.class)
            .addProvider(ValidationExceptionMapper.class)
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
    public void shouldFailValidation() {
        assertThat(serverPort)
                .isNotZero();

        assertThat(server.target("/report-adapter/report/1")
                .request()
                .get(XyzReport.class))
                .isEqualTo(new XyzReport().setSomeData(5L));

        XyzReport invalidReport = new XyzReport().setSomeData(-5L);
        Response response = server.target("/report-adapter")
                .path("report/{id}")
                .resolveTemplate("id", 1)
                .request()
                .put(xml(invalidReport));

        response.close();

        assertThat(response.getStatus())
                // in case of GrizzlyTestContainerFactory
                .isEqualTo(422); // Unprocessable Entity
        // in case of GrizzlyWebTestContainerFactory
        //.isEqualTo(INTERNAL_SERVER_ERROR.getStatusCode());
    }
}
