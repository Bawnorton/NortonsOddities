package com.bawnorton.tcgadditions.mixin.collectorsalbum;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.tnt.collectorsalbum.client.screen.CardPackOpeningScreen;

@Mixin(CardPackOpeningScreen.class)
public abstract class CardPackOpeningScreenMixin {
    @Unique
    private static final double tcgaditions$SCALE = 5;

    @ModifyExpressionValue(
            method = "init",
            at = @At(
                    value = "CONSTANT",
                    args = "intValue=40"
            )
    )
    private int scaleInvariantToGuiScale(int value) {
        return (int) (value * tcgaditions$SCALE / Minecraft.getInstance().getWindow().getGuiScale());
    }


    @ModifyExpressionValue(
            method = "onCardFlipped",
            at = @At(
                    value = "CONSTANT",
                    args = "doubleValue=4.0"
            ),
            remap = false
    )
    private double scaleInvariantToGuiScale(double value) {
        return value * tcgaditions$SCALE / Minecraft.getInstance().getWindow().getGuiScale();
    }

    @Mixin(targets = "team.tnt.collectorsalbum.client.screen.CardPackOpeningScreen$CardWidget")
    private abstract static class CardWidgetMixin extends AbstractWidget {
        public CardWidgetMixin(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
            super(pX, pY, pWidth, pHeight, pMessage);
        }

        @Inject(
                method = "<init>",
                at = @At("RETURN")
        )
        private void scaleInvariantToGuiScale(CallbackInfo ci) {
            double scale = tcgaditions$SCALE / Minecraft.getInstance().getWindow().getGuiScale();
            width = (int) (width * scale);
            height = (int) (height * scale);
        }
    }

    @Mixin(targets = "team.tnt.collectorsalbum.client.screen.CardPackOpeningScreen$FxElement")
    private abstract static class FxElementMixin {
        @ModifyExpressionValue(
                method = "draw",
                at = @At(
                        value = "CONSTANT",
                        args = "floatValue=8.0"
                ),
                remap = false
        )
        private float scaleInvariantToGuiScale(float value) {
            return (float) (value * tcgaditions$SCALE / Minecraft.getInstance().getWindow().getGuiScale());
        }
    }
}