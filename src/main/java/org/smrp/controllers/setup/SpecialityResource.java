package org.smrp.controllers.setup;

import java.util.HashMap;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.smrp.Constants;
import org.smrp.dtos.CommonSetupDto;
import org.smrp.dtos.KeywordDto;
import org.smrp.filters.Auth;
import org.smrp.models.CommonSetup;
import org.smrp.models.Pager;
import org.smrp.services.CommonSetupService;

import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Tag(name = "Setup/Speciality")
@SecurityRequirement(name = "BearerAuth")
@Path("/api")
@Auth
public class SpecialityResource {
    
    private final SecurityIdentity securityIdentity;
    private final CommonSetupService commonSetupService;
    private final String table = "speciality";
    
    public SpecialityResource(CommonSetupService commonSetupService, SecurityIdentity securityIdentity) {
        this.commonSetupService = commonSetupService;
        this.securityIdentity = securityIdentity;
    }

    @GET 
    @Path("/lookup/specialities")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<CommonSetup>> lookupList() {
        // String name = securityIdentity.getPrincipal().getName();
        // Long id = (Long) securityIdentity.getAttribute("id");
        return commonSetupService.findAll(table, 0, 0, "", "");
    }

    @GET
    @Path("/specialities")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> list(
        @QueryParam("_page") @DefaultValue("1") Integer page,
        @QueryParam("_limit") @DefaultValue("20") Integer limit,
        @QueryParam("sort") String sorts
    ) {
        String sortBy = "code";
        String sortDir = "asc";

        if (sorts != null && !sorts.isEmpty()) {
            String[] lis = sorts.split("\\$");
            String s = lis[0];
            String[] arr = s.split(":");
            sortBy = arr[0];
            sortDir = arr[1];
        }

        final String fsortBy = sortBy;
        final String fsortDir = sortDir;

        return commonSetupService.count(table).flatMap(total -> {
            Pager pg = Pager.from(total, page, limit);
            return commonSetupService.findAll(table, pg.getLowerBound(), pg.getPageSize(), fsortBy, fsortDir)
                .flatMap(items -> {
                    return Uni.createFrom().item(Response.ok(items)
                        .header(Constants.X_TOTAL_COUNT, total)
                        .header(Constants.X_TOTAL_PAGE, pg.getTotalPages())
                        .build());
                });
        });
    }

    @POST
    @Path("/specialities")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> searchList(
        @Valid KeywordDto data,
        @QueryParam("_page") @DefaultValue("1") Integer page,
        @QueryParam("_limit") @DefaultValue("20") Integer limit,
        @QueryParam("sort") String sorts
    ) {
        String sortBy = "code";
        String sortDir = "asc";

        if (sorts != null && !sorts.isEmpty()) {
            String[] lis = sorts.split("\\$");
            String s = lis[0];
            String[] arr = s.split(":");
            sortBy = arr[0];
            sortDir = arr[1];
        }

        final String key = String.format("%%%s%%", data.getKeyword());
        final String fsortBy = sortBy;
        final String fsortDir = sortDir;

        return commonSetupService.countByKeyword(key, table).flatMap(total -> {
            Pager pg = Pager.from(total, page, limit);
            return commonSetupService.findByKeyword(key, pg.getLowerBound(), pg.getPageSize(), fsortBy, fsortDir, table)
                .flatMap(items -> {
                    return Uni.createFrom().item(Response.ok(items)
                        .header(Constants.X_TOTAL_COUNT, total)
                        .header(Constants.X_TOTAL_PAGE, pg.getTotalPages())
                        .build());
                });
        });
    }

    @POST
    @Path("/speciality")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> create(@Valid CommonSetupDto data) {
        Long userId = (Long) securityIdentity.getAttribute("id");
        CommonSetup o = new CommonSetup();
        o.setCode(data.code());
        o.setDesc(data.desc());
        o.setRef(data.ref());
        o.setCreatedBy(userId);
        return commonSetupService.save(o, table).flatMap(x -> {
            HashMap<String, Object> mx = new HashMap<>();
            mx.put("success", 1);
            return Uni.createFrom().item(Response.ok(mx).build());
        });
    }

    @GET
    @Path("/speciality/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<CommonSetup> edit(@PathParam("id") Long id) {
        return commonSetupService.findById(id, table).flatMap(item -> {
            if (item == null) {
                return Uni.createFrom().failure(new NotFoundException("Record not found"));
            }
            return Uni.createFrom().item(item);
        });
    }

    @PUT
    @Path("/speciality/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> update(@PathParam("id") Long id, @Valid CommonSetupDto data) {
        Long userId = (Long) securityIdentity.getAttribute("id");
        return commonSetupService.findById(id, table).flatMap(item -> {
            if (item == null) {
                return Uni.createFrom().failure(new NotFoundException("Record not found"));
            }

            item.setCode(data.code());
            item.setDesc(data.desc());
            item.setRef(data.ref());
            item.setModifiedBy(userId);
            return commonSetupService.update(item, table).flatMap(x -> {
                HashMap<String, Object> mx = new HashMap<>();
                mx.put("success", 1);
                return Uni.createFrom().item(Response.ok(mx).build());
            });
        });
    }

    @DELETE
    @Path("/speciality/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> delete(@PathParam("id") Long id) {
        Long userId = (Long) securityIdentity.getAttribute("id");
        return commonSetupService.deleteById(id, userId, table).flatMap(x -> {
            HashMap<String, Object> mx = new HashMap<>();
            mx.put("success", 1);
            return Uni.createFrom().item(Response.ok(mx).build());
        });
    }
}
