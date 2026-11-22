package dev.cfstudio.vanta;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class VantaHud {
    private VantaHud() { }

    public static void register() {
        HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath(VantaClient.MOD_ID, "overlay"),
                VantaHud::render
        );
    }

    private static void render(GuiGraphics graphics, net.minecraft.client.DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.options.hideGui) {
            return;
        }

        int x = 10;
        int y = 10;
        int panelWidth = 168;
        int rows = 1;
        if (FeatureRegistry.isEnabled("coordinates")) rows++;
        if (FeatureRegistry.isEnabled("fps_hud")) rows++;
        if (rows == 1) return;

        int panelHeight = 28 + (rows - 1) * 13;
        graphics.fillGradient(x, y, x + panelWidth, y + panelHeight, 0xD9081422, 0xD9161028);
        graphics.fillGradient(x, y, x + 2, y + panelHeight, 0xFF55D7FF, 0xFFB58AFF);
        graphics.drawString(client.font, "V A N T A", x + 10, y + 6, 0xFF75E8FF, false);

        int lineY = y + 19;
        if (FeatureRegistry.isEnabled("coordinates")) {
            String coordinates = String.format(
                    "XYZ  %d  %d  %d",
                    (int) Math.floor(client.player.getX()),
                    (int) Math.floor(client.player.getY()),
                    (int) Math.floor(client.player.getZ())
            );
            graphics.drawString(client.font, Component.literal(coordinates), x + 10, lineY, 0xFFE8F3FF, false);
            lineY += 13;
        }
        if (FeatureRegistry.isEnabled("fps_hud")) {
            graphics.drawString(client.font, "FPS  " + client.getFps(), x + 10, lineY, 0xFFB8C8FF, false);
        }

        if (FeatureRegistry.isEnabled("module_hud")) {
            int right = client.getWindow().getGuiScaledWidth() - 10;
            int top = 10;
            for (FeatureRegistry.Module module : FeatureRegistry.enabledModules()) {
                if (module.id().equals("module_hud")) continue;
                int width = client.font.width(module.title()) + 18;
                graphics.fillGradient(right - width, top, right, top + 15, 0xCC111827, 0xCC20142B);
                graphics.fill(right - 2, top, right, top + 15, module.accent());
                graphics.drawString(client.font, module.title(), right - width + 7, top + 4, 0xFFF0F4FF, false);
                top += 18;
            }
        }
    }
}
