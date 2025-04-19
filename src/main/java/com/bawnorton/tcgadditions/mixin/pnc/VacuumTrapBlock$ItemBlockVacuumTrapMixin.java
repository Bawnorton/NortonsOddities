package com.bawnorton.tcgadditions.mixin.pnc;

import me.desht.pneumaticcraft.common.block.VacuumTrapBlock;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(VacuumTrapBlock.ItemBlockVacuumTrap.class)
public abstract class VacuumTrapBlock$ItemBlockVacuumTrapMixin {
    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/BlockItem;<init>(Lnet/minecraft/world/level/block/Block;Lnet/minecraft/world/item/Item$Properties;)V"
            )
    )
    private static Item.Properties makeFireproof(Item.Properties properties) {
        return properties.fireResistant();
    }
}
