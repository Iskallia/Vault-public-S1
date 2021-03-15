package iskallia.vault.network.message;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameType;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Iterator;
import java.util.function.Supplier;

public class AttackOffHandMessage {

	private int entityId;

	public AttackOffHandMessage(int entityId) {
		this.entityId = entityId;
	}

	public static void encode(AttackOffHandMessage packet, PacketBuffer buf) {
		buf.writeInt(packet.entityId);
	}

	public static AttackOffHandMessage decode(PacketBuffer buf) {
		return new AttackOffHandMessage(buf.readInt());
	}

	public static void handle(AttackOffHandMessage packet, Supplier<NetworkEvent.Context> ctx) {
		if(packet == null)return;

		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity player = ctx.get().getSender();
			ItemStack offhand = player.getHeldItemOffhand();
			if(offhand.isEmpty())return;

			int level = PlayerVaultStatsData.get((ServerWorld)player.world).getVaultStats(player).getVaultLevel();

			if(ModAttributes.MIN_VAULT_LEVEL.exists(offhand)
					&& level < ModAttributes.MIN_VAULT_LEVEL.get(offhand).get().getValue(offhand)) {
				return;
			}

			Entity target = player.world.getEntityByID(packet.entityId);
			if(target == null)return;
			float reach = 6.0F;

			// This is done to mitigate the difference between the render view entity's position
			// that is checked in the client to the server player entity's position
			float renderViewEntityOffsetFromPlayerMitigator = 0.2F;
			reach += renderViewEntityOffsetFromPlayerMitigator;

			double distanceSquared = player.getDistanceSq(target);

			if(reach * reach >= distanceSquared) {
				attackTargetEntityWithCurrentOffhandItem(player, target);
			}

			swingArm(player, Hand.OFF_HAND);
		});
	}

	public static void swingArm(ServerPlayerEntity playerEntity, Hand hand) {
		ItemStack stack = playerEntity.getHeldItem(hand);

		if(stack.isEmpty() || !stack.onEntitySwing(playerEntity)) {
			if(!playerEntity.isSwingInProgress || playerEntity.swingProgressInt >= getArmSwingAnimationEnd(playerEntity) / 2 || playerEntity.swingProgressInt < 0) {
				playerEntity.swingProgressInt = -1;
				playerEntity.isSwingInProgress = true;
				playerEntity.swingingHand = hand;

				if(playerEntity.world instanceof ServerWorld) {
					SAnimateHandPacket sanimatehandpacket = new SAnimateHandPacket(playerEntity, hand == Hand.MAIN_HAND ? 0 : 3);
					ServerChunkProvider serverchunkprovider = ((ServerWorld)playerEntity.world).getChunkProvider();

					serverchunkprovider.sendToAllTracking(playerEntity, sanimatehandpacket);
				}
			}

		}
	}

	private static int getArmSwingAnimationEnd(LivingEntity livingEntity) {
		if(EffectUtils.hasMiningSpeedup(livingEntity)) {
			return 6 - (1 + EffectUtils.getMiningSpeedup(livingEntity));
		} else {
			return livingEntity.isPotionActive(Effects.MINING_FATIGUE) ? 6 + (1 + livingEntity.getActivePotionEffect(Effects.MINING_FATIGUE).getAmplifier()) * 2 : 6;
		}
	}

	public static void attackTargetEntityWithCurrentOffhandItem(ServerPlayerEntity serverPlayerEntity, Entity target) {
		if (serverPlayerEntity.interactionManager.getGameType() == GameType.SPECTATOR) {
			serverPlayerEntity.setSpectatingEntity(target);
		} else {
			attackTargetEntityWithCurrentOffhandItemAsSuper(serverPlayerEntity, target);
		}
	}

	public static void attackTargetEntityWithCurrentOffhandItemAsSuper(PlayerEntity player, Entity target) {
		if(ForgeHooks.onPlayerAttackTarget(player, target)) {
			if(target.canBeAttackedWithItem() && !target.hitByEntity(player)) {
				// get attack damage attribute value
				float attackDamage = (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE);
				float enchantmentAffectsTargetBonus;
				if (target instanceof LivingEntity) {
					enchantmentAffectsTargetBonus = EnchantmentHelper.getModifierForCreature(player.getHeldItemOffhand(), ((LivingEntity)target).getCreatureAttribute());
				} else {
					enchantmentAffectsTargetBonus = EnchantmentHelper.getModifierForCreature(player.getHeldItemOffhand(), CreatureAttribute.UNDEFINED);
				}

				float cooledAttackStrength = player.getCooledAttackStrength(0.5F);
				attackDamage *= 0.2F + cooledAttackStrength * cooledAttackStrength * 0.8F;
				enchantmentAffectsTargetBonus *= cooledAttackStrength;
				//player.resetCooldown();
				if (attackDamage > 0.0F || enchantmentAffectsTargetBonus > 0.0F) {
					boolean flag = cooledAttackStrength > 0.9F;
					boolean flag1 = false;
					int i = 0;
					i = i + EnchantmentHelper.getKnockbackModifier(player);
					if (player.isSprinting() && flag) {
						player.world.playSound((PlayerEntity)null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1.0F, 1.0F);
						++i;
						flag1 = true;
					}

					boolean flag2 = flag && player.fallDistance > 0.0F && !player.isOnGround() && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(Effects.BLINDNESS) && !player.isPassenger() && target instanceof LivingEntity;
					flag2 = flag2 && !player.isSprinting();
					CriticalHitEvent hitResult = ForgeHooks.getCriticalHit(player, target, flag2, flag2 ? 1.5F : 1.0F);
					flag2 = hitResult != null;
					if (flag2) {
						attackDamage *= hitResult.getDamageModifier();
					}

					attackDamage += enchantmentAffectsTargetBonus;
					boolean flag3 = false;
					double d0 = player.distanceWalkedModified - player.prevDistanceWalkedModified;
					if (flag && !flag2 && !flag1 && player.isOnGround() && d0 < (double)player.getAIMoveSpeed()) {
						ItemStack itemstack = player.getHeldItem(Hand.OFF_HAND);
						if (itemstack.getItem() instanceof SwordItem) {
							flag3 = true;
						}
					}

					float f4 = 0.0F;
					boolean flag4 = false;
					int j = EnchantmentHelper.getFireAspectModifier(player);
					if (target instanceof LivingEntity) {
						f4 = ((LivingEntity)target).getHealth();
						if (j > 0 && !target.isBurning()) {
							flag4 = true;
							target.setFire(1);
						}
					}

					Vector3d vector3d = target.getMotion();
					DamageSource offhandAttack = DamageSource.causePlayerDamage(player);
					boolean flag5 = target.attackEntityFrom(offhandAttack, attackDamage);
					if (flag5) {
						if (i > 0) {
							if (target instanceof LivingEntity) {
								((LivingEntity)target).applyKnockback((float)i * 0.5F, (double) MathHelper.sin(player.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(player.rotationYaw * 0.017453292F)));
							} else {
								target.addVelocity(-MathHelper.sin(player.rotationYaw * 0.017453292F) * (float)i * 0.5F, 0.1D, (double)(MathHelper.cos(player.rotationYaw * 0.017453292F) * (float)i * 0.5F));
							}

							player.setMotion(player.getMotion().mul(0.6D, 1.0D, 0.6D));
							player.setSprinting(false);
						}

						if (flag3) {
							float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * attackDamage;
							Iterator<LivingEntity> var19 = player.world.getEntitiesWithinAABB(LivingEntity.class, target.getBoundingBox().grow(1.0D, 0.25D, 1.0D)).iterator();

							label174:
							while(true) {
								LivingEntity livingentity;
								do {
									do {
										do {
											do {
												if (!var19.hasNext()) {
													player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
													player.spawnSweepParticles();
													break label174;
												}

												livingentity = var19.next();
											} while(livingentity == player);
										} while(livingentity == target);
									} while(player.isOnSameTeam(livingentity));
								} while(livingentity instanceof ArmorStandEntity && ((ArmorStandEntity)livingentity).hasMarker());

								if (player.getDistanceSq(livingentity) < 9.0D) {
									livingentity.applyKnockback(0.4F, MathHelper.sin(player.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(player.rotationYaw * 0.017453292F)));
									livingentity.attackEntityFrom(offhandAttack, f3);
								}
							}
						}

						if (target instanceof ServerPlayerEntity && target.velocityChanged) {
							((ServerPlayerEntity)target).connection.sendPacket(new SEntityVelocityPacket(target));
							target.velocityChanged = false;
							target.setMotion(vector3d);
						}

						if (flag2) {
							player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1.0F, 1.0F);
							player.onCriticalHit(target);
						}

						if (!flag2 && !flag3) {
							if (flag) {
								player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), 1.0F, 1.0F);
							} else {
								player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1.0F, 1.0F);
							}
						}

						if (enchantmentAffectsTargetBonus > 0.0F) {
							player.onEnchantmentCritical(target);
						}

						player.setLastAttackedEntity(target);
						if (target instanceof LivingEntity) {
							EnchantmentHelper.applyThornEnchantments((LivingEntity)target, player);
						}

						EnchantmentHelper.applyArthropodEnchantments(player, target);
						ItemStack itemstack1 = player.getHeldItemOffhand();
						Entity entity = target;
						if (target instanceof EnderDragonPartEntity) {
							entity = ((EnderDragonPartEntity)target).dragon;
						}

						if (!player.world.isRemote && !itemstack1.isEmpty() && entity instanceof LivingEntity) {
							ItemStack copy = itemstack1.copy();
							itemstack1.hitEntity((LivingEntity)entity, player);
							if (itemstack1.isEmpty()) {
								ForgeEventFactory.onPlayerDestroyItem(player, copy, Hand.OFF_HAND);
								player.setHeldItem(Hand.OFF_HAND, ItemStack.EMPTY);
							}
						}

						if (target instanceof LivingEntity) {
							float f5 = f4 - ((LivingEntity)target).getHealth();
							player.addStat(Stats.DAMAGE_DEALT, Math.round(f5 * 10.0F));
							if (j > 0) {
								target.setFire(j * 4);
							}

							if (player.world instanceof ServerWorld && f5 > 2.0F) {
								int k = (int)((double)f5 * 0.5D);
								((ServerWorld)player.world).spawnParticle(ParticleTypes.DAMAGE_INDICATOR, target.getPosX(), target.getPosYHeight(0.5D), target.getPosZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
							}
						}

						player.addExhaustion(0.1F);
					} else {
						player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0F, 1.0F);
						if (flag4) {
							target.extinguish();
						}
					}
				}
			}

		}
	}

}
