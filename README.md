# Vanta Client

A client-side Fabric utility/visual client for **Minecraft Java 1.21.11**.

## Features

- Animated click GUI (Right Shift) with Visual, World, Movement and HUD categories
- Player ESP and hostile-mob ESP
- Chest / storage ESP
- Ore Finder for ore blocks in chunks the client has actually received
- Auto Sprint, Zoom and Max Brightness
- Coordinate, FPS and active-module HUD elements
- Separate keybinds for quick toggles

## Compatibility

- Minecraft Java 1.21.11
- Fabric Loader 0.18.1+
- Fabric API 0.141.6+1.21.11
- Java 21

## Important limitations

This is a **client-only** mod. It does not change server state, send privileged commands, or bypass server protections. ESP can only render entities and block states that the server has sent to the client. Ore Finder scans nearby loaded client chunks; servers using anti-xray or obfuscated chunk data can hide or replace ore information before it reaches the client, so this is not a guarantee of seeing hidden ores. Use it only where client-side visual mods are allowed.

## Build

The repository includes a GitHub Actions workflow. Open **Actions → Build Vanta Client → Run workflow** or push a commit. Download the `Vanta-Client-1.21.11` artifact from the completed run.

Locally, with JDK 21 and Gradle 9.2.1 installed:

```bash
gradle build
```

The remapped mod jar is written to `build/libs/`.

## Keybinds

| Action | Default key |
|---|---|
| Open GUI | Right Shift |
| Player ESP | F6 |
| Chest ESP | F7 |
| Ore Finder | F8 |
| Mob ESP | F9 |
| Coordinates HUD | F10 |
| Auto Sprint | V |
| Zoom | C |

All modules can also be toggled from the GUI.

## License

MIT
