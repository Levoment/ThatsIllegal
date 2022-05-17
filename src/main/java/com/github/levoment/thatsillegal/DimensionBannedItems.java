package com.github.levoment.thatsillegal;

import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DimensionBannedItems {
    Map<String, List<String>> CraftingBannedIngredients = new HashMap<>();
    Map<String, List<String>> FullyBannedItems = new HashMap<>();

    public DimensionBannedItems() {
        // Create the default structure of this object for the Json
        CraftingBannedIngredients.put(World.OVERWORLD.getValue().toString(), new ArrayList<>());
        CraftingBannedIngredients.put(World.NETHER.getValue().toString(), new ArrayList<>());
        CraftingBannedIngredients.put(World.END.getValue().toString(), new ArrayList<>());
        CraftingBannedIngredients.put("all_dimensions", new ArrayList<>());

        FullyBannedItems.put(World.OVERWORLD.getValue().toString(), new ArrayList<>());
        FullyBannedItems.put(World.NETHER.getValue().toString(), new ArrayList<>());
        FullyBannedItems.put(World.END.getValue().toString(), new ArrayList<>());
        FullyBannedItems.put("all_dimensions", new ArrayList<>());
    }
}
