package cool.furry.mc.neoforge.projectexpansion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import cool.furry.mc.neoforge.projectexpansion.config.Config;
import cool.furry.mc.neoforge.projectexpansion.item.ItemInfiniteFuel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class FurnaceFuelMixin {
    // Loading a previously paid burn must not charge EMC again.
    // 26.3 added the ServerLevel first parameter, because the burn time can now depend on the level.
    @Inject(method = "getBurnDuration", at = @At("HEAD"), cancellable = true)
    private void projectexpansion$fuelDuration(ServerLevel level, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (stack.getItem() instanceof ItemInfiniteFuel) cir.setReturnValue(Config.server.infiniteFuelBurnTime.get());
    }

    // 26.3 deleted AbstractFurnaceBlockEntity#isFuel. A positive burn duration is what marks a stack as
    // usable fuel now (serverTick only calls consumeFuel when getBurnDuration returned more than zero),
    // and the injection above already reports one for infinite fuel, so the old acceptance hook is gone.

    // This call is behind canBurn: idle or blocked furnaces never charge, and each paid grant lasts one tick.
    @WrapOperation(method = "serverTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;getBurnDuration(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;)I"))
    private static int projectexpansion$payForTick(AbstractFurnaceBlockEntity furnace, ServerLevel level, ItemStack stack, Operation<Integer> original) {
        if (!(stack.getItem() instanceof ItemInfiniteFuel fuel)) return original.call(furnace, level, stack);
        int period = Config.server.infiniteFuelBurnTime.get();
        if (furnace instanceof BlastFurnaceBlockEntity || furnace instanceof SmokerBlockEntity) period = Math.max(1, period / 2);
        return fuel.consumeTick(stack, period) ? 1 : 0;
    }

    // 26.3 moved the fuel stack's shrink out of serverTick into consumeFuel, so the hook follows it there.
    // The other shrink call, in burn, consumes the smelted input and must keep working.
    @WrapOperation(method = "consumeFuel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private static void projectexpansion$keepInfiniteFuel(ItemStack stack, int amount, Operation<Void> original) {
        if (!(stack.getItem() instanceof ItemInfiniteFuel)) original.call(stack, amount);
    }
}
