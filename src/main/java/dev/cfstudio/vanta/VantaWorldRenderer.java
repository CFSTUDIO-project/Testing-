package dev.cfstudio.vanta;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class VantaWorldRenderer {
    private static final int[] EDGES = {
            0, 1, 1, 3, 3, 2, 2, 0,
            4, 5, 5, 7, 7, 6, 6, 4,
            0, 4, 1, 5, 2, 6, 3, 7
    };

    private VantaWorldRenderer() { }

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(VantaWorldRenderer::render);
    }

    private static void render(WorldRenderContext context) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null) {
            return;
        }

        boolean players = FeatureRegistry.isEnabled("player_esp");
        boolean mobs = FeatureRegistry.isEnabled("mob_esp");
        boolean blocks = FeatureRegistry.isEnabled("chest_esp")
                || FeatureRegistry.isEnabled("storage_esp")
                || FeatureRegistry.isEnabled("ore_finder");

        if (!players && !mobs && !blocks) {
            return;
        }

        Vec3 camera = client.gameRenderer.getMainCamera().position();
        PoseStack.Pose pose = context.matrices().last();
        VertexConsumer lines = context.consumers().getBuffer(RenderTypes.linesTranslucent());

        if (players || mobs) {
            for (Entity entity : client.level.entitiesForRendering()) {
                if (entity == client.player || !entity.isAlive()
                        || entity.distanceToSqr(client.player) > 4096.0) {
                    continue;
                }

                int color = 0;
                if (players && entity instanceof Player) {
                    color = 0xFF57D8FF;
                } else if (mobs && entity instanceof Mob && entity instanceof Monster) {
                    color = 0xFFFF765F;
                }

                if (color != 0) {
                    AABB relative = entity.getBoundingBox().move(-camera.x, -camera.y, -camera.z);
                    drawBox(lines, pose, relative, color);
                }
            }
        }

        if (blocks) {
            for (VantaScanner.Marker marker : VantaScanner.markers()) {
                boolean enabled = switch (marker.kind()) {
                    case ORE -> FeatureRegistry.isEnabled("ore_finder");
                    case CHEST -> FeatureRegistry.isEnabled("chest_esp");
                    case STORAGE -> FeatureRegistry.isEnabled("storage_esp");
                };

                if (!enabled) {
                    continue;
                }

                AABB relative = new AABB(marker.position()).move(-camera.x, -camera.y, -camera.z);
                drawBox(lines, pose, relative, marker.color());
            }
        }
    }

    private static void drawBox(VertexConsumer consumer, PoseStack.Pose pose, AABB box, int color) {
        float[][] points = {
                {(float) box.minX, (float) box.minY, (float) box.minZ},
                {(float) box.maxX, (float) box.minY, (float) box.minZ},
                {(float) box.minX, (float) box.maxY, (float) box.minZ},
                {(float) box.maxX, (float) box.maxY, (float) box.minZ},
                {(float) box.minX, (float) box.minY, (float) box.maxZ},
                {(float) box.maxX, (float) box.minY, (float) box.maxZ},
                {(float) box.minX, (float) box.maxY, (float) box.maxZ},
                {(float) box.maxX, (float) box.maxY, (float) box.maxZ}
        };

        for (int i = 0; i < EDGES.length; i += 2) {
            float[] a = points[EDGES[i]];
            float[] b = points[EDGES[i + 1]];
            consumer.addVertex(pose, a[0], a[1], a[2]).setColor(color);
            consumer.addVertex(pose, b[0], b[1], b[2]).setColor(color);
        }
    }
}
