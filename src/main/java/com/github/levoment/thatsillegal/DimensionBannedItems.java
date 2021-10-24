package com.github.levoment.thatsillegal;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DimensionBannedItems {
    Map<String, List<String>> dimensionItems = new HashMap<>();

    public DimensionBannedItems() {
        // Create the default structure of this object for the Json
        dimensionItems.put(World.OVERWORLD.getValue().toString(), new ArrayList<>());
        dimensionItems.put(World.NETHER.getValue().toString(), new ArrayList<>());
        dimensionItems.put(World.END.getValue().toString(), new ArrayList<>());
        dimensionItems.put("all_dimensions", new ArrayList<>());
    }
}
