package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import iskallia.vault.entity.FighterEntity;
import iskallia.vault.entity.model.FighterModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;

public class FighterRenderer extends LivingRenderer<FighterEntity, FighterModel> {

    public FighterRenderer(EntityRendererManager renderManager) {
        this(renderManager, false);
    }

    public FighterRenderer(EntityRendererManager renderManager, boolean useSmallArms) {
        super(renderManager, new FighterModel(0.0F, useSmallArms), 0.5F);
        this.addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5F), new BipedModel<>(1.0F)));
        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new ArrowLayer<>(this));
        //this.addLayer(new Deadmau5HeadLayer(this));
        //this.addLayer(new CapeLayer(this));
        this.addLayer(new HeadLayer<>(this));
        this.addLayer(new ElytraLayer<>(this));
        //this.addLayer(new ParrotVariantLayer<>(this));
        //this.addLayer(new SpinAttackEffectLayer<>(this));
        this.addLayer(new BeeStingerLayer<>(this));
    }

    @Override
    protected void scale(FighterEntity entity, MatrixStack matrixStack, float partialTickTime) {
        float f = entity.sizeMultiplier;
        matrixStack.scale(f, f, f);
    }

    @Override
    public void render(FighterEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLightIn) {
        this.setModelVisibilities(entity);
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLightIn);
    }

    public void renderCrown(FighterEntity entity, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        matrixStack.pushPose();
        float sizeMultiplier = entity.getSizeMultiplier();
        matrixStack.scale(sizeMultiplier, sizeMultiplier, sizeMultiplier);
        matrixStack.translate(0, 2.5f, 0);
        float scale = 2.5f;
        matrixStack.scale(scale, scale, scale);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(entity.tickCount));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(20f));
        ItemStack itemStack = new ItemStack(Registry.ITEM.get(Vault.id("mvp_crown")));
        IBakedModel ibakedmodel = Minecraft.getInstance()
                .getItemRenderer().getModel(itemStack, null, null);
        Minecraft.getInstance().getItemRenderer()
                .render(itemStack, ItemCameraTransforms.TransformType.GROUND, true,
                        matrixStack, buffer, 15728864, 655360, ibakedmodel);
        matrixStack.popPose();
    }

    public Vector3d getRenderOffset(FighterEntity entityIn, float partialTicks) {
        return entityIn.isCrouching() ? new Vector3d(0.0D, -0.125D, 0.0D) : super.getRenderOffset(entityIn, partialTicks);
    }

    private void setModelVisibilities(FighterEntity clientPlayer) {
        FighterModel playermodel = this.getModel();
        if (clientPlayer.isSpectator()) {
            playermodel.setAllVisible(false);
            playermodel.head.visible = true;
            playermodel.hat.visible = true;
        } else {
            playermodel.setAllVisible(true);
			/* TODO: Maybe...
			playermodel.bipedHeadwear.showModel = clientPlayer.isWearing(PlayerModelPart.HAT);
			playermodel.bipedBodyWear.showModel = clientPlayer.isWearing(PlayerModelPart.JACKET);
			playermodel.bipedLeftLegwear.showModel = clientPlayer.isWearing(PlayerModelPart.LEFT_PANTS_LEG);
			playermodel.bipedRightLegwear.showModel = clientPlayer.isWearing(PlayerModelPart.RIGHT_PANTS_LEG);
			playermodel.bipedLeftArmwear.showModel = clientPlayer.isWearing(PlayerModelPart.LEFT_SLEEVE);
			playermodel.bipedRightArmwear.showModel = clientPlayer.isWearing(PlayerModelPart.RIGHT_SLEEVE);
			*/
            playermodel.crouching = clientPlayer.isCrouching();
            BipedModel.ArmPose bipedmodel$armpose = getArmPose(clientPlayer, Hand.MAIN_HAND);
            BipedModel.ArmPose bipedmodel$armpose1 = getArmPose(clientPlayer, Hand.OFF_HAND);
            if (bipedmodel$armpose.isTwoHanded()) {
                bipedmodel$armpose1 = clientPlayer.getOffhandItem().isEmpty() ? BipedModel.ArmPose.EMPTY : BipedModel.ArmPose.ITEM;
            }

            if (clientPlayer.getMainArm() == HandSide.RIGHT) {
                playermodel.rightArmPose = bipedmodel$armpose;
                playermodel.leftArmPose = bipedmodel$armpose1;
            } else {
                playermodel.rightArmPose = bipedmodel$armpose1;
                playermodel.leftArmPose = bipedmodel$armpose;
            }
        }

    }

    private static BipedModel.ArmPose getArmPose(FighterEntity p_241741_0_, Hand p_241741_1_) {
        ItemStack itemstack = p_241741_0_.getItemInHand(p_241741_1_);
        if (itemstack.isEmpty()) {
            return BipedModel.ArmPose.EMPTY;
        } else {
            if (p_241741_0_.getUsedItemHand() == p_241741_1_ && p_241741_0_.getUseItemRemainingTicks() > 0) {
                UseAction useaction = itemstack.getUseAnimation();
                if (useaction == UseAction.BLOCK) {
                    return BipedModel.ArmPose.BLOCK;
                }

                if (useaction == UseAction.BOW) {
                    return BipedModel.ArmPose.BOW_AND_ARROW;
                }

                if (useaction == UseAction.SPEAR) {
                    return BipedModel.ArmPose.THROW_SPEAR;
                }

                if (useaction == UseAction.CROSSBOW && p_241741_1_ == p_241741_0_.getUsedItemHand()) {
                    return BipedModel.ArmPose.CROSSBOW_CHARGE;
                }
            } else if (!p_241741_0_.swinging && itemstack.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemstack)) {
                return BipedModel.ArmPose.CROSSBOW_HOLD;
            }

            return BipedModel.ArmPose.ITEM;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(FighterEntity entity) {
        return entity.getLocationSkin();
    }

    protected void preRenderCallback(AbstractClientPlayerEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
    }

    protected void renderNameTag(FighterEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        double d0 = this.entityRenderDispatcher.distanceToSqr(entityIn);
        matrixStackIn.pushPose();

		/* TODO: Maybe...
		if (d0 < 100.0D) {
			Scoreboard scoreboard = entityIn.getWorldScoreboard();
			ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(2);
			if (scoreobjective != null) {
				Score score = scoreboard.getOrCreateScore(entityIn.getScoreboardName(), scoreobjective);
				super.renderName(entityIn, (new StringTextComponent(Integer.toString(score.getScorePoints()))).appendString(" ").append(scoreobjective.getDisplayName()), matrixStackIn, bufferIn, packedLightIn);
				matrixStackIn.translate(0.0D, (double)(9.0F * 1.15F * 0.025F), 0.0D);
			}
		}*/

        super.renderNameTag(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.popPose();
    }

    public void renderRightArm(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, FighterEntity playerIn) {
        this.renderItem(matrixStackIn, bufferIn, combinedLightIn, playerIn, (this.model).rightArm, (this.model).rightSleeve);
    }

    public void renderLeftArm(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, FighterEntity playerIn) {
        this.renderItem(matrixStackIn, bufferIn, combinedLightIn, playerIn, (this.model).leftArm, (this.model).leftSleeve);
    }

    private void renderItem(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int combinedLight, FighterEntity entity, ModelRenderer rendererArm, ModelRenderer rendererArmWear) {
        FighterModel playermodel = this.getModel();
        this.setModelVisibilities(entity);
        playermodel.attackTime = 0.0F;
        playermodel.crouching = false;
        playermodel.swimAmount = 0.0F;
        playermodel.setupAnim(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        rendererArm.xRot = 0.0F;
        rendererArm.render(matrixStackIn, buffer.getBuffer(RenderType.entitySolid(this.getTextureLocation(entity))), combinedLight, OverlayTexture.NO_OVERLAY);
        rendererArmWear.xRot = 0.0F;
        rendererArmWear.render(matrixStackIn, buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity))), combinedLight, OverlayTexture.NO_OVERLAY);
    }

    protected void setupRotations(FighterEntity entityLiving, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks) {
        float f = entityLiving.getSwimAmount(partialTicks);
        if (entityLiving.isFallFlying()) {
            super.setupRotations(entityLiving, matrixStack, ageInTicks, rotationYaw, partialTicks);
            float f1 = (float) entityLiving.getFallFlyingTicks() + partialTicks;
            float f2 = MathHelper.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
            if (!entityLiving.isAutoSpinAttack()) {
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(f2 * (-90.0F - entityLiving.xRot)));
            }

            Vector3d vector3d = entityLiving.getViewVector(partialTicks);
            Vector3d vector3d1 = entityLiving.getDeltaMovement();
            double d0 = Entity.getHorizontalDistanceSqr(vector3d1);
            double d1 = Entity.getHorizontalDistanceSqr(vector3d);
            if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d0 * d1);
                double d3 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;
                matrixStack.mulPose(Vector3f.YP.rotation((float) (Math.signum(d3) * Math.acos(d2))));
            }
        } else if (f > 0.0F) {
            super.setupRotations(entityLiving, matrixStack, ageInTicks, rotationYaw, partialTicks);
            float f3 = entityLiving.isInWater() ? -90.0F - entityLiving.xRot : -90.0F;
            float f4 = MathHelper.lerp(f, 0.0F, f3);
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(f4));
            if (entityLiving.isVisuallySwimming()) {
                matrixStack.translate(0.0D, -1.0D, (double) 0.3F);
            }
        } else {
            super.setupRotations(entityLiving, matrixStack, ageInTicks, rotationYaw, partialTicks);
        }

    }
}
