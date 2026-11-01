package cool.furry.mc.neoforge.projectexpansion.item;

import net.minecraft.world.item.component.TooltipDisplay;

import cool.furry.mc.neoforge.projectexpansion.config.Config;
import cool.furry.mc.neoforge.projectexpansion.util.ColorStyle;
import cool.furry.mc.neoforge.projectexpansion.util.EMCFormat;
import cool.furry.mc.neoforge.projectexpansion.util.Lang;
import cool.furry.mc.neoforge.projectexpansion.util.Util;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.List;
import java.util.function.Consumer;

public class ItemInfiniteSteak extends Item {
    @SuppressWarnings("unused")
    public ItemInfiniteSteak() {
        super(new Properties()
                .food(Foods.COOKED_BEEF)
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        tooltip.accept(Lang.Items.INFINITE_STEAK_TOOLTIP.translateColored(ChatFormatting.GRAY));
        tooltip.accept(Lang.COST.translateColored(ChatFormatting.RED, EMCFormat.getComponent(Config.server.infiniteSteakCost.get()).setStyle(ColorStyle.GRAY)));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return Items.COOKED_BEEF.getUseDuration(stack, entity);
    }


    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        @Nullable IKnowledgeProvider provider = Util.getKnowledgeProvider(player);
        if (!player.canEat(false) || Config.server.infiniteSteakCost.get() == 0 || provider == null || provider.getEmc().compareTo(BigInteger.valueOf(Config.server.infiniteSteakCost.get())) < 0) return InteractionResult.FAIL;
        player.startUsingItem(hand);
        //26.3 has no sidedSuccess helper; the swing has to be predicted on the client and confirmed on the server
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof ServerPlayer player) || level.isClientSide()) return stack;
        @Nullable IKnowledgeProvider provider = Util.getKnowledgeProvider(player);
        if (provider == null) {
            player.sendOverlayMessage(Lang.FAILED_TO_GET_KNOWLEDGE_PROVIDER.translateColored(ChatFormatting.RED, player.getDisplayName()));
            return stack;
        }
        BigInteger emc = provider.getEmc().subtract(BigInteger.valueOf(Config.server.infiniteSteakCost.get()));
        if (emc.compareTo(BigInteger.ZERO) < 0) {
            player.sendOverlayMessage(Lang.Items.INFINITE_STEAK_NOT_ENOUGH_EMC.translateColored(ChatFormatting.RED, Component.literal(Config.server.infiniteSteakCost.get().toString())));
            return stack;
        }
        provider.setEmc(emc);
        provider.syncEmc(player);
        //26.3 moved the eat helper off Player; the food data owns the nutrition maths now
        //26.3: FoodData#eat takes the food properties, not a stack
        player.getFoodData().eat(new net.minecraft.world.food.FoodProperties(8, 0.8F, true));
        return stack;
    }
}
