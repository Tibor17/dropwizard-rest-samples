import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.URL;

import static org.jboss.shrinkwrap.api.ShrinkWrap.create;

@RunWith(Arquillian.class)
public class RestResourceIT {

    @ArquillianResource
    private URL base;

    @Deployment(testable = true, name = "full-setup-test")
    @TargetsContainer("arquillian-wildfly-managed")
    @OverProtocol("Servlet 3.0")
    public static Archive<?> createDeployment() {
        PomEquippedResolveStage pom = Maven.resolver().loadPomFromFile("pom.xml");

        return create(MavenImporter.class)
                .loadPomFromFile("pom.xml")
                .importBuildOutput()
                .as(WebArchive.class)
                /*.addAsLibraries(pom.resolve("org.assertj:assertj-core").withTransitivity().asFile())
                .addAsLibraries(pom.resolve("io.rest-assured:rest-assured").withTransitivity().asFile())
                .addClasses(AuditRestResourceTestData.class)*/
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml"), "web.xml");
    }


    @Test
    //@OperateOnDeployment(value = "full-setup-test")
    public void test() throws Exception {
    }
}
