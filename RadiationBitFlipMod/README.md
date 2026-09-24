# Radiation Bit Flip Mod for Minecraft 1.12.2

Implements single event upsets for blocks near player. Press K in game to select mode (off / full / solids only). For more information check out the Atomic Frontier video.

## More details

- Uses a measured rate of 0.000224665 upsets per bit per second as measured during the standard portion of our RAM chip experiment. The full bit-by-bit replay tends to crash my computer so I had to simplify it a bit. Sorry!
- Flips one of the eight ID bits of nearby blocks. Metadata (e.g., block orientation or variant) is retained. Invalid target IDs or incompatible metadata leave the original block unchanged.
- Each player affects a 4 × 4 × 4 group of 16 × 16 × 16 block sections. Relative section offsets on each axis are -1, 0, +1, +2 from the section containing the player. At world height limits, only valid sections are considered.
- The full mode can turn air into blocks (which is really annoying); solids-only mode attempts changes only on blocks whose material is solid.
- In theory this works for everyone on your server, but as I have no friends I've been unable to verify.

## Install

Install Minecraft 1.12.2 with Forge 14.23.5.2859 or another compatible 1.12.2 Forge version. Put `radiation-bit-flip-1.0.0.jar` in the `mods` folder. On a dedicated server, install the same JAR on both the server and clients that use the menu.

## Build from source

Use Java 8. On Windows, run `gradlew.bat build`. On macOS or Linux, run `./gradlew build`. The distributable JAR appears in `build/libs/radiation-bit-flip-1.0.0.jar`. The `gradle/wrapper` files are included so a separate Gradle installation is not required.