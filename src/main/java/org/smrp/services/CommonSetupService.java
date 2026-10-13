package org.smrp.services;

// import jakarta.inject.Inject;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

import org.smrp.models.CommonSetup;


@ApplicationScoped
public class CommonSetupService {
    
    // @Inject
    private final Pool reactivePool; // Injected reactive pool client

    public CommonSetupService(Pool reactivePool) {
        this.reactivePool = reactivePool;
    }

    public Uni<CommonSetup> findById(Long id, String table) {
        String q = "select id, code, \"desc\", ref, created_by, created_date, modified_by, modified_date, deleted, deleted_by, deleted_date from %s where id = $1 limit 1";
        q = String.format(q, table);
        return reactivePool.preparedQuery(q)
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? CommonSetup.fromRow(iterator.next()) : null);
    }

    public Uni<CommonSetup> findByDesc(String desc, String table) {
        String q = "select t.id, t.code, t.desc, t.ref, t.created_by, t.created_date, t.modified_by, t.modified_date, t.deleted, t.deleted_by, t.deleted_date from %s t where lower(t.desc) = lower($1) limit 1";
        q = String.format(q, table);
        return reactivePool.preparedQuery(q)
                .execute(Tuple.of(desc))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? CommonSetup.fromRow(iterator.next()) : null);
    }

    public Uni<List<CommonSetup>> findAll(String table, int offset, int limit, String sortBy, String sortDir) {
        if (limit > 0) {
            String q = "select t.id, t.code, t.desc, t.ref, t.created_by, t.created_date, t.modified_by, t.modified_date, t.deleted, t.deleted_by, t.deleted_date from %s t where t.deleted is not true order by \"%s\" %s offset $1 limit $2";
            q = String.format(q, table, sortBy, sortDir);
            return reactivePool.preparedQuery(q)
                    .execute(Tuple.of(offset, limit))
                    .map(rows -> rows.stream().map(CommonSetup::fromRow).collect(Collectors.toList()));
        } else {
            String q = "select t.id, t.code, t.desc, t.ref, t.created_by, t.created_date, t.modified_by, t.modified_date, t.deleted, t.deleted_by, t.deleted_date from %s t where t.desc <> '' and t.deleted is not true order by t.code";
            q = String.format(q, table);
            return reactivePool.query(q).execute()
                    .map(rows -> rows.stream().map(CommonSetup::fromRow).collect(Collectors.toList()));
        }
    }

    public Uni<Long> count(String table) {
        String q = "select count(id) from %s t where t.deleted is not true";
        q = String.format(q, table);
        return reactivePool.preparedQuery(q)
                .execute()
                .map(rowSet -> rowSet.iterator().hasNext() ? rowSet.iterator().next().getLong(0) : 0L);
    }

    public Uni<List<CommonSetup>> findByKeyword(String keyword, int offset, int limit, String sortBy, String sortDir, String table) {
        String q = "select t.id, t.code, t.desc, t.ref, t.created_by, t.created_date, t.modified_by, t.modified_date, t.deleted, t.deleted_by, t.deleted_date from %s t where (t.code ilike $1 or t.desc ilike $2 or t.ref ilike $3) and t.deleted is not true order by \"%s\" %s offset $4 limit $5";
        q = String.format(q, table, sortBy, sortDir);
        return reactivePool.preparedQuery(q)
                .execute(Tuple.of(keyword, keyword, keyword, offset, limit))
                .map(rows -> rows.stream().map(CommonSetup::fromRow).collect(Collectors.toList()));
    }

    public Uni<Long> countByKeyword(String keyword, String table) {
        String q = "select count(id) from %s t where (t.code ilike $1 or t.desc ilike $2 or t.ref ilike $3) and t.deleted is not true";
        q = String.format(q, table);
        return reactivePool.preparedQuery(q)
                .execute(Tuple.of(keyword, keyword, keyword))
                .map(rowSet -> rowSet.iterator().hasNext() ? rowSet.iterator().next().getLong(0) : 0L);
    }

    public Uni<Void> save(CommonSetup o, String table) {
        String q = "insert into %s (id, code, \"desc\", ref, created_by, created_date) values(nextval('%s_id_seq'),$1,$2,$3,$4,now())";
        q = String.format(q, table, table);
        return reactivePool.preparedQuery(q)
                .execute(Tuple.of(o.getCode(), o.getDesc(), o.getRef(), o.getCreatedBy()))
                .replaceWithVoid();
    }

    public Uni<Void> update(CommonSetup o, String table) {
        String q = "update %s set code = $1, \"desc\" = $2, ref = $3, modified_by = $4, modified_date = now() where id = $5";
        q = String.format(q, table);
        return reactivePool.preparedQuery(q)
                .execute(Tuple.of(o.getCode(), o.getDesc(), o.getRef(), o.getModifiedBy(), o.getId()))
                .replaceWithVoid();
    }

    public Uni<Void> deleteById(Long id, Long userId, String table) {
        String q = "update %s set deleted = true, deleted_by = $1, deleted_date = now() where id = $2";
        q = String.format(q, table);
        return reactivePool.preparedQuery(q)
                .execute(Tuple.of(userId, id))
                .replaceWithVoid();
    }
}

// https://www.google.com/search?q=Uni%3CRowSet%3CRow%3E%3E+to+pojo&oq=Uni%3CRowSet%3CRow%3E%3E+to+pojo&sourceid=chrome&ie=UTF-8
// https://quarkus.io/guides/reactive-sql-clients/#reactive-sql-clients-details
