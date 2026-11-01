package cool.furry.mc.neoforge.projectexpansion.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import cool.furry.mc.neoforge.projectexpansion.Main;
import cool.furry.mc.neoforge.projectexpansion.gui.container.ContainerCondenserMK3Input;
import moze_intel.projecte.gameObjs.gui.PEContainerScreen;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.TransmutationEMCFormatter;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class GUICondenserMK3Input extends PEContainerScreen<ContainerCondenserMK3Input> {
    public GUICondenserMK3Input(ContainerCondenserMK3Input container, Inventory playerInventory, Component title) {
        //26.3 made imageWidth/imageHeight final; the size goes through the five argument constructor instead
        super(container, playerInventory, title, 255, 233);
    }

    protected Identifier getTexture() {
        return Main.rl("textures/gui/condenser_mk3_input.png");
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int x, int y, float partialTicks) {
        //26.3 removed the global shader state; GuiGraphicsExtractor picks the pipeline per blit

        blitGui(graphics, getTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int progress = menu.getProgressScaled();
        blitGui(graphics, getTexture(), leftPos + 33, topPos + 10, 0, 235, progress, 10);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int x, int y) {
        //Don't render title or inventory as we don't have space
        long toDisplay = Math.min(menu.displayEmc.get(), menu.requiredEmc.get());
        Component emc = TransmutationEMCFormatter.formatEMC(toDisplay);
        graphics.text(font, emc, 140, 10, 0xFF404040, false);
    }

    @Override
    //26.3 renamed renderTooltip to extractTooltip, and tooltips are now pushed through the graphics extractor
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        long toDisplay = Math.min(menu.displayEmc.get(), menu.requiredEmc.get());

        if (toDisplay < 1e12) {
            super.extractTooltip(graphics, mouseX, mouseY);
            return;
        }

        int emcLeft = 140 + leftPos;
        int emcRight = emcLeft + 110;
        int emcTop = 6 + topPos;
        int emcBottom = emcTop + 15;

        if (mouseX > emcLeft && mouseX < emcRight && mouseY > emcTop && mouseY < emcBottom) {
            graphics.setTooltipForNextFrame(PELang.EMC_TOOLTIP.translate(EMCHelper.formatEmc(toDisplay)), mouseX, mouseY);
        } else {
            super.extractTooltip(graphics, mouseX, mouseY);
        }
    }
}
