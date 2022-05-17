package com.github.levoment.thatsillegal;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BannedItemsHandler {
    public void initializeBannedItems(MinecraftServer minecraftServer) {
        // Get the current configuration
        ThatsIllegalModConfig config = AutoConfig.getConfigHolder(ThatsIllegalModConfig.class).getConfig();
        // Get the map of dimensions
        Map<String, List<String>> mapOfDimensions = config.DimensionBannedItems.CraftingBannedIngredients;

        // For each entry in the map
        for (Map.Entry<String, List<String>> entry : mapOfDimensions.entrySet()) {
            // Create a list to put the banned items for the dimension in the entry key
            List<Item> listOfItemsForTheDimension = new ArrayList<>();
            // For each item identifier for the item to be banned in the dimension
            for (String itemIdentifier : entry.getValue()) {
                // Get the item to add to the list of banned items for the dimension
                Item itemToAdd = Registry.ITEM.get(new Identifier(itemIdentifier));
                // If the item is an empty item
                if (itemToAdd.getDefaultStack().isEmpty()) {
                    // Show a message indicating that the item is not a valid item
                    ThatsIllegalMod.LOGGER.warn("The given item in configuration: " + itemIdentifier + " on " + entry.getKey() + " could not be identified as a valid item in the game.");
                } else {
                    // Add the item to the list of items that are banned on the dimension
                    listOfItemsForTheDimension.add(itemToAdd);
                }
            }
            // Add the list of items that are banned on the dimension on the map for the current dimension identifier in the entry
            ThatsIllegalMod.MAP_OF_BANNED_ITEMS.put(entry.getKey(), listOfItemsForTheDimension);
        }
    }

    public void initializeFullyBannedItems(MinecraftServer minecraftServer) {
        // Get the current configuration
        ThatsIllegalModConfig config = AutoConfig.getConfigHolder(ThatsIllegalModConfig.class).getConfig();
        // Get the map of dimensions
        Map<String, List<String>> mapOfDimensions = config.DimensionBannedItems.FullyBannedItems;

        // For each entry in the map
        for (Map.Entry<String, List<String>> entry : mapOfDimensions.entrySet()) {
            // Create a list to put the banned items for the dimension in the entry key
            List<Item> listOfItemsForTheDimension = new ArrayList<>();
            // For each item identifier for the item to be banned in the dimension
            for (String itemIdentifier : entry.getValue()) {
                // Get the item to add to the list of banned items for the dimension
                Item itemToAdd = Registry.ITEM.get(new Identifier(itemIdentifier));
                // If the item is an empty item
                if (itemToAdd.getDefaultStack().isEmpty()) {
                    // Show a message indicating that the item is not a valid item
                    ThatsIllegalMod.LOGGER.warn("The given item in configuration: " + itemIdentifier + " on " + entry.getKey() + " could not be identified as a valid item in the game.");
                } else {
                    // Add the item to the list of items that are banned on the dimension
                    listOfItemsForTheDimension.add(itemToAdd);
                }
            }
            // Add the list of items that are banned on the dimension on the map for the current dimension identifier in the entry
            ThatsIllegalMod.MAP_OF_FULLY_BANNED_ITEMS.put(entry.getKey(), listOfItemsForTheDimension);
        }
    }
}
