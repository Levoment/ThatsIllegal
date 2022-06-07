package com.github.levoment.thatsillegal.mixin;

import com.github.levoment.thatsillegal.ThatsIllegalMod;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CraftingInventory.class)
public class CraftingInventoryMixin {
    @Shadow @Final
    private ScreenHandler handler;

    @Inject(method = "setStack(ILnet/minecraft/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    public void setStackCallback(int slot, ItemStack stack, CallbackInfo callbackInfo) {
        // If the screen handler is an instance of PlayerScreenHandler
        if (this.handler instanceof PlayerScreenHandler playerScreenHandler) {
            // If the PlayerScreenHandler is on the server
            if (((PlayerScreenHandlerPlayerAccessor) playerScreenHandler).getOwner() instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) ((PlayerScreenHandlerPlayerAccessor) this.handler).getOwner();
                if (CheckForBannedItem(stack, serverPlayerEntity)) callbackInfo.cancel();
            }
        }
        if (this.handler instanceof CraftingScreenHandler craftingScreenHandler) {
            // If the PlayerScreenHandler is on the server
            if ((((CraftingScreenHandlerPlayerAccessor) craftingScreenHandler).getPlayer() instanceof ServerPlayerEntity)) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) ((CraftingScreenHandlerPlayerAccessor) this.handler).getPlayer();
                if (CheckForBannedItem(stack, serverPlayerEntity)) callbackInfo.cancel();
            }
        }
    }

    private boolean CheckForBannedItem(ItemStack stack, ServerPlayerEntity serverPlayerEntity) {
        // Get the current dimension identifier
        Identifier dimensionIdentifier = serverPlayerEntity.world.getRegistryKey().getValue();
        // Get the list containing the banned items for the dimension the player is in
        List<Item> bannedItemsInTheCurrentDimension = ThatsIllegalMod.MAP_OF_BANNED_ITEMS.get(dimensionIdentifier.getNamespace() + ":" + dimensionIdentifier.getPath());
        // Get a list of banned items for all dimensions
        List<Item> listOfAllDimensionsBannedItems = ThatsIllegalMod.MAP_OF_BANNED_ITEMS.get("all_dimensions");

        // If the list of items for the current dimension and all_dimensions is not null
        if (bannedItemsInTheCurrentDimension != null && listOfAllDimensionsBannedItems != null) {
            // Check if the item is contained in the lists
            if (listOfAllDimensionsBannedItems.contains(stack.getItem()) || bannedItemsInTheCurrentDimension.contains(stack.getItem())) {
                // Update the messages
                ThatsIllegalMod.UpdateConfig();
                // Create the text to display
                Text textToDisplay = Text.of(ThatsIllegalMod.ScreenMessage).getWithStyle(Style.EMPTY.withFormatting(Formatting.GOLD)).get(0);
                // Create the messages for the chat
                String secondText = Text.translatable(ThatsIllegalMod.ChatMessageSegment1).getString();
                String thirdText = Text.translatable(ThatsIllegalMod.ChatMessageSegment2).getString();

                // If the configuration is set to display a screen message
                if (ThatsIllegalMod.DisplayScreenMessage) {
                    // Create a packet to display the message
                    TitleS2CPacket titleS2CPacket = new TitleS2CPacket(textToDisplay);
                    // Send the message to the player client
                    serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket);
                }

                // If the configuration is set to display a chat message
                if (ThatsIllegalMod.DisplayChatMessage) {
                    // Create the chat message
                    Text chatMessage = Text.of("[§5That's Illegal Mod§r] " + "§6"+ secondText + " §c" + stack.getItem().getName().getString() + " §6" + thirdText).getWithStyle(Style.EMPTY.withFormatting(Formatting.BOLD)).get(0);
                    // Send the message to the player chat
                    serverPlayerEntity.sendMessage(chatMessage, MessageType.SYSTEM);
                }
                // Drop the item randomly
                serverPlayerEntity.dropItem(stack, true, true);
                return true;
            }
        } else {
            ThatsIllegalMod.LOGGER.error("It seems that the json containing the banned items in each dimension is malformed " +
                    "or it's missing either the current dimension identifier or the 'all_dimensions' identifier. " +
                    "To correct this, make sure the current dimension identifier exists or is written properly on the json file and that the `all_dimensions` key exists as well in the json. " +
                    "If those are correct, check if the json is a valid json. If the json is a valid json, then check on the repository " +
                    "of this mod that the json content for the configuration matches the one in this mod.");
            return false;
        }
        return false;
    }
}
