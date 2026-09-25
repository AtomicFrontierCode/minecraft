# Minecraft Radiation Bit-Flip Mod

[![Download Mod](https://img.shields.io/badge/Download-Mod%20.jar-DBB13B?style=for-the-badge&logo=java&logoColor=black)](https://github.com/AtomicFrontierCode/minecraft/raw/main/radiation-bit-flip-2.0.0.jar)

[Interactive Table of Minecraft](https://atomicfrontiercode.github.io/minecraft/)

# === minecraft bit flipper === 
- Implements single event upsets for blocks near player. 
- Press K in game to select mode (off / full / solids only) and rate (low / medium / high / replay). The replay mode is roughly based on the actual bit flip rate during the run.
- For more information check out [https://www.youtube.com/watch?v=Kd5-1IXqiDQ](https://www.youtube.com/watch?v=Kd5-1IXqiDQ)
- Use the interactive site [https://atomicfrontiercode.github.io/minecraft/](https://atomicfrontiercode.github.io/minecraft/) to plan bit flips.
- Developed for Minecraft version 1.12.2. It will probably crash (or at the very least, be very hard to predict upsets) if you use post-flattening versions. 

# === more details === 
- The measured flip rates in the actual experiment were:
  - Low: 2.36689 × 10⁻⁶ flips/bit/s (warm up, takes about 10s)
  - Medium: 2.24665 × 10⁻⁴ flips/bit/s upsets per bit per second (steady state, dafault for the mod)
  - High: 1.67743 × 10⁻² flips/bit/s (chip failing, takes about 90s to get here)
  - Radiation damadge is entirely random both in type (i.e. 0->1 is as likely as 1->0) and position (no bits within a byte, or bytes within the chip are any more or less likely to flip). I was kinda hoping it would be more interesting and I'd be able to publish a paper on it, alas no.

- Flips one of the eight ID bits of nearby blocks. Metadata (e.g., block orientation or variant) is retained. Invalid target IDs or incompatible metadata leave the original block unchanged.
- Each player affects a 4 × 4 × 4 group of 16 × 16 × 16 blocks (i.e., 64 sub-chunks). 
- The full mode can turn air into blocks (which is really annoying); solids-only mode attempts changes only on blocks whose material is solid.
- In theory this works for everyone on your server, but as I have no friends I've been unable to verify.

# === installation ===
1. To begin, you'll need the Minecraft Java Edition (i.e., the launcher on windows) and Java (https://www.java.com/en/download/manual.jsp).
2. Install Minecraft 1.12.2 with Forge 14.23.5.2859 (or another Forge version that works with 1.12.2). I got mine from (https://files.minecraftforge.net/net/minecraftforge/forge/index_1.12.2.html)
3. You should now be able to install forge by right clicking on the download at "...\Downloads\forge-1.12.2-14.23.5.2859-installer.jar" and opening it with Java. Click through to install client (the default).
4. Now, open the Minecraft launcher, it should have a new installaton called "forge" with a subheading along the lines of "1.12.2-forge-14.23.5.2859".
5. Hit installations tab then click the "open installations folder" icon (this takes me to C:\Users\James\AppData\Roaming\.minecraft).
6. Put radiation-bit-flip-2.0.0.jar in the mods folder.
7. You should now be able to load up a world and play. Press "K" to turn on and select the mode.

# == the source code ==
- Located in RadiationBitFlipMod folder
- Written in java, should be easy enough to modify if you want to.
- Use Java 8. On Windows, run gradlew.bat build. On macOS or Linux, run ./gradlew build. The distributable JAR appears in build/libs/radiation-bit-flip-1.0.0.jar. The gradle/wrapper files are included so a separate Gradle installation is not required.
