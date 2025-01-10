package com.zenith.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ServerSelectionList.class)
public abstract class MixinServerSelectionList extends ObjectSelectionList<ServerSelectionList.Entry> {

    @Final @Shadow private JoinMultiplayerScreen screen;

    public MixinServerSelectionList(final Minecraft minecraft, final int i, final int j, final int k, final int l) {
        super(minecraft, i, j, k, l);
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (handleMouseClick(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Unique
    private boolean handleMouseClick(final double mouseX, final double mouseY, final int button) {
        List<ServerSelectionList.Entry> children = this.children();
        int left = this.getRowLeft();

        for (int i = 0; i < children.size(); i++) {
            ServerSelectionList.Entry entry = children.get(i);
            if (!(entry instanceof ServerSelectionList.OnlineServerEntry onlineServerEntry)) continue;
            int top = getRowTop(i);
            int rowBottom = getRowBottom(i);
            if (rowBottom < this.getY() || top > getBottom()) continue;
            ServerData serverData = onlineServerEntry.getServerData();
            String serverVersionStr = serverData.version.tryCollapseToString();
            if (serverVersionStr == null) return false;
            if (!serverVersionStr.startsWith("ZenithProxy")) continue;
            boolean zHovering = mouseX >= left - 35 && mouseX <= left - 3 && mouseY >= top && mouseY <= top + 32;
            if (!zHovering) continue;
            Map<ResourceLocation, byte[]> cookies = new HashMap<>();
            cookies.put(ResourceLocation.tryBuild("zenith", "zenith-transfer-src"), serverData.ip.getBytes(StandardCharsets.UTF_8));
            cookies.put(ResourceLocation.tryBuild("zenith", "zenith-spectator"), String.valueOf(true).getBytes(StandardCharsets.UTF_8));
            TransferState transferState = new TransferState(cookies);
            ConnectScreen.startConnecting(this.screen, this.minecraft, ServerAddress.parseString(serverData.ip), serverData, false, transferState);
            return true;
        }
        return false;
    }

    @Override
    public void renderWidget(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        List<ServerSelectionList.Entry> children = this.children();
        int left = this.getRowLeft();

        for (int i = 0; i < children.size(); i++) {
            ServerSelectionList.Entry entry = children.get(i);
            if (!(entry instanceof ServerSelectionList.OnlineServerEntry onlineServerEntry)) continue;
            int top = getRowTop(i);
            int rowBottom = getRowBottom(i);
            if (rowBottom < this.getY() || top > getBottom()) continue;
            ServerData serverData = onlineServerEntry.getServerData();
            String serverVersionStr = serverData.version.tryCollapseToString();
            if (serverVersionStr == null) return;
            if (!serverVersionStr.startsWith("ZenithProxy")) continue;
            boolean zHovering = mouseX >= left - 35 && mouseX <= left - 3 && mouseY >= top && mouseY <= top + 32;
            guiGraphics.blitSprite(zHovering
                                       ? ServerSelectionList.JOIN_HIGHLIGHTED_SPRITE
                                       : ServerSelectionList.JOIN_SPRITE,
                                   left - 35, top, 32, 32);
        }
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }
}
