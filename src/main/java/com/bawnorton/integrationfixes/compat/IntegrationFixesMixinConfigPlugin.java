package com.bawnorton.integrationfixes.compat;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class IntegrationFixesMixinConfigPlugin implements IMixinConfigPlugin {
    private boolean isModLoaded(String id) {
        LoadingModList lml = LoadingModList.get();
        for (ModInfo info : lml.getMods()) {
            if(info.getModId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("compat")) {
            String modid = mixinClassName.substring(
                    mixinClassName.indexOf("compat.") + "compat.".length(),
                    mixinClassName.lastIndexOf(".")
            );
            return isModLoaded(modid);
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
