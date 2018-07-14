package resource;

import annotations.SecuredBasic;
import annotations.SecuredBearer;

import javax.annotation.security.RolesAllowed;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

@Path("/secure/car/components")
public class RootResource {

    @Context
    private SecurityContext ctx;

    @POST
    @Path("motor")
    @Consumes(APPLICATION_FORM_URLENCODED)
    @Produces(APPLICATION_JSON)
    public Response loadMotorDetails(@FormParam("j_username") String usr, @FormParam("j_password") String pswd) {
        JsonObject report = Json.createObjectBuilder()
                .add("reportName", "car-motor")
                .add("tradeoff", "Nissan")
                .add("numberOfParts", 1)
                .build();

        // old serializer - need newer Jersey, javax.json-api, javax.json, jackson-datatype-jsr353, jersey-media-json-jackson.
        return (isAuthorized(usr, pswd) ? ok(report.toString()) : status(UNAUTHORIZED)).build();
    }

    @GET
    @SecuredBearer
    @Path("motor")
    @Produces(APPLICATION_JSON)
    public Response loadMotorDetails() {
        JsonObject report = Json.createObjectBuilder()
                .add("reportName", "car-motor")
                .add("tradeoff", "Nissan")
                .add("numberOfParts", 1)
                .build();

        // old serializer - need newer Jersey, javax.json-api, javax.json, jackson-datatype-jsr353, jersey-media-json-jackson.
        return ok(report.toString()).build();
    }

    @GET
    @SecuredBasic
    @RolesAllowed("admin")
    @Path("nisan")
    @Produces(APPLICATION_JSON)
    public Response loadMotorDetailsWithBasicAuth() {
        Principal userPrincipal = ctx.getUserPrincipal();

        JsonObject report = Json.createObjectBuilder()
                .add("reportName", "car-motor")
                .add("tradeoff", "Nissan")
                .add("numberOfParts", 1)
                .build();

        // old serializer - need newer Jersey, javax.json-api, javax.json, jackson-datatype-jsr353, jersey-media-json-jackson.
        return ok(report.toString()).build();
    }

    private static boolean isAuthorized(String username, String password) {
        return "nissan@car".equals(username) && "tr454".equals(password);
    }
}
