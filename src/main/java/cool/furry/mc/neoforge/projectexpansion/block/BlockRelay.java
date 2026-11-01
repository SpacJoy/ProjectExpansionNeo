package cool.furry.mc.neoforge.projectexpansion.block;

import com.mojang.serialization.MapCodec;
import cool.furry.mc.neoforge.projectexpansion.block.entity.BlockEntityRelay;
import cool.furry.mc.neoforge.projectexpansion.config.Config;
import cool.furry.mc.neoforge.projectexpansion.registries.BlockEntityTypes;
import cool.furry.mc.neoforge.projectexpansion.util.*;
import moze_intel.projecte.gameObjs.IMatterType;
import moze_intel.projecte.gameObjs.blocks.IMatterBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class BlockRelay extends Block implements IHasMatter, EntityBlock, IMatterBlock, IHasBlockTooltip {
    private final Matter matter;

    public BlockRelay(BlockBehaviour.Properties properties, Matter matter) {
        super(properties.pushReaction(PushReaction.IMMOVEABLE).mapColor(matter.mapColor == null ? MapColor.NONE : matter.mapColor.get()));
        this.matter = matter;
    }

    public static BlockBehaviour.Properties getProperties(Matter matter) {
        return Block.Properties.of().strength(getDestroyTime(matter), getExplosionResistance(matter)).requiresCorrectToolForDrops().lightLevel((state) -> Math.min(matter.ordinal(), 15));
    }

    private static float getDestroyTime(Matter matter) {
        return switch (matter) {
            case BASIC -> 10F;
            case DARK -> 1_000_000;
            default -> 2_000_000;
        };
    }

    private static float getExplosionResistance(Matter matter) {
        return switch (matter) {
            case BASIC -> 30F;
            case DARK -> 3_000_000;
            default -> 6_000_000;
        };
    }

    @Override
    public Matter getMatter() {
        return matter;
    }

    @Override
    public IMatterType getMatterType() {
        return Util.getMatterForProjectE(getMatter());
    }


    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityRelay(pos, state);
    }
    //26.3 removed BlockBehaviour#appendHoverText, so the lines are relayed through TooltipBlockItem instead
    @Override
    public void addTooltip(ItemStack stack, Consumer<Component> tooltip) {
        tooltip.accept(Lang.Blocks.RELAY_TOOLTIP.translateColored(ChatFormatting.GRAY));
        tooltip.accept(Lang.Blocks.RELAY_BONUS.translateColored(ChatFormatting.GRAY, EMCFormat.getComponent(getMatter().getRelayBonusForTicks(Config.server.tickDelay.get())).setStyle(ColorStyle.GREEN)));
        tooltip.accept(Lang.Blocks.RELAY_TRANSFER.translateColored(ChatFormatting.GRAY, getMatter().getRelayTransferComponent().setStyle(ColorStyle.GREEN)));
        tooltip.accept(Lang.SEE_WIKI.translateColored(ChatFormatting.AQUA));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type == BlockEntityTypes.RELAY.get() && !level.isClientSide()) return BlockEntityRelay::tickServer;
        return null;
    }
    //26.3 removed the block_type registry and BlockBehaviour#codec; blocks are no longer serialized by codec
    //so the whole override is gone instead of returning a MapCodec that has nowhere to be registered
}
