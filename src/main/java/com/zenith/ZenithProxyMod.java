package com.zenith;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.world.level.GameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZenithProxyMod implements ClientModInitializer {
    public static final Logger LOG = LoggerFactory.getLogger("ZenithProxy");

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

    @Override
    public void onInitializeClient() {

    }
}
