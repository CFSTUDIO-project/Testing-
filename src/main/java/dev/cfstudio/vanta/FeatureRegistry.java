package dev.cfstudio.vanta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class FeatureRegistry {
    public enum Category {
        VISUALS("Visuals"),
        WORLD("World"),
        MOVEMENT("Movement"),
        HUD("HUD");

        private final String title;

        Category(String title) {
            this.title = title;
        }

        public String title() {
            return title;
        }
    }

    public record Module(
            String id,
            String title,
            String description,
            Category category,
            int accent,
            String hotkey
    ) { }

    private static final List<Module> MODULES = List.of(
            new Module("player_esp", "Player ESP", "Outline other players in loaded entity range.", Category.VISUALS, 0xFF55D7FF, "F6"),
            new Module("mob_esp", "Hostile ESP", "Outline hostile mobs that are currently loaded.", Category.VISUALS, 0xFFFF795F, "F9"),
            new Module("chest_esp", "Chest ESP", "Mark visible chest and trapped-chest block data.", Category.VISUALS, 0xFFFFC66D, "F7"),
            new Module("storage_esp", "Storage ESP", "Mark barrels, ender chests and shulker boxes.", Category.VISUALS, 0xFFC69BFF, "—"),
            new Module("ore_finder", "Ore Finder", "Mark ores in nearby chunks received by the client.", Category.WORLD, 0xFF58E6C5, "F8"),
            new Module("auto_sprint", "Auto Sprint", "Sprint while moving forward on the ground.", Category.MOVEMENT, 0xFF72E69A, "V"),
            new Module("zoom", "Zoom", "Temporarily lower the field of view.", Category.MOVEMENT, 0xFF83A9FF, "C"),
            new Module("max_brightness", "Max Brightness", "Set Minecraft gamma to its allowed maximum.", Category.VISUALS, 0xFFFFE28A, "—"),
            new Module("coordinates", "Coordinates HUD", "Show your XYZ position in a compact overlay.", Category.HUD, 0xFF62D9FF, "F10"),
            new Module("fps_hud", "FPS Counter", "Show the current client frame rate.", Category.HUD, 0xFF9DAEFF, "—"),
            new Module("module_hud", "Active Modules", "Show enabled Vanta modules on screen.", Category.HUD, 0xFFD59BFF, "—")
    );

    private static final Map<String, Boolean> STATES = new LinkedHashMap<>();

    static {
        for (Module module : MODULES) {
            STATES.put(module.id(), false);
        }
        STATES.put("coordinates", true);
    }

    private FeatureRegistry() { }

    public static List<Module> all() {
        return MODULES;
    }

    public static List<Module> inCategory(Category category) {
        List<Module> result = new ArrayList<>();
        for (Module module : MODULES) {
            if (module.category() == category) {
                result.add(module);
            }
        }
        return result;
    }

    public static boolean isEnabled(String id) {
        return STATES.getOrDefault(id, false);
    }

    public static void toggle(String id) {
        if (STATES.containsKey(id)) {
            STATES.put(id, !STATES.get(id));
        }
    }

    public static void setEnabled(String id, boolean enabled) {
        if (STATES.containsKey(id)) {
            STATES.put(id, enabled);
        }
    }

    public static List<Module> enabledModules() {
        List<Module> result = new ArrayList<>();
        for (Module module : MODULES) {
            if (isEnabled(module.id())) {
                result.add(module);
            }
        }
        return result;
    }

    public static int enabledCount() {
        return enabledModules().size();
    }
}
