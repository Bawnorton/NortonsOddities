package com.bawnorton.searchableworkbench.mixin;

import com.bawnorton.searchableworkbench.WorkbenchScreenTabExtension;
import com.mrcrayfish.guns.client.screen.WorkbenchScreen;
import com.mrcrayfish.guns.common.container.WorkbenchContainer;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

@Mixin(WorkbenchScreen.class)
public abstract class WorkbenchScreenMixin extends AbstractContainerScreenMixin<WorkbenchContainer> {

    @Shadow(remap = false) protected abstract void loadItem(int index);

    @Unique
    private static final VarHandle searchableWorkbench$currentTabHandle;
    @Unique
    private WorkbenchScreenTabExtension searchableWorkbench$currentTab;

    @Unique
    private EditBox searchableWorkbench$searchBar;

    protected WorkbenchScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lcom/mrcrayfish/guns/client/screen/WorkbenchScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;", ordinal = 0))
    private void addSearchBar(CallbackInfo ci) {
        int rightEdge = leftPos + imageWidth;
        int searchableWorkbench$searchBarWidth = 97;
        int searchBarX = rightEdge - 5 - searchableWorkbench$searchBarWidth;
        searchableWorkbench$searchBar = addRenderableWidget(new EditBox(
                font,
                searchBarX,
                topPos + 4,
                searchableWorkbench$searchBarWidth,
                font.lineHeight,
                Component.literal("Search...")
        ));
        searchableWorkbench$searchBar.setFocus(true);
        searchableWorkbench$searchBar.setResponder(newText -> {
            searchableWorkbench$currentTab = searchableWorkbench$getCurrentTab();
            if(searchableWorkbench$currentTab.filterItems(newText)) {
                loadItem(0);
            }
        });
    }

    @Inject(method = "loadItem", at = @At("HEAD"), cancellable = true, remap = false)
    private void dontLoadNoItem(CallbackInfo ci) {
        searchableWorkbench$currentTab = searchableWorkbench$getCurrentTab();
        int count = searchableWorkbench$currentTab.getRecipeCount();
        if (count == 0) {
            ci.cancel();
        }
    }

    @Inject(method = "mouseClicked", at = @At(value = "FIELD", target = "Lcom/mrcrayfish/guns/client/screen/WorkbenchScreen;currentTab:Lcom/mrcrayfish/guns/client/screen/WorkbenchScreen$Tab;", opcode = Opcodes.PUTFIELD, remap = false))
    private void invalidateCurrentTab(CallbackInfoReturnable<Boolean> cir) {
        searchableWorkbench$searchBar.setValue("");
        searchableWorkbench$currentTab = null;
        searchableWorkbench$searchBar.setFocus(true);
    }

    @Override
    protected void onKeyPressed(int pKeyCode, int pScanCode, int pModifiers, CallbackInfoReturnable<Boolean> cir) {
        searchableWorkbench$searchBar.keyPressed(pKeyCode, pScanCode, pModifiers);
        if(pKeyCode != GLFW.GLFW_KEY_ESCAPE) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private @NotNull WorkbenchScreenTabExtension searchableWorkbench$getCurrentTab() {
        if (searchableWorkbench$currentTab != null) {
            return searchableWorkbench$currentTab;
        }

        Object instance = searchableWorkbench$currentTabHandle.get(this);
        return searchableWorkbench$currentTab = new WorkbenchScreenTabExtension(instance);
    }

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(WorkbenchScreen.class, MethodHandles.lookup());
            Field currentTabField = WorkbenchScreen.class.getDeclaredField("currentTab");
            searchableWorkbench$currentTabHandle = lookup.findVarHandle(WorkbenchScreen.class, "currentTab", currentTabField.getType());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
