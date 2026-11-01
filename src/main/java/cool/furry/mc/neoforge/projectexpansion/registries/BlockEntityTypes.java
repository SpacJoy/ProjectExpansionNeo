package cool.furry.mc.neoforge.projectexpansion.registries;

import cool.furry.mc.neoforge.projectexpansion.Main;
import cool.furry.mc.neoforge.projectexpansion.block.entity.*;
import cool.furry.mc.neoforge.projectexpansion.util.AdvancedAlchemicalChest;
import cool.furry.mc.neoforge.projectexpansion.util.Matter;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;

import java.util.Set;
import java.util.Arrays;

@SuppressWarnings({"unused", "ConstantConditions"})
public class BlockEntityTypes {
    public static final PEDeferredRegister<BlockEntityType<?>> Registry = PEDeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Main.MOD_ID);

    //26.3 removed BlockEntityType$Builder; the constructor takes the supplier and the block set directly
    public static final PEDeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityEMCLink>> EMC_LINK = Registry.register("emc_link", () -> new BlockEntityType<>(BlockEntityEMCLink::new, Set.of(Arrays.stream(Matter.VALUES).map(Matter::getEMCLink).toArray(Block[]::new))));
    public static final PEDeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityPowerFlower>> POWER_FLOWER = Registry.register("power_flower", () -> new BlockEntityType<>(BlockEntityPowerFlower::new, Set.of(Arrays.stream(Matter.VALUES).map(Matter::getPowerFlower).toArray(Block[]::new))));
    public static final PEDeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityCollector>> COLLECTOR = Registry.register("collector", () -> new BlockEntityType<>(BlockEntityCollector::new, Set.of(Arrays.stream(Matter.VALUES).map(Matter::getCollector).toArray(Block[]::new))));
    public static final PEDeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityRelay>> RELAY = Registry.register("relay", () -> new BlockEntityType<>(BlockEntityRelay::new, Set.of(Arrays.stream(Matter.VALUES).map(Matter::getRelay).toArray(Block[]::new))));
    public static final PEDeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityTransmutationInterface>> TRANSMUTATION_INTERFACE = Registry.register("transmutation_interface", () -> new BlockEntityType<>(BlockEntityTransmutationInterface::new, Set.of(Blocks.TRANSMUTATION_INTERFACE.get())));
    public static final PEDeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityAdvancedAlchemicalChest>> ADVANCED_ALCHEMICAL_CHEST = Registry.register("advanced_alchemical_chest", () -> new BlockEntityType<>(BlockEntityAdvancedAlchemicalChest::new, Set.of(Arrays.stream(AdvancedAlchemicalChest.getBlocks()).toArray(Block[]::new))));
    public static final PEDeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityCondenserMK3>> CONDENSER_MK3 = Registry.register("condenser_mk3", () -> new BlockEntityType<>(BlockEntityCondenserMK3::new, Set.of(Blocks.CONDENSER_MK3.get())));
}
