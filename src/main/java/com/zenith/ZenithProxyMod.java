package com.zenith;

import com.zenith.config.Config;
import com.zenith.web.WebAPI;
import com.zenith.web.model.CommandResponse;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ForkJoinPool;

import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static com.zenith.config.Config.WebApiInstance;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ZenithProxyMod implements ClientModInitializer {
    public static final Logger LOG = LoggerFactory.getLogger("ZenithProxy");
    public static Config config;

    public static boolean inZenithSpectator() {
        var mc = Minecraft.getInstance();
        if (!onZenithServer()) return false;
        if (mc.gameMode == null) return false;
        return mc.gameMode.getPlayerMode() == GameType.SPECTATOR;
    }

    public static boolean onZenithServer() {
        var mc = Minecraft.getInstance();
        ServerData currentServer = mc.getCurrentServer();
        if (currentServer == null) return false;
        String serverVersionStr = currentServer.version.tryCollapseToString();
        if (serverVersionStr == null) return false;
        return serverVersionStr.startsWith("ZenithProxy");
    }

    public static void fullDisconnect() {
        var mc = Minecraft.getInstance();
        if (mc.player == null) return;
        mc.player.connection.sendUnsignedCommand("disconnect");
    }

    public static void swapSpectatorMode() {
        var mc = Minecraft.getInstance();
        if (mc.player == null) return;
        mc.player.connection.sendUnsignedCommand("swap");
    }

    @Override
    public void onInitializeClient() {
        config = Config.loadConfig();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("api")
                .then(literal("add")
                    .then(argument("id", string()).then(argument("url", string()).then(argument("token", string()).executes(c -> {
                        var id = getString(c, "id");
                        var url = getString(c, "url");
                        var token = getString(c, "token");
                        config.webApiList.removeIf(i -> i.id.equals(id));
                        var instance = new WebApiInstance();
                        instance.id = id;
                        instance.ip = url;
                        instance.token = token;
                        config.webApiList.add(instance);
                        c.getSource().sendFeedback(Component.literal("Added Web API instance: " + id));
                        config.save();
                        return 1;
                    })))))
                .then(literal("del")
                    .then(argument("id", string()).executes(c -> {
                        var id = getString(c, "id");
                        config.webApiList.removeIf(i -> i.id.equals(id));
                        c.getSource().sendFeedback(Component.literal("Removed Web API instance: " + id));
                        config.save();
                        return 1;
                    })))
                .then(literal("list").executes(c -> {
                    var instances = config.webApiList;
                    if (instances.isEmpty()) {
                        c.getSource().sendFeedback(Component.literal("No Web API instances configured"));
                    } else {
                        c.getSource().sendFeedback(Component.literal("Web API instances:"));
                        for (var instance : instances) {
                            c.getSource().sendFeedback(Component.literal(instance.id + ": " + instance.ip));
                        }
                    }
                    return 1;
                }))
                .then(literal("command")
                    .then(argument("id", string()).then(argument("command", greedyString()).executes(c -> {
                        String command = getString(c, "command");
                        String id = getString(c, "id");
                        var instance = config.webApiList.stream()
                            .filter(i -> i.id.equals(id))
                            .findFirst();
                        if (instance.isEmpty()) {
                            c.getSource().sendFeedback(Component.literal("No Web API instance with id: " + id));
                            return 1;
                        }
                        var webApiInstance = instance.get();
                        var ip = webApiInstance.ip;
                        var token = webApiInstance.token;
                        ForkJoinPool.commonPool().execute(() -> {
                            try {
                                CommandResponse response = WebAPI.INSTANCE.execute(command, ip, token);
                                var component = Component.Serializer.fromJson(response.embedComponent(), Minecraft.getInstance().player.registryAccess());
                                c.getSource().sendFeedback(component);
                            } catch (Exception e) {
                                LOG.error("Error executing WebAPI command: {}", command, e);
                                c.getSource().sendFeedback(Component.literal("Error: " + e.getClass().getSimpleName() + e.getMessage()));
                            }
                        });
                        return 1;
                    })))));
        });
    }
}
