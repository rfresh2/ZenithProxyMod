package com.zenith.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zenith.web.model.CommandRequest;
import com.zenith.web.model.CommandResponse;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class WebAPI {
    public static final WebAPI INSTANCE = new WebAPI();

    final Gson gson = new GsonBuilder().create();

    public CommandResponse execute(String command, String ip, String token) throws IOException, InterruptedException {
        CommandRequest commandRequest = new CommandRequest(command);
        String commandJson = gson.toJson(commandRequest);
        try (var client = buildHttpClient()) {
            String url = "";
            if (!(ip.startsWith("http://") || ip.startsWith("https://"))) {
                url = "http://";
            }
            url += ip + "/command";
            HttpRequest request = buildBaseRequest(url, token)
                .POST(HttpRequest.BodyPublishers.ofString(commandJson))
                .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String bodyJson = response.body();
            return gson.fromJson(bodyJson, CommandResponse.class);
        }
    }

    protected HttpClient buildHttpClient() {
        return HttpClient.newBuilder()
            .followRedirects(java.net.http.HttpClient.Redirect.ALWAYS)
            .connectTimeout(Duration.ofSeconds(2))
            .build();
    }

    protected HttpRequest.Builder buildBaseRequest(final String uri, String token) {
        return HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .header("User-Agent", "ZenithProxyMod/" + FabricLoader.getInstance().getModContainer("zenithproxy").get().getMetadata().getVersion().getFriendlyString())
            .header("Accept", "application/json")
            .header("Authorization", token)
            .timeout(Duration.ofSeconds(15));
    }
}
