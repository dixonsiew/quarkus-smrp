package org.smrp.services;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

import org.smrp.models.Role;


@ApplicationScoped
public class RoleService {
    
    private final Pool reactivePool;
    
    public RoleService(Pool reactivePool) {
        this.reactivePool = reactivePool;
    }

    public Uni<List<Role>> findAll(String sortBy, String sortDir) {
        String q = "select id, name from role order by %s %s";
        q = String.format(q, sortBy, sortDir);
        return reactivePool.query(q).execute()
                .map(rows -> rows.stream().map(Role::fromRow).collect(Collectors.toList()));
    }

    public Uni<Role> findById(Long id) {
        String q = "select id, name from role where id = $1 limit 1";
        return reactivePool.preparedQuery(q)
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? Role.fromRow(iterator.next()) : null);
    }
}
