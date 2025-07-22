package com.bawnorton.sporeadditions.mixin;

import com.bawnorton.sporeadditions.SporeAdditions;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Commands.class)
public abstract class CommandsMixin {
    @ModifyReceiver(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/brigadier/CommandDispatcher;setConsumer(Lcom/mojang/brigadier/ResultConsumer;)V"
            )
    )
    private CommandDispatcher<CommandSourceStack> registerCommands(CommandDispatcher<CommandSourceStack> instance, ResultConsumer<CommandSourceStack> consumer) {
        instance.register(Commands.literal("sporeadditions")
                         .then(Commands.literal("freeze")
                                       .executes(SporeAdditions::freezeSpore)
                         )
                         .then(Commands.literal("unfreeze")
                                       .executes(SporeAdditions::unfreezeSpore)
                         )
        );
        return instance;
    }
}
