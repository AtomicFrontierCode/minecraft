package com.github.radiationbitflip;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModeScreen extends GuiScreen {
    @Override
    public void initGui() {
        buttonList.clear();
        int buttonWidth = 120;
        int gap = 6;
        int left = (width - (3 * buttonWidth + 2 * gap)) / 2;
        int y = height / 2 - 10;
        buttonList.add(new GuiButton(0, left, y, buttonWidth, 20, "[off]"));
        buttonList.add(new GuiButton(1, left + buttonWidth + gap, y, buttonWidth, 20, "[on - full]"));
        buttonList.add(new GuiButton(2, left + 2 * (buttonWidth + gap), y,
                buttonWidth, 20, "[on - solids only]"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        RadiationBitFlipMod.network.sendToServer(new ModeMessage(button.id));
        mc.displayGuiScreen(null);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "Radiation Bit Flip Mod", width / 2, height / 2 - 40, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
