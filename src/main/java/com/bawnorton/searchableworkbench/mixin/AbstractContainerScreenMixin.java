package com.bawnorton.searchableworkbench.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen {
    @Shadow protected int leftPos;

    @Shadow protected int imageWidth;

    @Shadow protected int topPos;

    protected AbstractContainerScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    protected void onKeyPressed(int pKeyCode, int pScanCode, int pModifiers, CallbackInfoReturnable<Boolean> cir) {
        // no-op
    }
}
