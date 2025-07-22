package com.bawnorton.sporeadditions.mixin;

import com.bawnorton.sporeadditions.SporeAdditions;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/entity/EntityTickList;forEach(Ljava/util/function/Consumer;)V"
            )
    )
    private void dontTickSporeMobsOnFrozen(EntityTickList instance, Consumer<Entity> entityConsumer, Operation<Void> original) {
        if (!SporeAdditions.isSporeFrozen((ServerLevel) (Object) this)) {
            original.call(instance, entityConsumer);
            return;
        }

        instance.forEach(entity -> {
            if (!SporeAdditions.isSporeEntity(entity)) {
                entityConsumer.accept(entity);
            }
        });
    }
}
