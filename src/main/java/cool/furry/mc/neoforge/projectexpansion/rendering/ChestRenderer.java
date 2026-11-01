package cool.furry.mc.neoforge.projectexpansion.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import cool.furry.mc.neoforge.projectexpansion.Main;
import cool.furry.mc.neoforge.projectexpansion.block.BlockAdvancedAlchemicalChest;
import cool.furry.mc.neoforge.projectexpansion.block.BlockCondenserMK3;
import cool.furry.mc.neoforge.projectexpansion.util.IChestLike;
import moze_intel.projecte.PECore;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// lovingly "lifted" from ProjectE
// https://github.com/sinkillerj/ProjectE/blob/98aee771bdb09beecf51b5608938d93de6f1afb6/src/main/java/moze_intel/projecte/rendering/ChestRenderer.java
public class ChestRenderer<BE extends BlockEntity & IChestLike> implements BlockEntityRenderer<BE, ChestRenderState> {

	/**
	 * 26.3 needs an explicit {@link UvMapping} when a model part is submitted directly. The baked chest model already
	 * carries texture coordinates in the 0..1 range (vanilla only needs a mapping because it offsets into an atlas
	 * sprite), and the ProjectExpansion chests bind their own texture, so the identity mapping is what the old raw
	 * {@code ModelPart#render(PoseStack, VertexConsumer, int, int)} call used implicitly.
	 */
	private static final UvMapping CHEST_UV = new UvMapping() {
		@Override
		public float getU(float u) {
			return u;
		}

		@Override
		public float getV(float v) {
			return v;
		}
	};

	private final ModelPart lid;
	private final ModelPart bottom;
	private final ModelPart lock;

	private final BlockEntityType<BE> type;

	public ChestRenderer(BlockEntityRendererProvider.Context context, BlockEntityType<BE> type) {
		this.type = type;
		ModelPart modelpart = context.bakeLayer(ModelLayers.CHEST);
		this.bottom = modelpart.getChild("bottom");
		this.lid = modelpart.getChild("lid");
		this.lock = modelpart.getChild("lock");
	}

	@Override
	public ChestRenderState createRenderState() {
		return new ChestRenderState();
	}

	@Override
	public void extractRenderState(@NotNull BE chest, @NotNull ChestRenderState state, float partialTick,
			@Nullable Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(chest, state, partialTick, cameraPosition, crumblingOverlay);
		//26.3 moved everything the submit pass needs into the render state; the block entity is only readable here.
		state.facingYRot = 0;
		state.texture = null;
		if (chest.getLevel() != null && !chest.isRemoved()) {
			BlockState blockState = chest.getLevel().getBlockState(chest.getBlockPos());
			state.facingYRot = -blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot();
			state.texture = getTexture(blockState.getBlock());
		}
		float openNess = 1.0F - chest.getOpenNess(partialTick);
		state.lidAngle = 1.0F - openNess * openNess * openNess;
	}

	@Override
	public void submit(@NotNull ChestRenderState state, @NotNull PoseStack pose, @NotNull SubmitNodeCollector collector, @NotNull CameraRenderState camera) {
		Identifier texture = state.texture;
		if (texture == null) {
			return;
		}
		pose.pushPose();
		pose.translate(0.5D, 0.5D, 0.5D);
		pose.rotate(Axis.YP.rotationDegrees(state.facingYRot));
		pose.translate(-0.5D, -0.5D, -0.5D);
		//26.3: model parts are submitted rather than written straight into a buffer, so the pose is applied here and the
		//part rotation is only meaningful for the duration of this call.
		lid.xRot = -(state.lidAngle * Mth.HALF_PI);
		lock.xRot = lid.xRot;
		collector.submitModelPart(lid, pose, RenderTypes.entityCutout(texture), state.lightCoords, OverlayTexture.NO_OVERLAY, CHEST_UV);
		collector.submitModelPart(lock, pose, RenderTypes.entityCutout(texture), state.lightCoords, OverlayTexture.NO_OVERLAY, CHEST_UV);
		collector.submitModelPart(bottom, pose, RenderTypes.entityCutout(texture), state.lightCoords, OverlayTexture.NO_OVERLAY, CHEST_UV);
		pose.popPose();
	}

	private @Nullable Identifier getTexture(Block block) {
		return switch (block) {
			case BlockAdvancedAlchemicalChest chest -> Main.rl(String.format("textures/block/advanced_alchemical_chest/%s.png", chest.getColor().getName()));
			case BlockCondenserMK3 ignored -> PECore.rl("textures/block/condenser_mk1.png");
			default -> null;
		};
	}
}