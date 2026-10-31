package org.smrp.jobs;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import io.quarkus.logging.Log;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MainJob {

    private final WebClient webClient;

    public MainJob(Vertx vertx) {
        WebClientOptions options = new WebClientOptions()
                .setMaxPoolSize(50)
                .setConnectTimeout(5000);
        this.webClient = WebClient.create(vertx, options);
    }

    @Scheduled(every = "10s")
    void executeEveryTen() {
        Log.infof("Simple Job every 10 seconds: %s", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("username", "admin");
        m.put("password", "admin123");

        Uni<Map<String, Object>> data = webClient.postAbs("http://localhost:8300/smrp/o/token")
            .sendJson(m)
            .map(res -> {
                if (res.statusCode() == 200) {
                    JsonObject jsonObject = res.bodyAsJsonObject();
                    Map<String, Object> map = jsonObject.getMap();
                    return map;
                } else {
                    throw new RuntimeException("Failed with status code: " + res.statusCode());
                }
            });
        data.subscribe().with(mx -> {
            Log.info(mx);
            getUser(String.valueOf(mx.get("token")));
        });
    }

    private void getUser(String token) {
        Uni<Map<String, Object>> md = webClient.getAbs("http://localhost:8300/smrp/api/current-user")
            .putHeader("Authorization", String.format("Bearer %s", token))
            .send()
            .map(res -> {
                if (res.statusCode() == 200) {
                    JsonObject jsonObject = res.bodyAsJsonObject();
                    Map<String, Object> map = jsonObject.getMap();
                    return map;
                } else {
                    throw new RuntimeException("Failed with status code: " + res.statusCode());
                }
            });
        md.subscribe().with(ma -> {
            Log.info(ma);
        });
    }
}
