package cool.furry.mc.neoforge.projectexpansion.block;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Lets a block contribute lines to the tooltip of the item that places it.
 * <p>
 * 26.3 dropped {@code BlockBehaviour#appendHoverText} and {@code BlockItem} no longer overrides it either: tooltip text is
 * an item concern only. The lines therefore travel through {@code TooltipBlockItem}, which asks its block via this
 * interface. The stack is passed along because several of the machines change their tooltip with the stack size.
 */
public interface IHasBlockTooltip {

	/**
	 * Accepts the lines this block wants appended to the tooltip of {@code stack}.
	 */
	void addTooltip(ItemStack stack, Consumer<Component> tooltip);
}