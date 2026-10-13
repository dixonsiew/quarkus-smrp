package org.smrp.controllers.report.master_pd102;

import java.util.HashMap;
import java.util.List;

import org.bson.Document;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.smrp.Utils;
import org.smrp.filters.Auth;
import org.smrp.models.Pager;

import io.quarkus.mongodb.FindOptions;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.mongodb.reactive.ReactiveMongoDatabase;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Tag(name = "Report/MasterPD102")
@SecurityRequirement(name = "BearerAuth")
@Path("/api")
@Auth
public class MasterPD102Resource {
    
    @ConfigProperty(name = "mongodb.prefix")
    String mongodbPrefix;
    
    private final SecurityIdentity securityIdentity;
    private final ReactiveMongoClient mongoClient;
    
    public MasterPD102Resource(SecurityIdentity securityIdentity, ReactiveMongoClient mongoClient) {
        this.securityIdentity = securityIdentity;
        this.mongoClient = mongoClient;
    }

    @GET
    @Path("/master-pd102/rpt1")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> list(
        @QueryParam("_page") @DefaultValue("1") Integer page,
        @QueryParam("_limit") @DefaultValue("20") Integer limit,
        @QueryParam("datefrom") @DefaultValue("") String datefrom,
        @QueryParam("dateto") @DefaultValue("") String dateto
    ) {
        String username = securityIdentity.getPrincipal().getName();
        ReactiveMongoDatabase db = getDb();
        ReactiveMongoCollection<Document> col = db.getCollection(String.format("__%s__", username));
        ReactiveMongoCollection<Document> col2 = db.getCollection(String.format("__%s-q__", username));
        return col.countDocuments().flatMap(total -> {
            return col2.find().collect().asList().flatMap(ld -> {
                final String dateFrom = ld.size() > 0 ? ld.get(0).getString("datefrom") : datefrom;
                final String dateTo = ld.size() > 0 ? ld.get(0).getString("dateto") : dateto;

                Pager pg = Pager.from(total, page, limit);
                FindOptions options = new FindOptions()
                    .skip(pg.getLowerBound())
                    .limit(pg.getPageSize());

                return col.find(options).collect().asList().flatMap(ls -> {
                    List<Document> lr = Utils.processDoc(ls);
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("columnmaps", ColMap.COLUMN_MAP);
                    result.put("total_count", total);
                    result.put("total_page", pg.getTotalPages());
                    result.put("page", pg.getPageNum());
                    result.put("data", lr);
                    result.put("datefrom", dateFrom);
                    result.put("dateto", dateTo);
                    return Uni.createFrom().item(Response.ok(result).build());
                });
            });
        });
    }

    private ReactiveMongoCollection<Document> getCollection(String username) {
        String s = String.format("__%s__", username);
        ReactiveMongoDatabase db = getDb();
        return db.getCollection(s);
    }

    private ReactiveMongoDatabase getDb() {
        String suffix = "prod".equals(mongodbPrefix) ? "_prod" : "";
        String s = String.format("master_pd102%s", suffix);
        return mongoClient.getDatabase(s);
    }
}
