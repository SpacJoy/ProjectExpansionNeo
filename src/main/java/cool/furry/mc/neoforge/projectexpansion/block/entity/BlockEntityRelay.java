package cool.furry.mc.neoforge.projectexpansion.block.entity;

import cool.furry.mc.neoforge.projectexpansion.block.BlockRelay;
import cool.furry.mc.neoforge.projectexpansion.registries.BlockEntityTypes;
import cool.furry.mc.neoforge.projectexpansion.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.state.BlockState;
import cool.furry.mc.neoforge.projectexpansion.platform.CapabilityRegistrar;

import java.math.BigInteger;

@SuppressWarnings("unused")
public class BlockEntityRelay extends BlockEntityEMC implements IHasMatter, IRelayBigInteger {
    public Matter matter;
    public static final Direction[] DIRECTIONS = Direction.values();
    private BigInteger bonusEMC = BigInteger.ZERO;
    public BlockEntityRelay(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.RELAY.get(), pos, state);
    }

    public static void registerCapabilities(CapabilityRegistrar event) {
        BlockEntityEMC.registerCapabilities(event, BlockEntityTypes.RELAY.get());
    }

    public static void tickServer(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof BlockEntityRelay be) be.tickServer(level, pos, state, be);
    }

    public void tickServer(Level level, BlockPos pos, BlockState state, BlockEntityRelay blockEntity) {
        // we can't use the user defined value due to emc duplication possibilities
        if ((level.getGameTime() % 20L) != Util.mod(hashCode(), 20)) return;

        sendToAllAcceptors(level, pos, getStoredEmcBigInteger().min(getMatter().getRelayTransfer()));
    }


    @Override
    public Matter getMatter() {
        BlockRelay block = (BlockRelay) getBlockState().getBlock();
        if (block.getMatter() != matter) {
            this.matter = block.getMatter();
        }
        return matter;
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        //26.3: ValueInput#getString returns an Optional, so the BigInteger is built off the fallback string
        bonusEMC = new BigInteger(input.getStringOr(TagNames.BONUS_EMC, "0"));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString(TagNames.BONUS_EMC, bonusEMC.toString());
    }

    @Override
    public boolean isRelay() {
        return true;
    }

    @Override
    public BigInteger getBonusToAddBigInteger() {
        return getMatter().getRelayBonus();
    }

    @Override
    public void addBonus(Level level, BlockPos pos) {
        bonusEMC = bonusEMC.add(getBonusToAddBigInteger());
        if (bonusEMC.compareTo(BigInteger.ONE) >= 0) {
            insertEmcBigInteger(bonusEMC, EmcAction.EXECUTE);
            bonusEMC = BigInteger.ZERO;
        }
        markDirty(level, pos);
    }
}
