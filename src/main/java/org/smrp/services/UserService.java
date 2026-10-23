package org.smrp.services;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

import org.mindrot.jbcrypt.BCrypt;
import org.smrp.models.Role;
import org.smrp.models.User;


@ApplicationScoped 
public class UserService {
    
    private final Pool reactivePool;
    
    public UserService(Pool reactivePool) {
        this.reactivePool = reactivePool;
    }

    public Uni<User> findById(Long id) {
        return reactivePool.preparedQuery("select id, username, first_name, last_name, password, last_login from app_user where id = $1 limit 1")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? User.fromRow(iterator.next()) : null).chain(o -> {
                    if (o == null) {
                        return Uni.createFrom().item(o);
                    }
                    return setRole(o);
                });
    }

    public Uni<User> findByUsername(String username) {
        return reactivePool.preparedQuery("select id, username, first_name, last_name, password, last_login from app_user where username = $1 limit 1")
                .execute(Tuple.of(username))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? User.fromRow(iterator.next()) : null).chain(o -> {
                    if (o == null) {
                        return Uni.createFrom().item(o);
                    }
                    return setRole(o);
                });
    }

    public Uni<List<User>> findAll(int offset, int limit, String sortBy, String sortDir) {
        String q = "select t.id, t.username, t.first_name, t.last_name, t.password, t.last_login from app_user t order by %s %s offset $1 limit $2";
        q = String.format(q, sortBy, sortDir);
        Uni<List<User>> uni = reactivePool.preparedQuery(q)
                .execute(Tuple.of(offset, limit))
                .map(rows -> rows.stream().map(User::fromRow).collect(Collectors.toList()));
        return uni.onItem().transformToMulti(list -> Multi.createFrom().iterable(list))
                .onItem().transformToUniAndMerge(user -> setRole(user))
                .collect().asList();
    }

    public Uni<Long> count() {
        return reactivePool.preparedQuery("select count(id) from app_user")
                .execute()
                .map(rowSet -> rowSet.iterator().hasNext() ? rowSet.iterator().next().getLong(0) : 0L);
    }

    public Uni<Boolean> existsByOtherUsername(String username, Long id) {
        return reactivePool.preparedQuery("select exists (select 1 from app_user t where t.username = $1 and t.id <> $2)")
                .execute(Tuple.of(username, id))
                .map(rowSet -> rowSet.iterator().hasNext() && rowSet.iterator().next().getBoolean(0) ? Boolean.TRUE : Boolean.FALSE);
    }

    public Uni<Boolean> existsByUsername(String username) {
        return reactivePool.preparedQuery("select exists (select 1 from app_user t where t.username = $1)")
                .execute(Tuple.of(username))
                .map(rowSet -> rowSet.iterator().hasNext() && rowSet.iterator().next().getBoolean(0) ? Boolean.TRUE : Boolean.FALSE);
    }

    public Uni<List<User>> findByKeyword(String keyword, int offset, int limit, String sortBy, String sortDir) {
        String q = "select t.id, t.username, t.first_name, t.last_name, t.password, t.last_login from app_user t where (t.username ilike $1 or t.first_name ilike $2 or t.last_name ilike $3) order by %s %s offset $4 limit $5";
        q = String.format(q, sortBy, sortDir);
        Uni<List<User>> uni = reactivePool.preparedQuery(q)
                .execute(Tuple.of(keyword, keyword, keyword, offset, limit))
                .map(rows -> rows.stream().map(User::fromRow).collect(Collectors.toList()));
        return uni.onItem().transformToMulti(list -> Multi.createFrom().iterable(list))
                .onItem().transformToUniAndMerge(user -> setRole(user))
                .collect().asList();
    }

    public Uni<Long> countByKeyword(String keyword) {
        String q = "select count(id) from app_user t where (t.username ilike $1 or t.first_name ilike $2 or t.last_name ilike $3)";
        return reactivePool.preparedQuery(q)
                .execute(Tuple.of(keyword, keyword, keyword))
                .map(rowSet -> rowSet.iterator().hasNext() ? rowSet.iterator().next().getLong(0) : 0L);
    }

    public Uni<Void> save(User o) {
        String pwd = BCrypt.hashpw(o.getPassword(), BCrypt.gensalt());
        return reactivePool.withTransaction(con -> {
            return con.preparedQuery("insert into app_user (id, username, password, first_name, last_name, active) values(nextval('app_user_id_seq'),$1,$2,$3,$4,$5) returning id as app_user_id")
                    .execute(Tuple.of(o.getUsername(), pwd, o.getFirstName(), o.getLastName(), true))
                    .onItem().transformToUni(rowSet -> {
                        Long id = rowSet.iterator().next().getLong("app_user_id");
                        List<Uni<Void>> tasks = o.getRoles().stream().map(r -> {
                            return con.preparedQuery("insert into app_user_roles (app_user_id, roles_id) values($1, $2)")
                                    .execute(Tuple.of(id, r.getId()))
                                    .replaceWithVoid();
                        }).collect(Collectors.toList());
                        return Uni.join().all(tasks).andFailFast().replaceWithVoid();
                    });
            });
    }

    public Uni<Void> update(User o) {
        return reactivePool.withTransaction(con -> {
            Uni<Void> u1;

            if (o.getPassword() != null && !o.getPassword().isEmpty()) {
                String pwd = BCrypt.hashpw(o.getPassword(), BCrypt.gensalt());
                u1 = con.preparedQuery("update app_user set password = $1, first_name = $2, last_name = $3 where id = $4")
                            .execute(Tuple.of(pwd, o.getFirstName(), o.getLastName(), o.getId()))
                            .replaceWithVoid();
            } else {
                u1 = con.preparedQuery("update app_user set first_name = $1, last_name = $2 where id = $3")
                            .execute(Tuple.of(o.getFirstName(), o.getLastName(), o.getId()))
                            .replaceWithVoid();
            }

            Uni<Void> u2 = con.preparedQuery("delete from app_user_roles where app_user_id = $1")
                    .execute(Tuple.of(o.getId()))
                    .replaceWithVoid();

            List<Uni<Void>> unis = o.getRoles().stream().map(r -> {
                return con.preparedQuery("insert into app_user_roles (app_user_id, roles_id) values($1,$2)")
                        .execute(Tuple.of(o.getId(), r.getId()))
                        .replaceWithVoid();
            }).collect(Collectors.toList());
            Uni<Void> u3 = Uni.join().all(unis).andFailFast().replaceWithVoid();
            
            return u1.chain(() -> u2.chain(() -> u3));
        });
    }

    public Uni<Void> deleteById(Long id) {
        return reactivePool.withTransaction(con -> {
            Uni<Void> u1 = reactivePool.preparedQuery("delete from app_user_roles where app_user_id = $1")
                    .execute(Tuple.of(id))
                    .replaceWithVoid();
            Uni<Void> u2 = reactivePool.preparedQuery("delete from app_user where id = $1")
                    .execute(Tuple.of(id))
                    .replaceWithVoid();
            return u1.chain(() -> u2);
        });
    }

    public Uni<Void> updateLastLogin(Long id) {
        return reactivePool.preparedQuery("update app_user set last_login = now() where id = $1")
                .execute(Tuple.of(id))
                .replaceWithVoid();
    }

    public Uni<Void> updatePassword(User o) {
        String pwd = BCrypt.hashpw(o.getPassword(), BCrypt.gensalt());
        return reactivePool.preparedQuery("update app_user set password = $1 where id = $2")
                .execute(Tuple.of(pwd, o.getId()))
                .replaceWithVoid();
    }

    public boolean validateCredentials(User user, String password) {
        return BCrypt.checkpw(password, user.getPassword());
    }

    private Uni<User> setRole(User o) {
        Uni<List<Role>> lx = reactivePool.preparedQuery("select r.id, r.name from app_user_roles aur inner join role r on aur.roles_id = r.id where aur.app_user_id = $1")
                .execute(Tuple.of(o.getId()))
                .map(rows -> rows.stream().map(Role::fromRow).collect(Collectors.toList()));
        return lx.map(items -> {
            o.setRoles(items);
            return o;
        });
    }
}
