package dev.cfstudio.vanta;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;

public final class VantaClient implements ClientModInitializer {
    public static final String MOD_ID = "vanta";

    private static KeyMapping openMenuKey;
    private static final Map<String, KeyMapping> MODULE_KEYS = new LinkedHashMap<>();

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "main")
        );

        openMenuKey = register("key.vanta.open_menu", GLFW.GLFW_KEY_RIGHT_SHIFT, category);
        bind("player_esp", "key.vanta.player_esp", GLFW.GLFW_KEY_F6, category);
        bind("chest_esp", "key.vanta.chest_esp", GLFW.GLFW_KEY_F7, category);
        bind("ore_finder", "key.vanta.ore_finder", GLFW.GLFW_KEY_F8, category);
        bind("mob_esp", "key.vanta.mob_esp", GLFW.GLFW_KEY_F9, category);
        bind("coordinates", "key.vanta.coordinates", GLFW.GLFW_KEY_F10, category);
        bind("auto_sprint", "key.vanta.auto_sprint", GLFW.GLFW_KEY_V, category);
        bind("zoom", "key.vanta.zoom", GLFW.GLFW_KEY_C, category);

        VantaWorldRenderer.register();
        VantaHud.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.consumeClick()) {
                if (client.screen instanceof VantaScreen screen) {
                    screen.beginClose();
                } else {
                    client.setScreen(new VantaScreen());
                }
            }

            for (Map.Entry<String, KeyMapping> entry : MODULE_KEYS.entrySet()) {
                while (entry.getValue().consumeClick()) {
                    FeatureRegistry.toggle(entry.getKey());
                }
            }

            VantaScanner.tick(client);
            VantaFeatures.tick(client);
        });
    }

    private static void bind(String id, String translationKey, int key, KeyMapping.Category category) {
        MODULE_KEYS.put(id, register(translationKey, key, category));
    }

    private static KeyMapping register(String translationKey, int key, KeyMapping.Category category) {
        return KeyBindingHelper.registerKeyBinding(
                new KeyMapping(translationKey, InputConstants.Type.KEYSYM, key, category)
        );
    }
}
