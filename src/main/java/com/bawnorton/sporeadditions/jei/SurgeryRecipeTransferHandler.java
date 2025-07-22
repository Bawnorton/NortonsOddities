package com.bawnorton.sporeadditions.jei;

import com.Harbinger.Spore.Recipes.SurgeryRecipe;
import com.Harbinger.Spore.Screens.SurgeryMenu;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.common.network.packets.PacketRecipeTransfer;
import mezz.jei.common.transfer.RecipeTransferOperationsResult;
import mezz.jei.common.transfer.RecipeTransferUtil;
import mezz.jei.library.transfer.BasicRecipeTransferHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import java.util.Collections;
import java.util.List;

public class SurgeryRecipeTransferHandler extends BasicRecipeTransferHandler<SurgeryMenu, SurgeryRecipe> {
    private final IConnectionToServer serverConnection;
    private final IStackHelper stackHelper;
    private final IRecipeTransferHandlerHelper handlerHelper;
    private final SurgeryRecipeTransferInfo transferInfo;

    public SurgeryRecipeTransferHandler(IConnectionToServer serverConnection, IStackHelper stackHelper, IRecipeTransferHandlerHelper handlerHelper, SurgeryRecipeTransferInfo transferInfo) {
        super(serverConnection, stackHelper, handlerHelper, transferInfo);
        this.serverConnection = serverConnection;
        this.stackHelper = stackHelper;
        this.handlerHelper = handlerHelper;
        this.transferInfo = transferInfo;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(SurgeryMenu container, SurgeryRecipe recipe, IRecipeSlotsView recipeSlotsView, Player player, boolean maxTransfer, boolean doTransfer) {
        List<Slot> craftingSlots = Collections.unmodifiableList(transferInfo.getRecipeSlots(container, recipe));
        List<Slot> inventorySlots = Collections.unmodifiableList(transferInfo.getInventorySlots(container, recipe));

        List<IRecipeSlotView> inputItemSlotViews = recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT);
        inputItemSlotViews.add(recipeSlotsView.getSlotViews(RecipeIngredientRole.CATALYST).get(0));
        if (!validateRecipeView(transferInfo, container, craftingSlots, inputItemSlotViews)) {
            return handlerHelper.createInternalError();
        }

        InventoryState inventoryState = getInventoryState(craftingSlots, inventorySlots, player, container, transferInfo);
        if (inventoryState == null) {
            return handlerHelper.createInternalError();
        }
        int inputCount = inputItemSlotViews.size();
        if (!inventoryState.hasRoom(inputCount)) {
            Component message = Component.translatable("jei.tooltip.error.recipe.transfer.inventory.full");
            return handlerHelper.createUserErrorWithTooltip(message);
        }

        RecipeTransferOperationsResult transferOperations = RecipeTransferUtil.getRecipeTransferOperations(
                stackHelper,
                inventoryState.availableItemStacks(),
                inputItemSlotViews,
                craftingSlots
        );

        if (!transferOperations.missingItems.isEmpty()) {
            Component message = Component.translatable("jei.tooltip.error.recipe.transfer.missing");
            return handlerHelper.createUserErrorForMissingSlots(message, transferOperations.missingItems);
        }

        if (!RecipeTransferUtil.validateSlots(player, transferOperations.results, craftingSlots, inventorySlots)) {
            return handlerHelper.createInternalError();
        }

        if (doTransfer) {
            boolean requireCompleteSets = transferInfo.requireCompleteSets(container, recipe);
            PacketRecipeTransfer packet = new PacketRecipeTransfer(
                    transferOperations.results,
                    craftingSlots,
                    inventorySlots,
                    maxTransfer,
                    requireCompleteSets
            );
            serverConnection.sendPacketToServer(packet);
        }

        return null;
    }
}
