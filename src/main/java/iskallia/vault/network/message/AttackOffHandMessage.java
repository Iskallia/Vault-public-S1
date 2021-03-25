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
			ItemStack offhand = player.getOffhandItem();
			if(offhand.isEmpty())return;

			int level = PlayerVaultStatsData.get((ServerWorld)player.level).getVaultStats(player).getVaultLevel();

			if(ModAttributes.MIN_VAULT_LEVEL.exists(offhand)
					&& level < ModAttributes.MIN_VAULT_LEVEL.get(offhand).get().getValue(offhand)) {
				return;
			}

			Entity target = player.level.getEntity(packet.entityId);
			if(target == null)return;
			float reach = 6.0F;

			// This is done to mitigate the difference between the render view entity's position
			// that is checked in the client to the server player entity's position
			float renderViewEntityOffsetFromPlayerMitigator = 0.2F;
			reach += renderViewEntityOffsetFromPlayerMitigator;

			double distanceSquared = player.distanceToSqr(target);

			if(reach * reach >= distanceSquared) {
				attackTargetEntityWithCurrentOffhandItem(player, target);
			}

			swingArm(player, Hand.OFF_HAND);
		});
	}

	public static void swingArm(ServerPlayerEntity playerEntity, Hand hand) {
		ItemStack stack = playerEntity.getItemInHand(hand);

		if(stack.isEmpty() || !stack.onEntitySwing(playerEntity)) {
			if(!playerEntity.swinging || playerEntity.swingTime >= getArmSwingAnimationEnd(playerEntity) / 2 || playerEntity.swingTime < 0) {
				playerEntity.swingTime = -1;
				playerEntity.swinging = true;
				playerEntity.swingingArm = hand;

				if(playerEntity.level instanceof ServerWorld) {
					SAnimateHandPacket sanimatehandpacket = new SAnimateHandPacket(playerEntity, hand == Hand.MAIN_HAND ? 0 : 3);
					ServerChunkProvider serverchunkprovider = ((ServerWorld)playerEntity.level).getChunkSource();

					serverchunkprovider.broadcast(playerEntity, sanimatehandpacket);
				}
			}

		}
	}

	private static int getArmSwingAnimationEnd(LivingEntity livingEntity) {
		if(EffectUtils.hasDigSpeed(livingEntity)) {
			return 6 - (1 + EffectUtils.getDigSpeedAmplification(livingEntity));
		} else {
			return livingEntity.hasEffect(Effects.DIG_SLOWDOWN) ? 6 + (1 + livingEntity.getEffect(Effects.DIG_SLOWDOWN).getAmplifier()) * 2 : 6;
		}
	}

	public static void attackTargetEntityWithCurrentOffhandItem(ServerPlayerEntity serverPlayerEntity, Entity target) {
		if (serverPlayerEntity.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
			serverPlayerEntity.setCamera(target);
		} else {
			attackTargetEntityWithCurrentOffhandItemAsSuper(serverPlayerEntity, target);
		}
	}

	public static void attackTargetEntityWithCurrentOffhandItemAsSuper(PlayerEntity player, Entity target) {
		if(ForgeHooks.onPlayerAttackTarget(player, target)) {
			if(target.isAttackable() && !target.skipAttackInteraction(player)) {
				// get attack damage attribute value
				float attackDamage = (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE);
				float enchantmentAffectsTargetBonus;
				if (target instanceof LivingEntity) {
					enchantmentAffectsTargetBonus = EnchantmentHelper.getDamageBonus(player.getOffhandItem(), ((LivingEntity)target).getMobType());
				} else {
					enchantmentAffectsTargetBonus = EnchantmentHelper.getDamageBonus(player.getOffhandItem(), CreatureAttribute.UNDEFINED);
				}

				float cooledAttackStrength = player.getAttackStrengthScale(0.5F);
				attackDamage *= 0.2F + cooledAttackStrength * cooledAttackStrength * 0.8F;
				enchantmentAffectsTargetBonus *= cooledAttackStrength;
				//player.resetCooldown();
				if (attackDamage > 0.0F || enchantmentAffectsTargetBonus > 0.0F) {
					boolean flag = cooledAttackStrength > 0.9F;
					boolean flag1 = false;
					int i = 0;
					i = i + EnchantmentHelper.getKnockbackBonus(player);
					if (player.isSprinting() && flag) {
						player.level.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, player.getSoundSource(), 1.0F, 1.0F);
						++i;
						flag1 = true;
					}

					boolean flag2 = flag && player.fallDistance > 0.0F && !player.isOnGround() && !player.onClimbable() && !player.isInWater() && !player.hasEffect(Effects.BLINDNESS) && !player.isPassenger() && target instanceof LivingEntity;
					flag2 = flag2 && !player.isSprinting();
					CriticalHitEvent hitResult = ForgeHooks.getCriticalHit(player, target, flag2, flag2 ? 1.5F : 1.0F);
					flag2 = hitResult != null;
					if (flag2) {
						attackDamage *= hitResult.getDamageModifier();
					}

					attackDamage += enchantmentAffectsTargetBonus;
					boolean flag3 = false;
					double d0 = player.walkDist - player.walkDistO;
					if (flag && !flag2 && !flag1 && player.isOnGround() && d0 < (double)player.getSpeed()) {
						ItemStack itemstack = player.getItemInHand(Hand.OFF_HAND);
						if (itemstack.getItem() instanceof SwordItem) {
							flag3 = true;
						}
					}

					float f4 = 0.0F;
					boolean flag4 = false;
					int j = EnchantmentHelper.getFireAspect(player);
					if (target instanceof LivingEntity) {
						f4 = ((LivingEntity)target).getHealth();
						if (j > 0 && !target.isOnFire()) {
							flag4 = true;
							target.setSecondsOnFire(1);
						}
					}

					Vector3d vector3d = target.getDeltaMovement();
					DamageSource offhandAttack = DamageSource.playerAttack(player);
					boolean flag5 = target.hurt(offhandAttack, attackDamage);
					if (flag5) {
						if (i > 0) {
							if (target instanceof LivingEntity) {
								((LivingEntity)target).knockback((float)i * 0.5F, (double) MathHelper.sin(player.yRot * 0.017453292F), (double)(-MathHelper.cos(player.yRot * 0.017453292F)));
							} else {
								target.push(-MathHelper.sin(player.yRot * 0.017453292F) * (float)i * 0.5F, 0.1D, (double)(MathHelper.cos(player.yRot * 0.017453292F) * (float)i * 0.5F));
							}

							player.setDeltaMovement(player.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
							player.setSprinting(false);
						}

						if (flag3) {
							float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * attackDamage;
							Iterator<LivingEntity> var19 = player.level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(1.0D, 0.25D, 1.0D)).iterator();

							label174:
							while(true) {
								LivingEntity livingentity;
								do {
									do {
										do {
											do {
												if (!var19.hasNext()) {
													player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
													player.sweepAttack();
													break label174;
												}

												livingentity = var19.next();
											} while(livingentity == player);
										} while(livingentity == target);
									} while(player.isAlliedTo(livingentity));
								} while(livingentity instanceof ArmorStandEntity && ((ArmorStandEntity)livingentity).isMarker());

								if (player.distanceToSqr(livingentity) < 9.0D) {
									livingentity.knockback(0.4F, MathHelper.sin(player.yRot * 0.017453292F), (double)(-MathHelper.cos(player.yRot * 0.017453292F)));
									livingentity.hurt(offhandAttack, f3);
								}
							}
						}

						if (target instanceof ServerPlayerEntity && target.hurtMarked) {
							((ServerPlayerEntity)target).connection.send(new SEntityVelocityPacket(target));
							target.hurtMarked = false;
							target.setDeltaMovement(vector3d);
						}

						if (flag2) {
							player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, player.getSoundSource(), 1.0F, 1.0F);
							player.crit(target);
						}

						if (!flag2 && !flag3) {
							if (flag) {
								player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, player.getSoundSource(), 1.0F, 1.0F);
							} else {
								player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, player.getSoundSource(), 1.0F, 1.0F);
							}
						}

						if (enchantmentAffectsTargetBonus > 0.0F) {
							player.magicCrit(target);
						}

						player.setLastHurtMob(target);
						if (target instanceof LivingEntity) {
							EnchantmentHelper.doPostHurtEffects((LivingEntity)target, player);
						}

						EnchantmentHelper.doPostDamageEffects(player, target);
						ItemStack itemstack1 = player.getOffhandItem();
						Entity entity = target;
						if (target instanceof EnderDragonPartEntity) {
							entity = ((EnderDragonPartEntity)target).parentMob;
						}

						if (!player.level.isClientSide && !itemstack1.isEmpty() && entity instanceof LivingEntity) {
							ItemStack copy = itemstack1.copy();
							itemstack1.hurtEnemy((LivingEntity)entity, player);
							if (itemstack1.isEmpty()) {
								ForgeEventFactory.onPlayerDestroyItem(player, copy, Hand.OFF_HAND);
								player.setItemInHand(Hand.OFF_HAND, ItemStack.EMPTY);
							}
						}

						if (target instanceof LivingEntity) {
							float f5 = f4 - ((LivingEntity)target).getHealth();
							player.awardStat(Stats.DAMAGE_DEALT, Math.round(f5 * 10.0F));
							if (j > 0) {
								target.setSecondsOnFire(j * 4);
							}

							if (player.level instanceof ServerWorld && f5 > 2.0F) {
								int k = (int)((double)f5 * 0.5D);
								((ServerWorld)player.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5D), target.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
							}
						}

						player.causeFoodExhaustion(0.1F);
					} else {
						player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, player.getSoundSource(), 1.0F, 1.0F);
						if (flag4) {
							target.clearFire();
						}
					}
				}
			}

		}
	}

}
