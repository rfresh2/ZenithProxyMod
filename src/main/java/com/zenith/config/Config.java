package com.zenith.config;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;

import static com.zenith.ZenithProxyMod.LOG;

public class Config {

    public final ArrayList<WebApiInstance> webApiList = new ArrayList<>();

    public static final class WebApiInstance {
        public String id = "";
        public String ip = "";
        public String token = "";
    }

    public static Path configPath = FabricLoader.getInstance().getConfigDir().resolve("zenithproxy.json");
    static Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .setLenient()
        .create();

    public static Config loadConfig() {
        if (!configPath.toFile().exists()) {
            return new Config();
        }
        try (var reader = new FileReader(configPath.toFile())) {
            return gson.fromJson(reader, Config.class);
        } catch (Exception e) {
            LOG.error("Failed to load config", e);
            return new Config();
        }
    }

    public void save() {
        try {
            File tempFile = File.createTempFile("zenithproxy-mod", null);
            try (var writer = new FileWriter(tempFile)) {
                gson.toJson(this, writer);
            }
            Files.move(tempFile, configPath.toFile());
        } catch (Exception e) {
            LOG.error("Failed to write config", e);
        }
    }
}
