package cool.furry.mc.neoforge.projectexpansion.item;

import cool.furry.mc.neoforge.projectexpansion.util.Fuel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

public class ItemFuel extends Item {
    private final Fuel level;
    public ItemFuel(Fuel level) {
        //26.3 removed the furnace fuel table, so the burn time is baked into the item as a COOKING_FUEL component
        super(new Properties().rarity(level.getRarity()).component(DataComponents.COOKING_FUEL,
                new CookingFuel(new ResolvableInt.Constant(level.getDefaultBurnTime()), new ResolvableFloat.Constant(1.0F))));
        this.level = level;
    }

    public Fuel getLevel() {
        return level;
    }
}