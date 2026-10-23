package org.smrp.controllers.setup;

import java.util.HashMap;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.smrp.Constants;
import org.smrp.dtos.KeywordDto;
import org.smrp.dtos.UserDto;
import org.smrp.filters.Auth;
import org.smrp.models.Pager;
import org.smrp.models.User;
import org.smrp.services.RoleService;
import org.smrp.services.UserService;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
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


@Tag(name = "Setup/User")
@SecurityRequirement(name = "BearerAuth")
@Path("/api")
@Auth
public class UserResource {
    
    private final RoleService roleService;
    private final UserService userService;
    
    public UserResource(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> list(
        @QueryParam("_page") @DefaultValue("1") Integer page,
        @QueryParam("_limit") @DefaultValue("20") Integer limit,
        @QueryParam("sort") String sorts
    ) {
        String sortBy = "username";
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

        return userService.count().flatMap(total -> {
            Pager pg = Pager.from(total, page, limit);
            return userService.findAll(pg.getLowerBound(), pg.getPageSize(), fsortBy, fsortDir)
                .flatMap(items -> {
                    return Uni.createFrom().item(Response.ok(items)
                        .header(Constants.X_TOTAL_COUNT, total)
                        .header(Constants.X_TOTAL_PAGE, pg.getTotalPages())
                        .build());
                });
        });
    }

    @POST
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> searchList(
        @Valid KeywordDto data,
        @QueryParam("_page") @DefaultValue("1") Integer page,
        @QueryParam("_limit") @DefaultValue("20") Integer limit,
        @QueryParam("sort") String sorts
    ) {
        String sortBy = "username";
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

        return userService.countByKeyword(key).flatMap(total -> {
            Pager pg = Pager.from(total, page, limit);
            return userService.findByKeyword(key, pg.getLowerBound(), pg.getPageSize(), fsortBy, fsortDir)
                .flatMap(items -> {
                    return Uni.createFrom().item(Response.ok(items)
                        .header(Constants.X_TOTAL_COUNT, total)
                        .header(Constants.X_TOTAL_PAGE, pg.getTotalPages())
                        .build());
                });
        });
    }

    @POST
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> create(@Valid UserDto data) {
        return userService.existsByUsername(data.username()).flatMap(b -> {
            if (b) {
                return Uni.createFrom().failure(new BadRequestException("A user with that username already exists"));
            }

            return roleService.findById(data.roleId()).flatMap(role -> {
                if (role == null) {
                    return Uni.createFrom().failure(new NotFoundException("Role not found"));
                }

                User o = new User();
                o.setUsername(data.username());
                o.setPassword(data.password());
                o.setFirstName(data.firstname());
                o.setLastName(data.lastname());
                o.setRoles(List.of(role));
                return userService.save(o).flatMap(x -> {
                    HashMap<String, Object> mx = new HashMap<>();
                    mx.put("success", 1);
                    return Uni.createFrom().item(Response.ok(mx).build());
                });
            });
        });
    }

    @GET
    @Path("/user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<User> edit(@PathParam("id") Long id) {
        return userService.findById(id).flatMap(item -> {
            if (item == null) {
                return Uni.createFrom().failure(new NotFoundException("Record not found"));
            }
            return Uni.createFrom().item(item);
        });
    }

    @PUT
    @Path("/user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> update(@PathParam("id") Long id, @Valid UserDto data) {
        String password = data.password();
        return userService.existsByOtherUsername(data.username(), id).flatMap(b -> {
            if (b) {
                return Uni.createFrom().failure(new BadRequestException("A user with that username already exists"));
            }

            return roleService.findById(data.roleId()).flatMap(role -> {
                if (role == null) {
                    return Uni.createFrom().failure(new NotFoundException("Role not found"));
                }

                User o = new User();
                o.setId(id);
                o.setUsername(data.username());
                o.setPassword(!"********".equals(password) ? password : "");
                o.setFirstName(data.firstname());
                o.setLastName(data.lastname());
                o.setRoles(List.of(role));
                return userService.update(o).flatMap(x -> {
                    HashMap<String, Object> mx = new HashMap<>();
                    mx.put("success", 1);
                    return Uni.createFrom().item(Response.ok(mx).build());
                });
            });
        });
    }

    @DELETE
    @Path("/user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> delete(@PathParam("id") Long id) {
        return userService.deleteById(id).flatMap(x -> {
            HashMap<String, Object> mx = new HashMap<>();
            mx.put("success", 1);
            return Uni.createFrom().item(Response.ok(mx).build());
        });
    }
}
