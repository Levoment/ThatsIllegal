package com.github.levoment.thatsillegal;

import net.minecraft.client.MinecraftClient;

public class DebugHelper {
    public static boolean releaseCursor() {
        MinecraftClient.getInstance().mouse.unlockCursor();
        return true;
    }
}
