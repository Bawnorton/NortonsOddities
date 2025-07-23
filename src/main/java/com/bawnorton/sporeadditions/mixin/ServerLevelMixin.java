package com.bawnorton.sporeadditions.mixin;

import com.bawnorton.sporeadditions.SporeAdditions;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
    protected ServerLevelMixin(WritableLevelData pLevelData, ResourceKey<Level> pDimension, RegistryAccess pRegistryAccess, Holder<DimensionType> pDimensionTypeRegistration, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed, int pMaxChainedNeighborUpdates) {
        super(pLevelData, pDimension, pRegistryAccess, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed, pMaxChainedNeighborUpdates);
    }

    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/entity/EntityTickList;forEach(Ljava/util/function/Consumer;)V"
            )
    )
    private void dontTickSporeMobsIfFrozen(EntityTickList instance, Consumer<Entity> entityConsumer, Operation<Void> original) {
        if (!SporeAdditions.isSporeFrozen((ServerLevel) (Object) this)) {
            original.call(instance, entityConsumer);
            return;
        }

        instance.forEach(entity -> {
            if (!SporeAdditions.isSporeEntity(entity)) {
                entityConsumer.accept(entity);
            } else {
                try {
                    entity.baseTick();
                } catch (Exception e) {
                    entity.remove(Entity.RemovalReason.DISCARDED);
                    entity.kill();
                    SporeAdditions.LOGGER.error("Error base ticking spore entity while frozen: {}", entity, e);
                }
            }
        });
    }

    @Inject(
            method = "addFreshEntity",
            at = @At("HEAD"),
            cancellable = true
    )
    private void dontSpawnSporeMobsIfFrozen(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (SporeAdditions.isSporeFrozen((ServerLevel) (Object) this) && SporeAdditions.isSporeEntity(entity)) {
            cir.setReturnValue(false);
        }
    }

    @WrapOperation(
            method = "tickChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;isRandomlyTicking()Z"
            )
    )
    private boolean dontTickSporeBlocksIfFrozen(BlockState instance, Operation<Boolean> original) {
        boolean tick = original.call(instance);
        if (!tick) return false;

        return !(SporeAdditions.isSporeFrozen((ServerLevel) (Object) this) && SporeAdditions.isSporeBlock(instance.getBlock()));
    }
}
