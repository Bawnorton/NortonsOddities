package com.bawnorton.sporeadditions;

import com.Harbinger.Spore.Core.Sentities;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(SporeAdditions.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SporeAdditions {
    public static final String MOD_ID = "sporeadditions";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SporeAdditions() {}

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static boolean isSporeFrozen(ServerLevel world) {
        FreezeData freezeData = world.getDataStorage().computeIfAbsent(FreezeData::load, FreezeData::new, FreezeData.ID);
        return freezeData.isFrozen();
    }

    public static int freezeSpore(CommandContext<CommandSourceStack> context) {
        updateFrozenStatus(context, true, "Spore has been frozen", ChatFormatting.AQUA);
        return 1;
    }

    public static int unfreezeSpore(CommandContext<CommandSourceStack> context) {
        updateFrozenStatus(context, false, "Spore has been unfrozen", ChatFormatting.RED);
        return 1;
    }

    private static void updateFrozenStatus(CommandContext<CommandSourceStack> context, boolean frozen, String message, ChatFormatting colour) {
        ServerLevel world = context.getSource().getLevel();
        FreezeData freezeData = world.getDataStorage().computeIfAbsent(FreezeData::load, FreezeData::new, FreezeData.ID);
        if (freezeData.isFrozen() == frozen) {
            context.getSource().sendSystemMessage(Component.literal("Spore is already " + (frozen ? "frozen" : "unfrozen")).withStyle(ChatFormatting.GRAY));
            return;
        }
        freezeData.setFrozen(frozen);
        context.getSource().sendSystemMessage(Component.literal(message).withStyle(colour));
    }

    public static boolean isSporeEntity(Entity entity) {
        EntityType<?> type = entity.getType();
        return Sentities.SPORE_ENTITIES.getEntries()
                                .stream()
                                .map(RegistryObject::get)
                                .anyMatch(entry -> entry.equals(type));
    }

    public static int isSporeFrozen(CommandContext<CommandSourceStack> context) {
        ServerLevel world = context.getSource().getLevel();
        boolean isFrozen = isSporeFrozen(world);
        String message = isFrozen ? "Spore is currently frozen" : "Spore is not frozen";
        ChatFormatting color = isFrozen ? ChatFormatting.AQUA : ChatFormatting.RED;
        context.getSource().sendSystemMessage(Component.literal(message).withStyle(color));
        return 1;
    }
}
