package dev.cfstudio.vanta;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public final class VantaScanner {
    public enum MarkerKind {
        ORE,
        CHEST,
        STORAGE
    }

    public record Marker(BlockPos position, MarkerKind kind, int color) { }

    private static List<Marker> markers = List.of();
    private static int ticks;

    private VantaScanner() { }

    public static List<Marker> markers() {
        return markers;
    }

    public static void tick(Minecraft client) {
        if (client.level == null || client.player == null) {
            markers = List.of();
            ticks = 0;
            return;
        }

        boolean scanOres = FeatureRegistry.isEnabled("ore_finder");
        boolean scanChests = FeatureRegistry.isEnabled("chest_esp");
        boolean scanStorage = FeatureRegistry.isEnabled("storage_esp");

        if (!scanOres && !scanChests && !scanStorage) {
            markers = List.of();
            ticks = 0;
            return;
        }

        if (++ticks < 20) {
            return;
        }
        ticks = 0;

        List<Marker> found = new ArrayList<>();
        BlockPos center = client.player.blockPosition();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        // A small bounded scan avoids walking unloaded chunks or doing work every frame.
        for (int x = -12; x <= 12; x++) {
            for (int y = -8; y <= 8; y++) {
                for (int z = -12; z <= 12; z++) {
                    cursor.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    if (!client.level.hasChunkAt(cursor)) {
                        continue;
                    }

                    BlockState state = client.level.getBlockState(cursor);
                    if (scanOres && isOre(state)) {
                        found.add(new Marker(cursor.immutable(), MarkerKind.ORE, 0xFF56E8CF));
                    } else if (scanChests && (state.is(Blocks.CHEST) || state.is(Blocks.TRAPPED_CHEST))) {
                        found.add(new Marker(cursor.immutable(), MarkerKind.CHEST, 0xFFFFC66D));
                    } else if (scanStorage && isStorage(state)) {
                        found.add(new Marker(cursor.immutable(), MarkerKind.STORAGE, 0xFFC69BFF));
                    }
                }
            }
        }

        markers = List.copyOf(found);
    }

    private static boolean isOre(BlockState state) {
        return state.is(Blocks.COAL_ORE)
                || state.is(Blocks.DEEPSLATE_COAL_ORE)
                || state.is(Blocks.COPPER_ORE)
                || state.is(Blocks.DEEPSLATE_COPPER_ORE)
                || state.is(Blocks.IRON_ORE)
                || state.is(Blocks.DEEPSLATE_IRON_ORE)
                || state.is(Blocks.GOLD_ORE)
                || state.is(Blocks.DEEPSLATE_GOLD_ORE)
                || state.is(Blocks.REDSTONE_ORE)
                || state.is(Blocks.DEEPSLATE_REDSTONE_ORE)
                || state.is(Blocks.LAPIS_ORE)
                || state.is(Blocks.DEEPSLATE_LAPIS_ORE)
                || state.is(Blocks.DIAMOND_ORE)
                || state.is(Blocks.DEEPSLATE_DIAMOND_ORE)
                || state.is(Blocks.EMERALD_ORE)
                || state.is(Blocks.DEEPSLATE_EMERALD_ORE)
                || state.is(Blocks.NETHER_GOLD_ORE)
                || state.is(Blocks.NETHER_QUARTZ_ORE)
                || state.is(Blocks.ANCIENT_DEBRIS);
    }

    private static boolean isStorage(BlockState state) {
        return state.is(Blocks.BARREL)
                || state.is(Blocks.ENDER_CHEST)
                || state.getBlock() instanceof ShulkerBoxBlock;
    }
}
