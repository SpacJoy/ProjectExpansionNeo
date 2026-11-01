package cool.furry.mc.neoforge.projectexpansion.block.entity;

import cool.furry.mc.neoforge.projectexpansion.util.ColorStyle;
import cool.furry.mc.neoforge.projectexpansion.util.Lang;
import cool.furry.mc.neoforge.projectexpansion.util.TagNames;
import cool.furry.mc.neoforge.projectexpansion.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class BlockEntityOwnable extends BlockEntityBase {
    public UUID owner = new UUID(0L, 0L);
    public String ownerName = "";

    public BlockEntityOwnable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        //26.3: ValueInput exposes typed codecs, so the old hasUUID/getUUID pair became a codec read
        input.read(TagNames.OWNER, UUIDUtil.CODEC).ifPresent(value -> owner = value);
        input.getString(TagNames.OWNER_NAME).ifPresent(value -> ownerName = value);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store(TagNames.OWNER, UUIDUtil.CODEC, owner);
        output.putString(TagNames.OWNER_NAME, ownerName);
    }

    public void setOwner(Player player) {
        owner = player.getUUID();
        ownerName = player.getScoreboardName();
        Util.markDirty(this);
    }

    public enum ActivationType {
        DISPLAY_NAME,
        CHECK_OWNERSHIP
    }

    // return true if ownership not checked, or if passed
    public boolean handleActivation(Player player, ActivationType activationType) {
        switch (activationType) {
            case DISPLAY_NAME -> player.sendOverlayMessage(Component.literal(ownerName));
            case CHECK_OWNERSHIP -> {
                if (!owner.equals(player.getUUID())) {
                    player.sendOverlayMessage(Lang.NOT_OWNER.translateColored(ChatFormatting.RED, Component.literal(ownerName).setStyle(ColorStyle.RED)));
                    return false;
                }
            }
        }

        return true;
    }

    public void handlePlace(@Nullable LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity instanceof Player player) setOwner(player);
    }
}
