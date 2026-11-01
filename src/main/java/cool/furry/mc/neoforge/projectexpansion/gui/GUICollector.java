package cool.furry.mc.neoforge.projectexpansion.gui;

import cool.furry.mc.neoforge.projectexpansion.gui.container.ContainerCollector;
import cool.furry.mc.neoforge.projectexpansion.util.EMCFormat;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.gui.PEContainerScreen;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class GUICollector<T extends ContainerCollector> extends PEContainerScreen<T> {

    public GUICollector(T container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
    }

    //26.3 made imageWidth/imageHeight final on AbstractContainerScreen, so the panel size has to reach super directly
    public GUICollector(T container, Inventory invPlayer, Component title, int imageWidth, int imageHeight) {
        super(container, invPlayer, title, imageWidth, imageHeight);
    }

    protected abstract Identifier getTexture();

    protected int getBonusXShift() {
        return 0;
    }

    protected int getTextureBonusXShift() {
        return 0;
    }

    @Override
    //26.3 replaced drawString with text
    protected void extractLabels(GuiGraphicsExtractor graphics, int x, int y) {
        //Don't render title or inventory as we don't have space
        graphics.text(font, EMCFormat.format(menu.emc.get()), 60 + getBonusXShift(), 32, 0xFF404040, false);
        long kleinCharge = menu.kleinEmc.get();
        if (kleinCharge > 0) {
            graphics.text(font, EMCHelper.formatEmc(kleinCharge), 60 + getBonusXShift(), 44, 0xFF404040, false);
        }
    }

    @Override
    //26.3 requires an explicit render pipeline for every blit, which blitGui supplies
    public void extractContents(GuiGraphicsExtractor graphics, int x, int y, float partialTicks) {
        blitGui(graphics, getTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight);

        //Light Level. Max is 12
        int progress = (int) (menu.sunLevel.get() * 12.0 / 16);
        blitGui(graphics, getTexture(), leftPos + 126 + getBonusXShift(), topPos + 49 - progress, 177 + getTextureBonusXShift(), 13 - progress, 12, progress);

        //EMC storage. Max is 48
        blitGui(graphics, getTexture(), leftPos + 64 + getBonusXShift(), topPos + 18, 0, 166, (int) (new BigDecimal(menu.emc.get()).divide(new BigDecimal(menu.collector.getMaximumEmcBigInteger()), 3, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(48)).doubleValue()), 10);

        //Klein Star Charge Progress. Max is 48
        progress = (int) (menu.getKleinChargeProgress() * 48);
        blitGui(graphics, getTexture(), leftPos + 64 + getBonusXShift(), topPos + 58, 0, 166, progress, 10);

        //Fuel Progress. Max is 24.
        progress = (int) (menu.getFuelProgress() * 24);
        blitGui(graphics, getTexture(), leftPos + 138 + getBonusXShift(), topPos + 55 - progress, 176 + getTextureBonusXShift(), 38 - progress, 10, progress + 1);
    }

    public static class Tier1 extends GUICollector<ContainerCollector> {

        public Tier1(ContainerCollector container, Inventory invPlayer, Component title) {
            super(container, invPlayer, title, 176, 166);
        }

        @Override
        protected Identifier getTexture() {
            return PECore.rl("textures/gui/collector1.png");
        }
    }

    public static class Tier2 extends GUICollector<ContainerCollector> {

        public Tier2(ContainerCollector container, Inventory invPlayer, Component title) {
            //26.3 made imageWidth/imageHeight final; the size goes through the five argument constructor instead
            super(container, invPlayer, title, 200, 165);
        }

        @Override
        protected Identifier getTexture() {
            return PECore.rl("textures/gui/collector2.png");
        }

        @Override
        protected int getBonusXShift() {
            return 16;
        }

        @Override
        protected int getTextureBonusXShift() {
            return 25;
        }
    }

    public static class Tier3 extends GUICollector<ContainerCollector> {

        public Tier3(ContainerCollector container, Inventory invPlayer, Component title) {
            //26.3 made imageWidth/imageHeight final; the size goes through the five argument constructor instead
            super(container, invPlayer, title, 218, 165);
        }

        @Override
        protected Identifier getTexture() {
            return PECore.rl("textures/gui/collector3.png");
        }

        @Override
        protected int getBonusXShift() {
            return 34;
        }

        @Override
        protected int getTextureBonusXShift() {
            return 43;
        }
    }
}
