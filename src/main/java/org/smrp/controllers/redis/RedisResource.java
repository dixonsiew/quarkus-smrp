package org.smrp.controllers.redis;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.smrp.controllers.redis.models.Increment;
import org.smrp.services.IncrementService;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;


@Tag(name = "Redis")
@Path("/redis")
public class RedisResource {

    private final IncrementService incrementService;
    
    public RedisResource(IncrementService incrementService) {
        this.incrementService = incrementService;
    }

    @GET
    public Uni<List<String>> keys() {
        return incrementService.keys();
    }

    @POST
    public Uni<Void> create(Increment increment) {
        return incrementService.set(increment.key, increment.value);
    }

    @GET
    @Path("/{key}")
    public Uni<Increment> get(@PathParam("key") String key) {
        return incrementService.get(key).onItem().transform(value -> new Increment(key, value));
    }

    @PUT
    @Path("/{key}")
    public Uni<Void> increment(@PathParam("key") String key, long value) {
        return incrementService.increment(key, value);
    }

    @DELETE
    @Path("/{key}")
    public Uni<Void> delete(@PathParam("key") String key) {
        return incrementService.del(key);
    }
}
