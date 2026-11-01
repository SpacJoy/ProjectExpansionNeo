package cool.furry.mc.neoforge.projectexpansion.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.UUID;
import moze_intel.projecte.rendering.LayerYue;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// this removes the "yue" from players that aren't sin or their gf when in development (because it's always shown in development, thanks for that sin)
@Mixin(value = LayerYue.class)
@SuppressWarnings("unused")
public class DeleteWeebShitMixin {
    /**
     * 26.3 replaced the layer render hook with a state driven submit pass: {@code LayerYue#submit(PoseStack,
     * SubmitNodeCollector, int, AvatarRenderState, float, float)}. The live {@code AbstractClientPlayer} is no longer
     * passed in, so the render state id is resolved back to an entity the same way {@code LayerYue} itself does it.
     */
    @Inject(at = @At("HEAD"), method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", cancellable = true)
    public void submit(PoseStack pose, SubmitNodeCollector collector, int light, AvatarRenderState state, float limbSwing, float limbSwingAmount, CallbackInfo ci) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            return;
        }
        Entity entity = Minecraft.getInstance().level == null ? null : Minecraft.getInstance().level.getEntity(state.id);
        if (entity instanceof Player player && (SIN_UUID.equals(player.getUUID()) || CLAR_UUID.equals(player.getUUID()))) {
            return;
        }
        ci.cancel();
    }

    @Shadow(remap = false) @Final private static UUID SIN_UUID;
    @Shadow(remap = false) @Final private static UUID CLAR_UUID;
}