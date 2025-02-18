package com.bawnorton.rldeasier.mixin.srparasites;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityBiomass;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIV;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSV;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EntityBiomass.class)
public abstract class EntityBiomassMixin {
    @Shadow(remap = false) private EntityParasiteBase entityin;

    @ModifyArg(
            method = "explode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"
            )
    )
    private Entity addSpawnedFromBeckonTag(Entity entityout) {
        if(entityin instanceof EntityVenkrol
           || entityin instanceof EntityVenkrolSII
           || entityin instanceof EntityVenkrolSIII
           || entityin instanceof EntityVenkrolSIV
           || entityin instanceof EntityVenkrolSV) {
            entityout.addTag("spawnedFromBeckon");
        }
        return entityout;
    }
}
