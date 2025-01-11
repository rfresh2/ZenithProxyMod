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
import net.minecraft.client.renderer.RenderType;
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

    @Unique private static final ResourceLocation COOKIE_KEY_TRANSFER_SRC = ResourceLocation.tryBuild("zenith", "zenith-transfer-src");
    @Unique private static final ResourceLocation COOKIE_KEY_SPECTATOR = ResourceLocation.tryBuild("zenith", "zenith-spectator");
    @Final @Shadow private JoinMultiplayerScreen screen;
    @Unique final int buttonWidth = 32;
    @Unique final int buttonHeight = 32;
    // empty space in the left of the sprite png
    // the sprite is 32x32, but the actual button is 20x32
    @Unique private static final int SPRITE_LEFT_OFFSET = 12;

    public MixinServerSelectionList(final Minecraft minecraft, final int i, final int j, final int k, final int l) {
        super(minecraft, i, j, k, l);
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (button == 0 && handleMouseClick(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Unique
    private boolean handleMouseClick(final double mouseX, final double mouseY, final int button) {
        List<ServerSelectionList.Entry> children = this.children();
        int left = this.getRowLeft();
        int width = this.getRowWidth();

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
            int buttonBoundLeft = getButtonBoundLeft(left, width);
            int buttonBoundRight = getButtonBoundRight(left, width);
            int buttonBoundTop = getButtonBoundTop(top);
            int buttonBoundBottom = getButtonBoundBottom(top);
            boolean zHovering = isHoveringOverButton((int) mouseX, (int) mouseY, buttonBoundLeft, buttonBoundRight, buttonBoundTop, buttonBoundBottom);
            if (!zHovering) continue;
            Map<ResourceLocation, byte[]> cookies = new HashMap<>();
            cookies.put(COOKIE_KEY_TRANSFER_SRC, serverData.ip.getBytes(StandardCharsets.UTF_8));
            cookies.put(COOKIE_KEY_SPECTATOR, String.valueOf(true).getBytes(StandardCharsets.UTF_8));
            TransferState transferState = new TransferState(cookies);
            ConnectScreen.startConnecting(this.screen, this.minecraft, ServerAddress.parseString(serverData.ip), serverData, false, transferState);
            return true;
        }
        return false;
    }

    @Override
    public void renderWidget(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        List<ServerSelectionList.Entry> children = this.children();
        int left = this.getRowLeft();
        int width = this.getRowWidth();

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
            int buttonBoundLeft = getButtonBoundLeft(left, width);
            int buttonBoundRight = getButtonBoundRight(left, width);
            int buttonBoundTop = getButtonBoundTop(top);
            int buttonBoundBottom = getButtonBoundBottom(top);
            boolean zHovering = isHoveringOverButton(mouseX, mouseY, buttonBoundLeft, buttonBoundRight, buttonBoundTop, buttonBoundBottom);
            guiGraphics.blitSprite(RenderType::guiTextured, zHovering
                                       ? ServerSelectionList.JOIN_HIGHLIGHTED_SPRITE
                                       : ServerSelectionList.JOIN_SPRITE,
                                   buttonBoundLeft, buttonBoundTop, buttonWidth, buttonHeight);
        }
    }

    @Unique
    private boolean isHoveringOverButton(final int mouseX, final int mouseY, final int buttonBoundLeft, final int buttonBoundRight, final int buttonBoundTop, final int buttonBoundBottom) {
        return mouseX >= (buttonBoundLeft + SPRITE_LEFT_OFFSET) && mouseX <= buttonBoundRight && mouseY >= buttonBoundTop && mouseY <= buttonBoundBottom;
    }

    @Unique
    private int getButtonBoundTop(final int top) {
        return top;
    }

    @Unique
    private int getButtonBoundBottom(final int top) {
        return top + buttonHeight;
    }

    @Unique
    private int getButtonBoundRight(final int left, final int width) {
        return left + width + buttonWidth
            + 6 // scrollbar width
            - 3; // padding
    }

    @Unique
    private int getButtonBoundLeft(final int left, final int width) {
        return left + width
            + 6 // scrollbar width
            - 3; // padding
    }
}
