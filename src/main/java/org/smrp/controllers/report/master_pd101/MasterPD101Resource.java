package org.smrp.controllers.report.master_pd101;

import java.util.HashMap;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
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
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static com.mongodb.client.model.Filters.eq;


@Tag(name = "Report/MasterPD101")
@SecurityRequirement(name = "BearerAuth")
@Path("/api")
@Auth
public class MasterPD101Resource {

    @ConfigProperty(name = "mongodb.prefix")
    String mongodbPrefix;
    
    private final SecurityIdentity securityIdentity;
    private final ReactiveMongoClient mongoClient;
    
    public MasterPD101Resource(SecurityIdentity securityIdentity, ReactiveMongoClient mongoClient) {
        this.securityIdentity = securityIdentity;
        this.mongoClient = mongoClient;
    }

    @GET
    @Path("/master-pd101/rpt1")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> list(
        @QueryParam("_page") @DefaultValue("1") Integer page,
        @QueryParam("_limit") @DefaultValue("20") Integer limit,
        @QueryParam("vt") @DefaultValue("0") String vt,
        @QueryParam("datefrom") @DefaultValue("") String datefrom,
        @QueryParam("dateto") @DefaultValue("") String dateto
    ) {
        String username = securityIdentity.getPrincipal().getName();
        ReactiveMongoDatabase db = getDb(vt);
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

    @GET
    @Path("/master-pd101/rpt1/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> edit(@PathParam("id") String id, @QueryParam("vt") @DefaultValue("0") String vt) {
        String username = securityIdentity.getPrincipal().getName();
        ReactiveMongoCollection<Document> col = getCollection(username, vt);
        try {
            ObjectId objectId = new ObjectId(id);
            return col.find(eq("_id", objectId)).collect().first().flatMap(doc -> {
                doc.put("_id", doc.getObjectId("_id").toString());
                return Uni.createFrom().item(Response.ok(doc).build());
            }).onItem().ifNull().switchTo(Uni.createFrom().failure(new NotFoundException("Record not found")));
        } catch (Exception e) {
            throw new NotFoundException("Record not found");
        }
    }

    private ReactiveMongoCollection<Document> getCollection(String username, String vt) {
        String s = String.format("__%s__", username);
        ReactiveMongoDatabase db = getDb(vt);
        return db.getCollection(s);
    }

    private ReactiveMongoDatabase getDb(String vt) {
        String suffix = "prod".equals(mongodbPrefix) ? "_prod" : "";
        ReactiveMongoDatabase db;
        if ("0".equals(vt)) {
            String s = String.format("master_pd101%s", suffix);
            db = mongoClient.getDatabase(s);
        } else {
            String s = String.format("master_rh101%s", suffix);
            db = mongoClient.getDatabase(s);
        }
        
        return db;
    }
}
