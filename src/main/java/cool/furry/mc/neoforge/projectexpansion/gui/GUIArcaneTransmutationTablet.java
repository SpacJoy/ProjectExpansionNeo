package cool.furry.mc.neoforge.projectexpansion.gui;

import com.mojang.blaze3d.platform.InputConstants;
import cool.furry.mc.neoforge.projectexpansion.Main;
import cool.furry.mc.neoforge.projectexpansion.config.Config;
import cool.furry.mc.neoforge.projectexpansion.gui.container.ContainerArcaneTransmutationTablet;
import cool.furry.mc.neoforge.projectexpansion.gui.container.slots.PXOutputSlot;
import cool.furry.mc.neoforge.projectexpansion.net.packets.to_server.PacketArcaneTransmutationTabletSmallButton;
import cool.furry.mc.neoforge.projectexpansion.util.EMCFormat;
import cool.furry.mc.neoforge.projectexpansion.util.Lang;
import cool.furry.mc.neoforge.projectexpansion.util.SearchType;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.gui.PEContainerScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.joml.Matrix3x2fStack;

import java.math.BigInteger;

public class GUIArcaneTransmutationTablet extends PEContainerScreen<ContainerArcaneTransmutationTablet> {
    private static final int CRAFTING_PANEL_X = -75;
    private static final int CRAFTING_PANEL_Y = 10;
    private static final int CRAFTING_PANEL_WIDTH = 76;
    private static final int CRAFTING_PANEL_HEIGHT = 89;
    public static final Identifier texture = Main.rl("textures/gui/arcane_transmutation_tablet.png");
    // public static final Identifier texture = Main.rl("textures/gui/arcane_transmutation_tablet_layout.png");

    private final TransmutationInventory inv;
    private EditBox textBoxFilter;

    public GUIArcaneTransmutationTablet(ContainerArcaneTransmutationTablet container, Inventory invPlayer, Component title) {
        //26.3 made imageWidth/imageHeight final; the size goes through the five argument constructor instead
        super(container, invPlayer, title, 176, 217);
        this.inv = container.transmutationInventory;
    }

    private void updateFilter(String text) {
        inv.updateFilter(text);
        Config.client.searchType.get().sync(text);
    }

    private Button previous, next, rotate, balance, search, clear;

    @Override
    public void init() {
        super.init();

        this.textBoxFilter = addRenderableWidget(new EditBox(this.font, leftPos + 7, topPos + 6, 162, 12, Component.empty()));
        textBoxFilter.setResponder(this::updateFilter);

        if (Config.client.searchType.get().autoFocus) {
            setFocused(textBoxFilter);
        }

        this.previous = addRenderableWidget(Button.builder(Component.empty(), (button) -> inv.previousPage())
                .pos(leftPos + 7, topPos + 20)
                .size(18, 18)
                .build());
        this.next = addRenderableWidget(Button.builder(Component.empty(), (button) -> inv.nextPage())
                .pos(leftPos + 151, topPos + 20)
                .size(18, 18)
                .build());
        this.rotate = addRenderableWidget(Button.builder(Component.empty(), (button) -> {
                    menu.action(shiftDown() ? PacketArcaneTransmutationTabletSmallButton.Action.ROTATE_CC : PacketArcaneTransmutationTabletSmallButton.Action.ROTATE);
                })
                .pos(leftPos - 71, topPos + 16)
                .size(9, 9)
                .build());
        this.balance = addRenderableWidget(Button.builder(Component.empty(), (button) -> {
                    menu.action(shiftDown() ? PacketArcaneTransmutationTabletSmallButton.Action.SPREAD : PacketArcaneTransmutationTabletSmallButton.Action.BALANCE);
                })
                .pos(leftPos - 71, topPos + 26)
                .size(9, 9)
                .build());
        this.search = addRenderableWidget(Button.builder(Component.empty(), (button) -> {
                    ModConfigSpec.ConfigValue<SearchType> config = Config.client.searchType;
                    config.set(config.get().next());
                    config.save();
                })
                .pos(leftPos - 71, topPos + 36)
                .size(9, 9)
                .build());
        this.clear = addRenderableWidget(Button.builder(Component.empty(), (button) -> {
                    menu.action(shiftDown() ? PacketArcaneTransmutationTabletSmallButton.Action.CLEAR_FORCE : PacketArcaneTransmutationTabletSmallButton.Action.CLEAR);
                })
                .pos(leftPos - 71, topPos + 61)
                .size(9, 9)
                .build());
        updateButtons();
    }

