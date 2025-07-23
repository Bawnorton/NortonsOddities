package com.bawnorton.sporeadditions.mixin;

import com.Harbinger.Spore.SBlockEntities.BiomassLumpEntity;
import com.Harbinger.Spore.SBlockEntities.BrainRemnantBlockEntity;
import com.Harbinger.Spore.SBlockEntities.OutpostWatcherBlockEntity;
import com.Harbinger.Spore.SBlockEntities.OvergrownSpawnerEntity;
import com.bawnorton.sporeadditions.SporeAdditions;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Level.class)
public abstract class LevelMixin {
    @Shadow
    @Nullable
    public abstract BlockEntity getBlockEntity(BlockPos pPos);

    @SuppressWarnings("ConstantValue")
    @ModifyReturnValue(
            method = "shouldTickBlocksAt(Lnet/minecraft/core/BlockPos;)Z",
            at = @At("RETURN")
    )
    private boolean dontTickSporeBlocksIfFrozen(boolean original, BlockPos pos) {
        if (!original) return false;

        if (!((Object) this instanceof ServerLevel level)) return true;
        if (!SporeAdditions.isSporeFrozen(level)) return true;

        BlockEntity blockEntity = getBlockEntity(pos);
        return !(blockEntity instanceof OutpostWatcherBlockEntity
                 || blockEntity instanceof OvergrownSpawnerEntity
                 || blockEntity instanceof BrainRemnantBlockEntity);
    }
}
