package com.github.radiationbitflip;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class ModeScreen extends GuiScreen {
    private static int selectedMode = RadiationBitFlipMod.MODE_SOLIDS_ONLY;
    private static int selectedRate = RadiationBitFlipMod.RATE_MEDIUM;
    private static boolean pendingSettings;
    private final GuiScreen parent;

    ModeScreen() {
        this(null);
    }

    ModeScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int buttonWidth = 120;
        int gap = 6;
        int left = (width - (3 * buttonWidth + 2 * gap)) / 2;
        int y = height / 2 - 25;
        buttonList.add(new GuiButton(0, left, y, buttonWidth, 20, "[off]"));
        buttonList.add(new GuiButton(1, left + buttonWidth + gap, y, buttonWidth, 20, "[on - full]"));
        buttonList.add(new GuiButton(2, left + 2 * (buttonWidth + gap), y,
                buttonWidth, 20, "[on - solids only]"));

        int rateWidth = 88;
        left = (width - (4 * rateWidth + 3 * gap)) / 2;
        y += 48;
        buttonList.add(new GuiButton(10, left, y, rateWidth, 20, "[low]"));
        buttonList.add(new GuiButton(11, left + rateWidth + gap, y, rateWidth, 20, "[medium]"));
        buttonList.add(new GuiButton(12, left + 2 * (rateWidth + gap), y, rateWidth, 20, "[high]"));
        buttonList.add(new GuiButton(13, left + 3 * (rateWidth + gap), y, rateWidth, 20, "[replay]"));
        updateButtons();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id >= 0 && button.id <= 2) selectedMode = button.id;
        if (button.id >= 10 && button.id <= 13) selectedRate = button.id - 10;
        if (selectedRate == RadiationBitFlipMod.RATE_REPLAY && button.id == 13) {
            selectedMode = RadiationBitFlipMod.MODE_FULL;
        }
        pendingSettings = true;
        sendSettings();
        updateButtons();
    }

    private void updateButtons() {
        for (GuiButton button : buttonList) {
            if (button.id >= 0 && button.id <= 2) button.enabled = button.id != selectedMode;
            if (button.id >= 10 && button.id <= 13) button.enabled = button.id - 10 != selectedRate;
        }
    }

    static void sendSettings() {
        if (!pendingSettings) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player == null || minecraft.getConnection() == null) return;
        RadiationBitFlipMod.network.sendToServer(new ModeMessage(selectedMode, selectedRate));
        pendingSettings = false;
    }

    static void resetSelection() {
        selectedMode = RadiationBitFlipMod.MODE_SOLIDS_ONLY;
        selectedRate = RadiationBitFlipMod.RATE_MEDIUM;
        pendingSettings = false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_K) {
            mc.displayGuiScreen(parent);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "Radiation Bit Flip Mod", width / 2, height / 2 - 72, 0xFFFFFF);
        drawCenteredString(fontRenderer, "type", width / 2, height / 2 - 39, 0xA0A0A0);
        drawCenteredString(fontRenderer, "flip rate", width / 2, height / 2 + 9, 0xA0A0A0);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
