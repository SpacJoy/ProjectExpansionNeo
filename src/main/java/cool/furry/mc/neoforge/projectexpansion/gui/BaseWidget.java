package cool.furry.mc.neoforge.projectexpansion.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class BaseWidget extends AbstractWidget {
    private final int uOffset, vOffset, uWidth, vHeight;
    private final Identifier texture;
    public BaseWidget(Identifier texture, int x, int y, int imageWidth, int imageHeight) {
        this(texture, x, y, imageWidth, imageHeight, x, y, imageWidth, imageHeight);
    }

    public BaseWidget(Identifier texture, int x, int y, int imageWidth, int imageHeight, int uOffset, int vOffset, int uWidth, int vHeight) {
        super(x, y, imageWidth, imageHeight, Component.empty());
        this.uOffset = uOffset;
        this.vOffset = vOffset;
        this.uWidth = uWidth;
        this.vHeight = vHeight;
        this.texture = texture;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        //26.3 requires an explicit render pipeline for every blit, which blitGui supplies
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), uOffset, vOffset, uWidth, vHeight, 256, 256);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