    //26.3 removed the static Screen#hasShiftDown helper, so the modifier state comes off Minecraft
    private static boolean shiftDown() {
        return Minecraft.getInstance().hasShiftDown();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY < 0 && inv.hasNextPage()) inv.nextPage();
        if (scrollY > 0 && inv.hasPreviousPage()) inv.previousPage();
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    //26.3 dropped the mouse button argument from hasClickedOutside
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top) {
        // The crafting panel extends beyond the main image, but must not act as a drop area.
        boolean insideCraftingPanel = mouseX >= left + CRAFTING_PANEL_X
                && mouseX < left + CRAFTING_PANEL_X + CRAFTING_PANEL_WIDTH
                && mouseY >= top + CRAFTING_PANEL_Y
                && mouseY < top + CRAFTING_PANEL_Y + CRAFTING_PANEL_HEIGHT;
        return !insideCraftingPanel && super.hasClickedOutside(mouseX, mouseY, left, top);
    }

    @Override
    //26.3 changed Screen#resize to take only the new dimensions
    public void resize(int width, int height) {
        String filter = this.textBoxFilter.getValue();
        super.resize(width, height);
        this.textBoxFilter.setValue(filter);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateButtons();
    }

    private void updateButtons() {
        if (previous != null) {
            previous.active = inv.hasPreviousPage();
        }
        if (next != null) {
            next.active = inv.hasNextPage();
        }
    }

    @Override
    //26.3 renamed renderBg to extractBackground. Overriding extractContents instead replaces the parent
    //slot and carried item drawing, which leaves the screen visually empty while clicks still land.
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        blitGui(graphics, texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        blitGui(graphics, texture, leftPos + CRAFTING_PANEL_X, topPos + CRAFTING_PANEL_Y,
                180, 32, CRAFTING_PANEL_WIDTH, CRAFTING_PANEL_HEIGHT);

        renderIfHovered(graphics, mouseX, mouseY, previous, 196, 0);
        renderIfHovered(graphics, mouseX, mouseY, next, 215, 0);
        renderIfHovered(graphics, mouseX, mouseY, rotate, 234, 0);
        renderIfHovered(graphics, mouseX, mouseY, balance, 234, 0);
        renderIfHovered(graphics, mouseX, mouseY, search, 234, 0);
        renderIfHovered(graphics, mouseX, mouseY, clear, 234, 0);

        if (menu.isCrafting) { // fill in the crafting arrow
            blitGui(graphics, texture, leftPos - 50, topPos + 76, 177, 19, 18, 12);
        }

        if (shiftDown()) { // make the clear box red when hovering and holding shift
            renderIfHovered(graphics, mouseX, mouseY, clear, 234, 10);
        }
    }

    private void renderIfHovered(GuiGraphicsExtractor graphics, int mouseX, int mouseY, Button button, int uOffset, int vOffset) {
        if (button.isMouseOver(mouseX, mouseY)) {
            blitGui(graphics, texture, button.getX(), button.getY(), uOffset, vOffset, button.getWidth(), button.getHeight());
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        BigInteger emc = inv.getAvailableEmc();
        String emcFmt = EMCFormat.format(emc);
        int textWidth = font.width(emcFmt);
        int textX = (imageWidth - textWidth) / 2;
        graphics.text(font, emcFmt, textX, -9, 0xFFFFFFFF, true);

        if (inv.learnFlag > 0) {
            Component learned = Lang.GUI.ARCANE_TRANSMUTATION_TABLET_LEARNED.translate();
            graphics.text(font, learned, 170 - font.width(learned), 60, 0xFFFFFFFF, true);
            inv.learnFlag--;
        }

        if (inv.unlearnFlag > 0) {
            graphics.text(font, Lang.GUI.ARCANE_TRANSMUTATION_TABLET_UNLEARNED.translate(), 6, 60, 0xFFFFFFFF, true);
            inv.unlearnFlag--;
        }
    }

    @Override
    //26.3 no longer stacks gui elements by call order. Every draw call is filed into a node tree of
    // GuiRenderState keyed by bounding box (findAppropriateNode -> navigateToAboveHighestElementWithIntersectingBounds),
    // so drawing the counts after super.extractSlots still lets the item icons win. nextStratum forces a new layer on
    // top of everything drawn so far, which is exactly what vanilla uses for the carried item.
    protected void extractSlots(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractSlots(graphics, mouseX, mouseY);
        BigInteger emc = inv.getAvailableEmc();
        //26.3 flattened the GUI matrix to a 2D stack, so only the horizontal and vertical offsets survive
        Matrix3x2fStack pose = graphics.pose();
        //One extra stratum for all the counts is enough. Without it the bounding box based layering of GuiRenderState
        // puts the item icons drawn by super.extractSlots above these labels.
        graphics.nextStratum();
        for (PXOutputSlot slot : menu.getOutputSlots()) {
            long value = IEMCProxy.INSTANCE.getValue(slot.getItem());
            if (value <= 0) continue;
            BigInteger count = emc.equals(BigInteger.ZERO) ? emc : emc.divide(BigInteger.valueOf(value));
            String countFmt = EMCFormat.formatForceShort(count);
            pose.pushMatrix();
            pose.translate(slot.x + 17, slot.y + 12);
            pose.scale(0.5F);
            graphics.text(font, countFmt, -font.width(countFmt), 0, 0xFFFFFFFF, true);
            pose.popMatrix();
        }
    }

    @Override
    //26.3 routes key input through a KeyEvent record
    public boolean keyPressed(KeyEvent event) {
        if (textBoxFilter.isFocused()) {
            //Manually make it so that hitting escape when the filter is focused will exit the focus
            if (event.key() == InputConstants.KEY_ESCAPE) {
                textBoxFilter.setFocused(false);
                return true;
            }
            //Otherwise have it handle the key press
            //This is where key combos and deletion is handled, and where we bypass the inventory key closing the screen
            return textBoxFilter.keyPressed(event);
        }
        return super.keyPressed(event);
    }

    @Override
    //26.3 routes mouse input through a MouseButtonEvent record
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (textBoxFilter.isMouseOver(event.x(), event.y())) {
            if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
                //Note: Clearing filter will be handled by the text box's responder
                this.textBoxFilter.setValue("");
            }
        } else if (textBoxFilter.isFocused()) {
            if (hoveredSlot == null || (!hoveredSlot.hasItem() && menu.getCarried().isEmpty())) {
                textBoxFilter.setFocused(false);
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void removed() {
        super.removed();
        inv.learnFlag = 0;
        inv.unlearnFlag = 0;
    }

    @Override
    //26.3 renamed renderTooltip to extractTooltip, and tooltips are now pushed through the graphics extractor
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            super.extractTooltip(graphics, mouseX, mouseY);
            return;
        }

        int width = 16, height = 16,
                lockX = leftPos + menu.lockX, lockY = topPos + menu.lockY,
                consumeX = leftPos + menu.consumeX, consumeY = topPos + menu.consumeY,
                unlearnX  = leftPos + menu.unlearnX, unlearnY = topPos + menu.unlearnY;

        boolean shiftDown = shiftDown();
        if (mouseX > lockX && mouseX < (lockX + width) && mouseY > lockY && mouseY < (lockY + height)) {
            graphics.setTooltipForNextFrame(Lang.GUI.ARCANE_TRANSMUTATION_TABLET_LOCK.translate(), mouseX, mouseY);
        } else if (mouseX > consumeX && mouseX < (consumeX + width) && mouseY > consumeY && mouseY < (consumeY + height)) {
            graphics.setTooltipForNextFrame(Lang.GUI.ARCANE_TRANSMUTATION_TABLET_CONSUME.translate(), mouseX, mouseY);
        } else if (mouseX > unlearnX && mouseX < (unlearnX + width) && mouseY > unlearnY && mouseY < (unlearnY + height)) {
            graphics.setTooltipForNextFrame(Lang.GUI.ARCANE_TRANSMUTATION_TABLET_UNLEARN.translate(), mouseX, mouseY);
        } else if (rotate.isMouseOver(mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(Lang.GUI.ARCANE_TRANSMUTATION_TABLET_ROTATE.translate().append(Component.literal(": ")).append(
                    shiftDown ? Lang.GUI.ARCANE_TRANSMUTATION_TABLET_ROTATE_COUNTER_CLOCKWISE.translateColored(ChatFormatting.GRAY) : Lang.GUI.ARCANE_TRANSMUTATION_TABLET_ROTATE_CLOCKWISE.translateColored(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        } else if (balance.isMouseOver(mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(shiftDown ? Lang.GUI.ARCANE_TRANSMUTATION_TABLET_SPREAD.translate() : Lang.GUI.ARCANE_TRANSMUTATION_TABLET_BALANCE.translate(), mouseX, mouseY);
        } else if (search.isMouseOver(mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(Lang.GUI.ARCANE_TRANSMUTATION_TABLET_SEARCH_TYPE.translate().append(Component.literal(": ")).append(
                    Config.client.searchType.get().translateColored(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        } else if (clear.isMouseOver(mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(Lang.GUI.ARCANE_TRANSMUTATION_TABLET_CLEAR.translate(), mouseX, mouseY);
        } else {
            super.extractTooltip(graphics, mouseX, mouseY);
        }
    }
}
