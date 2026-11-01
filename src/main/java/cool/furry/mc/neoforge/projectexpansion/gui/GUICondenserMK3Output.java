package cool.furry.mc.neoforge.projectexpansion.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import cool.furry.mc.neoforge.projectexpansion.Main;
import cool.furry.mc.neoforge.projectexpansion.gui.container.ContainerCondenserMK3Output;
import moze_intel.projecte.gameObjs.gui.PEContainerScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class GUICondenserMK3Output extends PEContainerScreen<ContainerCondenserMK3Output> {
    public GUICondenserMK3Output(ContainerCondenserMK3Output container, Inventory playerInventory, Component title) {
        //26.3 made imageWidth/imageHeight final; the size goes through the five argument constructor instead
        super(container, playerInventory, title, 382, 252);
    }

    protected Identifier getTexture() {
        return Main.rl("textures/gui/condenser_mk3_output.png");
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {}

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int x, int y, float partialTicks) {
        //26.3 removed the global shader state; GuiGraphicsExtractor picks the pipeline per blit

        blitGui(graphics, getTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
