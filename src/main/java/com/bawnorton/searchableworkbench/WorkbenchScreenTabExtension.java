package com.bawnorton.searchableworkbench;

import com.bawnorton.searchableworkbench.mixin.WorkbenchScreen$TabAccessor;
import com.mrcrayfish.guns.crafting.WorkbenchRecipe;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WorkbenchScreenTabExtension {
    private final WorkbenchScreen$TabAccessor accessor;
    private final List<WorkbenchRecipe> recipes;
    private String lastSearchTerm = "";

    public WorkbenchScreenTabExtension(Object fieldInstance) {
        this.accessor = (WorkbenchScreen$TabAccessor) fieldInstance;
        recipes = new ArrayList<>(accessor.getItems());
    }

    public boolean filterItems(String newText) {
        if(newText.equals(lastSearchTerm)) {
            return false;
        }
        lastSearchTerm = newText;
        if(newText.isEmpty()) {
            accessor.setItems(recipes);
            return false;
        }

        List<WorkbenchRecipe> toShow = new ArrayList<>(recipes);
        toShow.removeIf(recipe -> !recipe.getResultItem()
                .getHoverName()
                .getString()
                .toLowerCase(Locale.ENGLISH)
                .contains(newText.toLowerCase(Locale.ENGLISH))
        );
        accessor.setItems(toShow);
        return true;
    }

    public int getRecipeCount() {
        return accessor.getItems().size();
    }
}
