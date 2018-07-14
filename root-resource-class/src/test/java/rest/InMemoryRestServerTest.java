package rest;

import data.XyzReport;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.glassfish.jersey.test.inmemory.InMemoryTestContainerFactory;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryRestServerTest {

    private final ReportAdapterRestResource resource = new ReportAdapterRestResource(new MyService());

    @Rule
    public final ResourceTestRule server = ResourceTestRule.builder()
            .setTestContainerFactory(new InMemoryTestContainerFactory())
            .addProvider(JpaNoResultExceptionMapper.class)
            .addProvider(ServerRuntimeExceptionMapper.class)
            .addProvider(ResourceExceptionMapper.class)
            .addProvider(ServerErrorMapper.class)
            .addProvider(ObjectMapperProvider.class)
            .addProvider(RequestLogger.class)
            .addResource(() -> resource)
            .build();

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
}
