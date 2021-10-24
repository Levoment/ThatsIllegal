package com.github.levoment.thatsillegal;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.item.Item;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.*;

public class ThatsIllegalMod implements ModInitializer, DedicatedServerModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger("modid");

	// Map of banned items. It contains the dimension identifier string and a list of items for it
	public static Map<String, List<Item>> MAP_OF_BANNED_ITEMS = new HashMap<>();

	// The MODID
	public static final String MOD_ID = "thatsillegal";

	// Create an object to handle banned items
	public static BannedItemsHandler bannedItemsHandler = new BannedItemsHandler();

	// Create the variables for the mod configuration
	public static String ScreenMessage = "That's Illegal";
	public static String ChatMessageSegment1 = "The item";
	public static String ChatMessageSegment2 = "cannot be used to craft in this dimension";
	public static boolean DisplayScreenMessage = true;
	public static boolean DisplayChatMessage = true;

	@Override
	public void onInitialize() {
		// Initialize the configuration on the client
		AutoConfig.register(ThatsIllegalModConfig.class, GsonConfigSerializer::new);
		// Register a method to be run when the server starts to create the map of banned items
		ServerLifecycleEvents.SERVER_STARTED.register(bannedItemsHandler::initializeBannedItems);

		// Register the command to teleport to the SkyBreezes dimension
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
					dispatcher.register(CommandManager.literal("thatsillegal_banned_items").executes(ThatsIllegalMod.this::executeBannedItemsCommand));
				}
		);
	}


	@Override
	public void onInitializeServer() {
		// Initialize the configuration on the dedicated server
		AutoConfig.register(ThatsIllegalModConfig.class, GsonConfigSerializer::new);
		// Register a method to be run when the server starts to create the map of banned items
		ServerLifecycleEvents.SERVER_STARTED.register(bannedItemsHandler::initializeBannedItems);
	}

	public static void UpdateConfig() {
		// Get the mod configuration
		ThatsIllegalModConfig config = AutoConfig.getConfigHolder(ThatsIllegalModConfig.class).getConfig();
		// Update the messages and values for the screen message and chat options and text
		ThatsIllegalMod.ScreenMessage = config.ScreenMessage;
		ThatsIllegalMod.ChatMessageSegment1 = config.ChatMessageSegment1;
		ThatsIllegalMod.ChatMessageSegment2 = config.ChatMessageSegment2;
		ThatsIllegalMod.DisplayScreenMessage = config.DisplayScreenMessage;
		ThatsIllegalMod.DisplayChatMessage = config.DisplayChatMessage;
	}

	private int executeBannedItemsCommand(CommandContext<ServerCommandSource> objectCommandContext) throws CommandSyntaxException {
		// Get the player that executed the command
		ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) objectCommandContext.getSource().getEntity();
		String messageString = "";
		// Get the mod configuration
		ThatsIllegalModConfig config = AutoConfig.getConfigHolder(ThatsIllegalModConfig.class).getConfig();
		// Get the map for banned items
		Map<String, List<String>> mapOfBannedItemsInDimensions = config.DimensionBannedItems.dimensionItems;
		for (Map.Entry<String, List<String>> entry : mapOfBannedItemsInDimensions.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				messageString += "Items banned in §6" + entry.getKey() + "§r: ";
				for (String item : entry.getValue()) {
					Item bannedItem = Registry.ITEM.get(new Identifier(item)).asItem();
					if (!bannedItem.getDefaultStack().isEmpty()) {
						messageString += "§c" + bannedItem.getName().getString() + ", ";
					}
				}
				messageString = messageString.trim();
				messageString = messageString.substring(0, messageString.length() - 1);
				messageString += "§r. ";
			}
		}
		// Create the chat message
		Text chatMessage = new LiteralText("[§5That's Illegal Mod§r] " + messageString);
		// Send the message to the player chat
		serverPlayerEntity.sendSystemMessage(chatMessage, Util.NIL_UUID);
		return 1;
	}
}
