package org.smrp.services;

import java.util.List;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class IncrementService {
    
    private final ReactiveKeyCommands<String> keyCommands;
    private final ReactiveValueCommands<String, Long> countCommands;

    public IncrementService(ReactiveRedisDataSource reactive) {
        // Retrieve commands for counter operations (imperative)
        this.countCommands = reactive.value(Long.class);
        // Retrieve commands for key operations (reactive)
        this.keyCommands = reactive.key();
    }

    public Uni<Long> get(String key) {
        return countCommands.get(key).onItem().ifNull().continueWith(0L);
    }

    public Uni<Void> set(String key, Long value) {
        return countCommands.set(key, value).replaceWithVoid();
    }

    public Uni<Void> increment(String key, Long incrementBy) {
        return countCommands.incrby(key, incrementBy).replaceWithVoid();
    }

    public Uni<Integer> del(String key) {
        return keyCommands.del(key);
    }

    public Uni<List<String>> keys() {
        return keyCommands.keys("*");
    }
}
