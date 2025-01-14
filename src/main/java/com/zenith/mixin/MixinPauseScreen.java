package com.zenith.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.zenith.ZenithProxyMod;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public class MixinPauseScreen {

    @Inject(method = "createPauseMenu", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/gui/layouts/GridLayout;arrangeElements()V"
    ))
    public void createPauseMenu(final CallbackInfo ci, @Local GridLayout gridLayout, @Local GridLayout.RowHelper rowHelper) {
        if (!ZenithProxyMod.onZenithServer()) return;
        rowHelper.addChild(Button.builder(Component.literal("Disconnect ZenithProxy"), button -> {
            button.active = false;
            ZenithProxyMod.fullDisconnect();
        }).width(204).build(), 2);
    }
}
