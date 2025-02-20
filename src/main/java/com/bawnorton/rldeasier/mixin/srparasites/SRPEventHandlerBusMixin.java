package com.bawnorton.rldeasier.mixin.srparasites;

import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.util.handlers.SRPEventHandlerBus;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SRPEventHandlerBus.class)
public abstract class SRPEventHandlerBusMixin {
    @Unique
    private static final ThreadLocal<String[]> DROPPING = ThreadLocal.withInitial(() -> new String[]{"minecraft:air"});
    @Unique
    private static final ThreadLocal<Boolean> WILL_DROP = ThreadLocal.withInitial(() -> false);

    @ModifyExpressionValue(
            method = "setLoot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;isPotionActive(Lnet/minecraft/potion/Potion;)Z",
                    ordinal = 0,
                    remap = true
            ),
            remap = false
    )
    private boolean ignoreEffectIfSpawnedFromBeckon(boolean original, LivingDropsEvent event) {
        return original && !event.getEntity().getTags().contains("spawnedFromBeckon");
    }

    @ModifyExpressionValue(
            method = "loot",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;split(Ljava/lang/String;)[Ljava/lang/String;"
            ),
            remap = false
    )
    private String[] captureDropping(String[] dropping) {
        DROPPING.set(dropping);
        return dropping;
    }

    @ModifyExpressionValue(
            method = "loot",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Boolean;parseBoolean(Ljava/lang/String;)Z"
            ),
            remap = false
    )
    private boolean alwaysDropLivingMaterials(boolean original, LivingDropsEvent event) {
        if(original) {
            WILL_DROP.set(true);
            return true;
        }
        if(!event.getEntityLiving().getTags().contains("spawnedFromBeckon")) return false;

        Item item = Item.getByNameOrId(DROPPING.get()[0]);
        if(item == null) return false;

        WILL_DROP.set(item.equals(SRPItems.ahull_drop) || item.equals(SRPItems.ashyco_drop) || item.equals(SRPItems.acanra_drop));
        return WILL_DROP.get();
    }

    @ModifyExpressionValue(
            method = "loot",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Integer;parseInt(Ljava/lang/String;)I",
                    ordinal = 1
            ),
            remap = false
    )
    private int alwaysDropLivingMaterials(int original, LivingDropsEvent event) {
        return WILL_DROP.get() ? 100 : original;
    }

    @ModifyExpressionValue(
            method = "loot",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Integer;parseInt(Ljava/lang/String;)I",
                    ordinal = 0
            ),
            remap = false
    )
    private int dropMoreMaterials(int original, LivingDropsEvent event) {
        Item item = Item.getByNameOrId(DROPPING.get()[0]);
        if(WILL_DROP.get() && item != null && item.equals(SRPItems.ashyco_drop)) return (original + 1) * 2;
        return original;
    }
}
