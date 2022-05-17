package com.github.levoment.thatsillegal.mixin;

import com.github.levoment.thatsillegal.ThatsIllegalMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z",
    at = @At("HEAD"), cancellable = true)
    public void insertStackCallback(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        PlayerInventory playerInventory = (PlayerInventory)((Object)this);
        PlayerEntity playerEntity = playerInventory.player;
        if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity) {
            if (checkForBannedItem(stack, serverPlayerEntity)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "setStack(ILnet/minecraft/item/ItemStack;)V",
            at = @At("HEAD"), cancellable = true)
    public void addStackCallback(int slot, ItemStack stack, CallbackInfo ci) {
        PlayerInventory playerInventory = (PlayerInventory)((Object)this);
        PlayerEntity playerEntity = playerInventory.player;
        if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity) {
            if (checkForBannedItem(stack, serverPlayerEntity)) ci.cancel();
        }
    }

    private boolean checkForBannedItem(ItemStack stack, ServerPlayerEntity serverPlayerEntity) {
        // Get the current dimension identifier
        Identifier dimensionIdentifier = serverPlayerEntity.world.getRegistryKey().getValue();
        // Get the list containing the banned items for the dimension the player is in
        List<Item> bannedItemsInTheCurrentDimension = ThatsIllegalMod.MAP_OF_FULLY_BANNED_ITEMS.get(dimensionIdentifier.getNamespace() + ":" + dimensionIdentifier.getPath());
        // Get a list of banned items for all dimensions
        List<Item> listOfAllDimensionsBannedItems = ThatsIllegalMod.MAP_OF_FULLY_BANNED_ITEMS.get("all_dimensions");

        // If the list of items for the current dimension and all_dimensions is not null
        if (bannedItemsInTheCurrentDimension != null && listOfAllDimensionsBannedItems != null) {
            // Check if the item is contained in the lists
            if (listOfAllDimensionsBannedItems.contains(stack.getItem()) || bannedItemsInTheCurrentDimension.contains(stack.getItem())) {
                // Update the messages
                ThatsIllegalMod.UpdateConfig();
                // Create the text to display
                Text textToDisplay = new LiteralText(ThatsIllegalMod.ScreenMessage).formatted(Formatting.GOLD);
                // Create the messages for the chat
                String secondText = new TranslatableText(ThatsIllegalMod.ChatMessageSegment1).getString();
                String thirdText = new TranslatableText(ThatsIllegalMod.ChatMessageSegment3).getString();

                // If the configuration is set to display a screen message
                if (ThatsIllegalMod.DisplayScreenMessage && ThatsIllegalMod.fullyBannedItemMessageCount == 0) {
                    // Create a packet to display the message
                    TitleS2CPacket titleS2CPacket = new TitleS2CPacket(textToDisplay);
                    // Send the message to the player client
                    serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket);
                }

                // If the configuration is set to display a chat message
                if (ThatsIllegalMod.DisplayChatMessage && ThatsIllegalMod.fullyBannedItemMessageCount == 0) {
                    // Create the chat message
                    Text chatMessage = new LiteralText("[§5That's Illegal Mod§r] " + "§6"+ secondText + " §c" + stack.getItem().getName().getString() + " §6" + thirdText).formatted(Formatting.BOLD);
                    // Send the message to the player chat
                    serverPlayerEntity.sendSystemMessage(chatMessage, Util.NIL_UUID);
                }
                ThatsIllegalMod.fullyBannedItemMessageCount++;
                if (ThatsIllegalMod.fullyBannedItemMessageCount == 60) ThatsIllegalMod.fullyBannedItemMessageCount = 0;
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
