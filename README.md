# === minecraft bit flipper === 
- Implements single event upsets for blocks near player. 
- Press K in game to select mode (off / full / solids only).
- For more information check out https://www.youtube.com/watch?v=aUkhZalh-u4
- Developed for Minecraft version 1.12.2. It will probably crash (or at the very least, be very hard to predict upsets) if you use post-flattening versions. 

# === more details === 
- Uses a rate of 0.000224665 upsets per bit per second as measured during the standard portion of our RAM chip experiment. The full bit-by-bit replay tends to crash my computer so I had to simplify it a bit. Sorry!
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
6. Put radiation-bit-flip-1.0.0.jar in the mods folder.
7. You should now be able to load up a world and play. Press "K" to turn on and select the mode.

# === possible avenues for developement ===
- We can probaly add some form of slider to allow user-adjusted flip rate, and another to extend how much of the world is subject to radiation.
- To help with this, the easured flip rates in the actual experiment were:
  - Low: 2.36689 × 10⁻⁶ flips/bit/s (warm up, takes about 10s)
  - Medium: 2.24665 × 10⁻⁴ flips/bit/s upsets per bit per second (steady state, the one used for the existing mod)
  - High: 1.67743 × 10⁻² flips/bit/s (chip failing, takes about 90s to get here)
  - Fun fact! The radiation damadge is entirely random both in type (i.e. 0->1 is as likely as 1->0) and position (no bit within a byte, or byte within the chip is any more or less likely). I was kinda hoping it would be more interesting and I'd be able to publish a paper on it, alas no.
- In addition to the pretty cheat sheet, I also made an interactive one to help with the route plan (click one cell and then its bit-flip neighbors are highlighted, like in the post-speedrun section of the video). We should probably add something similar into the mod but I didn't have the time.

# == the source code ==
- Located in RadiationBitFlipMod folder
- Written in java, should be easy enough to modify if you want to.
- Use Java 8. On Windows, run gradlew.bat build. On macOS or Linux, run ./gradlew build. The distributable JAR appears in build/libs/radiation-bit-flip-1.0.0.jar. The gradle/wrapper files are included so a separate Gradle installation is not required.
