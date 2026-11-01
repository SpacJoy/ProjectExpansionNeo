package cool.furry.mc.neoforge.projectexpansion.item;

import cool.furry.mc.neoforge.projectexpansion.util.Fuel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

import java.util.Objects;

public class FuelBlockItem extends BlockItem {
    private final Fuel level;
    public FuelBlockItem(Fuel level) {
        //26.3 removed the furnace fuel table; nine single fuels are baked into the block item as one COOKING_FUEL component
        super(Objects.requireNonNull(Objects.requireNonNull(level).getBlock()), new Properties().rarity(level.getRarity())
                //26.3 block items default to item.<ns>.<path>; the lang files only carry block.<ns>.<path>
                .useBlockDescriptionPrefix()
                .component(DataComponents.COOKING_FUEL,
                        new CookingFuel(new ResolvableInt.Constant(level.getDefaultBurnTime() * 9), new ResolvableFloat.Constant(1.0F))));
        this.level = level;
    }

    public Fuel getLevel() {
        return level;
    }
}