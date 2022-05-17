package com.github.levoment.thatsillegal;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = ThatsIllegalMod.MOD_ID)
public class ThatsIllegalModConfig implements ConfigData {
    @ConfigEntry.Gui.Excluded
    DimensionBannedItems DimensionBannedItems = new DimensionBannedItems();

    String ScreenMessage = "That's Illegal";
    String ChatMessageSegment1 = "The item";
    String ChatMessageSegment2 = "cannot be used to craft in this dimension";
    String ChatMessageSegment3 = "cannot be used in this dimension";

    boolean DisplayScreenMessage = true;
    boolean DisplayChatMessage = true;
}
