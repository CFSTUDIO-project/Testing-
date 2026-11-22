package dev.cfstudio.vanta;

import net.minecraft.client.Minecraft;

public final class VantaFeatures {
    private static double savedGamma = 1.0;
    private static boolean gammaCaptured;
    private static int savedFov = 70;
    private static boolean fovCaptured;

    private VantaFeatures() { }

    public static void tick(Minecraft client) {
        if (client.player == null) {
            restoreOptions(client);
            return;
        }

        if (FeatureRegistry.isEnabled("auto_sprint")
                && client.options.keyUp.isDown()
                && client.player.onGround()
                && !client.player.isSprinting()) {
            client.player.setSprinting(true);
        }

        if (FeatureRegistry.isEnabled("max_brightness")) {
            if (!gammaCaptured) {
                savedGamma = client.options.gamma().get();
                gammaCaptured = true;
            }
            client.options.gamma().set(1.0);
        } else if (gammaCaptured) {
            client.options.gamma().set(savedGamma);
            gammaCaptured = false;
        }

        if (FeatureRegistry.isEnabled("zoom")) {
            if (!fovCaptured) {
                savedFov = client.options.fov().get();
                fovCaptured = true;
            }
            client.options.fov().set(Math.min(savedFov, 30));
        } else if (fovCaptured) {
            client.options.fov().set(savedFov);
            fovCaptured = false;
        }
    }

    private static void restoreOptions(Minecraft client) {
        if (gammaCaptured) {
            client.options.gamma().set(savedGamma);
            gammaCaptured = false;
        }
        if (fovCaptured) {
            client.options.fov().set(savedFov);
            fovCaptured = false;
        }
    }
}
