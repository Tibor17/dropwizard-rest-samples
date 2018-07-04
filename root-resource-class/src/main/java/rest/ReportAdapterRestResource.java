package rest;

import data.XyzReport;
import samples.PATCH;

import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.HttpURLConnection;

import static javax.ws.rs.core.MediaType.*;
import static javax.ws.rs.core.Response.ok;

@Path("/report-adapter")
public class ReportAdapterRestResource {
    private final MyService myService;

    @Inject
    public ReportAdapterRestResource(MyService myService) {
        this.myService = myService;
    }

    @GET
    @Path("do/error")
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    @Logger
    public XyzReport doError() {
        return myService.produceError();
    }

    @GET
    @Path("no/content")
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public XyzReport noContent() {
        return myService.noContent();
    }

    @GET
    @Path("xyz/{id: \\d+}")
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public XyzReport filterXyzReport(@PathParam("id") int id) {
        return myService.filterXyzReport(id);
    }

    @GET
    @Path("report/{id}")
    @Produces(APPLICATION_XML)
    public Response findReportById(@PathParam("id") int id, @Context UriInfo uriInfo/*Request req*/) {
        XyzReport report = new XyzReport()
                .setSomeData(5L);

        return ok(report).build();
    }

    @PUT
    @Path("report/{id}")
    @Consumes(APPLICATION_XML)
    public Response updateReport(@PathParam("id") long id, @NotNull @Valid XyzReport report) {
        // merge report with given id;
        // this is another alternative to correct solution with: Response.ok().build();
        return Response.status(HttpURLConnection.HTTP_OK).build();
    }

    @POST
    @Path("report/{id}")
    @Consumes(APPLICATION_XML)
    public Response createReport(@PathParam("id") long id, @NotNull @Valid XyzReport report) {
        // merge report with given id;
        // here use static import Response.ok;
        return Response.ok().build();
    }

    @PATCH
    @Path("report/{id}")
    @Consumes(APPLICATION_XML)
    public Response patchReport(@PathParam("id") long id, @NotNull @Valid XyzReport report) {
        // merge report with given id;
        return ok().build();
    }

    @PATCH
    @Path("report/{id}")
    @Consumes(APPLICATION_JSON)
    public Response patchJsonReport(@PathParam("id") long id, @NotNull @Valid XyzReport report) {
        // merge report with given id;
        return ok().build();
    }

    /**
     * This is implemented according to
     * {@link <a href="https://williamdurand.fr/2014/02/14/please-do-not-patch-like-an-idiot/">Please. Don't Patch Like An Idiot.</a>}.
     * <br>
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
    @PATCH
    @Path("report/{id}")
    @Consumes("application/json-patch+json")
    public Response jsonPatch(@PathParam("id") long id, @Valid JsonArray report) {
        // merge report with given id;
        return ok().build();
    }

    /**
     * This is implemented according to
     * {@link <a href="https://williamdurand.fr/2014/02/14/please-do-not-patch-like-an-idiot/">Please. Don't Patch Like An Idiot.</a>}.
     * <br>
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
    @PATCH
    @Path("report/{id}")
    @Consumes("application/merge-patch+json")
    public Response jsonMergePatch(@PathParam("id") long id, @Valid JsonObject report) {
        // merge report with given id;
        return ok().build();
    }
}
