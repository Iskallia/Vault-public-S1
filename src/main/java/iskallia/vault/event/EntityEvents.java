package iskallia.vault.event;

import iskallia.vault.Vault;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.block.VaultDoorBlock;
import iskallia.vault.block.item.LootStatueBlockItem;
import iskallia.vault.entity.*;
import iskallia.vault.init.*;
import iskallia.vault.item.ItemTraderCore;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.util.StatueType;
import iskallia.vault.world.data.EternalsData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.gen.PortalPlacer;
import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents {

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity().world.isRemote
                || !(event.getEntity() instanceof MonsterEntity)
                || event.getEntity() instanceof EternalEntity
                || event.getEntity().world.getDimensionKey() != Vault.VAULT_KEY
                || event.getEntity().getTags().contains("VaultScaled")) return;

        MonsterEntity entity = (MonsterEntity) event.getEntity();
        VaultRaid raid = VaultRaidData.get((ServerWorld) entity.world).getAt(entity.getPosition());
        if (raid == null) return;

        EntityScaler.scaleVault(entity, raid.level, new Random(), EntityScaler.Type.MOB);
        entity.getTags().add("VaultScaled");
        entity.enablePersistence();
    }

    @SubscribeEvent
    public static void onEntityTick2(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity().world.isRemote
                || !(event.getEntity() instanceof FighterEntity)) return;

        ((FighterEntity) event.getEntity()).enablePersistence();
    }

    @SubscribeEvent
    public static void onEntityTick3(EntityEvent.EntityConstructing event) {
        if (event.getEntity().world.isRemote
                || !(event.getEntity() instanceof AreaEffectCloudEntity)
                || event.getEntity().world.getDimensionKey() != Vault.VAULT_KEY) return;

        event.getEntity().getServer().enqueue(new TickDelayedTask(event.getEntity().getServer().getTickCounter() + 2, () -> {
            if (!event.getEntity().getTags().contains("vault_door")) return;

            for (int ox = -1; ox <= 1; ox++) {
                for (int oz = -1; oz <= 1; oz++) {
                    BlockPos pos = event.getEntity().getPosition().add(ox, 0, oz);
                    BlockState state = event.getEntity().world.getBlockState(pos);

                    if (state.getBlock() == Blocks.IRON_DOOR) {
                        BlockState newState = VaultDoorBlock.VAULT_DOORS.get(event.getEntity().world.rand.nextInt(VaultDoorBlock.VAULT_DOORS.size())).getDefaultState()
                                .with(DoorBlock.FACING, state.get(DoorBlock.FACING))
                                .with(DoorBlock.OPEN, state.get(DoorBlock.OPEN))
                                .with(DoorBlock.HINGE, state.get(DoorBlock.HINGE))
                                .with(DoorBlock.POWERED, state.get(DoorBlock.POWERED))
                                .with(DoorBlock.HALF, state.get(DoorBlock.HALF));

                        PortalPlacer placer = new PortalPlacer((pos1, random, facing) -> null, (pos1, random, facing) -> Blocks.BEDROCK.getDefaultState());
                        placer.place(event.getEntity().world, pos, state.get(DoorBlock.FACING).rotateYCCW(), 1, 2);
                        placer.place(event.getEntity().world, pos.offset(state.get(DoorBlock.FACING).getOpposite()), state.get(DoorBlock.FACING).rotateYCCW(), 1, 2);
                        placer.place(event.getEntity().world, pos.offset(state.get(DoorBlock.FACING).getOpposite(), 2), state.get(DoorBlock.FACING).rotateYCCW(), 1, 2);
                        placer.place(event.getEntity().world, pos.offset(state.get(DoorBlock.FACING)), state.get(DoorBlock.FACING).rotateYCCW(), 1, 2);
                        placer.place(event.getEntity().world, pos.offset(state.get(DoorBlock.FACING), 2), state.get(DoorBlock.FACING).rotateYCCW(), 1, 2);

                        event.getEntity().world.setBlockState(pos.up(), Blocks.AIR.getDefaultState(), 27);
                        event.getEntity().world.setBlockState(pos, newState, 11);
                        event.getEntity().world.setBlockState(pos.up(), newState.with(DoorBlock.HALF, DoubleBlockHalf.UPPER), 11);

                        for (int x = -30; x <= 30; x++) {
                            for (int z = -30; z <= 30; z++) {
                                for (int y = -15; y <= 15; y++) {
                                    BlockPos c = pos.add(x, y, z);
                                    BlockState s = event.getEntity().world.getBlockState(c);

                                    if (s.getBlock() == Blocks.PINK_WOOL) {
                                        event.getEntity().world.setBlockState(c, Blocks.CHEST.getDefaultState()
                                                .with(ChestBlock.FACING, Direction.byHorizontalIndex(event.getEntity().world.rand.nextInt(4))), 2);
                                        TileEntity te = event.getEntity().world.getTileEntity(c);

                                        if (te instanceof ChestTileEntity) {
                                            ((ChestTileEntity) te).setLootTable(Vault.id("chest/treasure"), 0L);
                                        }
                                    } else if (s.getBlock() == Blocks.PURPLE_WOOL) {
                                        event.getEntity().world.setBlockState(c, Blocks.CHEST.getDefaultState()
                                                .with(ChestBlock.FACING, Direction.byHorizontalIndex(event.getEntity().world.rand.nextInt(4))), 2);
                                        TileEntity te = event.getEntity().world.getTileEntity(c);

                                        if (te instanceof ChestTileEntity) {
                                            ((ChestTileEntity) te).setLootTable(Vault.id("chest/treasure_extra"), 0L);
                                        }
                                    } else if (s.getBlock() == Blocks.BEDROCK) {
                                        event.getEntity().world.setBlockState(c, ModBlocks.VAULT_BEDROCK.getDefaultState());
                                    }
                                }
                            }
                        }

                        event.getEntity().remove();
                    }
                }
            }
        }));
    }

    @SubscribeEvent
    public static void onEntityTick5(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity().world.isRemote
                || event.getEntity().world.getDimensionKey() != Vault.VAULT_KEY
                || !(event.getEntity() instanceof ArmorStandEntity)
            //|| !event.getEntity().getTags().contains("vault_obelisk")
        ) return;

        event.getEntityLiving().world.setBlockState(event.getEntityLiving().getPosition(), ModBlocks.OBELISK.getDefaultState());
        event.getEntityLiving().remove();
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntity().world.isRemote
                || event.getEntity().world.getDimensionKey() != Vault.VAULT_KEY
                || !event.getEntity().getTags().contains("VaultBoss")) return;

        ServerWorld world = (ServerWorld) event.getEntityLiving().world;
        VaultRaid raid = VaultRaidData.get(world).getAt(event.getEntity().getPosition());

		if(raid != null) {
			raid.bosses.remove(event.getEntity().getUniqueID());
			if(raid.isFinalVault)return;

			raid.runForPlayers(world.getServer(), player -> {
				for(EquipmentSlotType slot: EquipmentSlotType.values()) {
					ItemStack stack = player.getItemStackFromSlot(slot);
					float chance = ModAttributes.GEAR_LEVEL_CHANCE.getOrDefault(stack, 1.0F).getValue(stack);

					if(world.getRandom().nextFloat() < chance) {
						VaultGear.addLevel(stack, 1.0F);
					}
				}

				LootContext.Builder builder = (new LootContext.Builder(world)).withRandom(world.rand)
						.withParameter(LootParameters.THIS_ENTITY, player)
						.withParameter(LootParameters.field_237457_g_, event.getEntity().getPositionVec())
						.withParameter(LootParameters.DAMAGE_SOURCE, event.getSource())
						.withNullableParameter(LootParameters.KILLER_ENTITY, event.getSource().getTrueSource())
						.withNullableParameter(LootParameters.DIRECT_KILLER_ENTITY, event.getSource().getImmediateSource())
						.withParameter(LootParameters.LAST_DAMAGE_PLAYER, player).withLuck(player.getLuck());

				LootContext ctx = builder.build(LootParameterSets.ENTITY);

				NonNullList<ItemStack> stacks = NonNullList.create();
				stacks.addAll(world.getServer().getLootTableManager().getLootTableFromLocation(Vault.id("chest/boss")).generate(ctx));

				if(raid.playerBossName != null && !raid.playerBossName.isEmpty()) {
					stacks.add(LootStatueBlockItem.forVaultBoss(event.getEntity().getCustomName().getString(), StatueType.VAULT_BOSS.ordinal(), false));

					if(world.rand.nextInt(4) != 0) {
						stacks.add(ItemTraderCore.generate(event.getEntity().getCustomName().getString(), 100, true, ItemTraderCore.CoreType.RAFFLE));
					}
				}

				int count = EternalsData.get(world).getTotalEternals();

				if(count != 0) {
					stacks.add(new ItemStack(ModItems.ETERNAL_SOUL, world.rand.nextInt(count) + 1));
				}

				ItemStack crate = VaultCrateBlock.getCrateWithLoot(ModBlocks.VAULT_CRATE, stacks);

				event.getEntity().entityDropItem(crate);

				FireworkRocketEntity fireworks = new FireworkRocketEntity(world, event.getEntity().getPosX(),
						event.getEntity().getPosY(), event.getEntity().getPosZ(), new ItemStack(Items.FIREWORK_ROCKET));
				world.addEntity(fireworks);
				//world.getServer().getLootTableManager().getLootTableFromLocation(Vault.id("chest/boss")).generate(ctx).forEach(stack -> {
				//	if(!player.addItemStackToInventory(stack)) {
				//		//TODO: drop the item at spawn
				//	}
				//});

				raid.won = true;
				raid.ticksLeft = 20 * 20;
				world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 1.0F, 1.0F);

				StringTextComponent title = new StringTextComponent("Vault Cleared!");
				title.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_ddd01e)));

				Entity entity = event.getEntity();

				IFormattableTextComponent entityName = entity instanceof FighterEntity
						? entity.getName().deepCopy() : entity.getType().getName().deepCopy();
				entityName.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_dd711e)));
				IFormattableTextComponent subtitle = new StringTextComponent(" is defeated.");
				subtitle.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_ddd01e)));

				StringTextComponent actionBar = new StringTextComponent("You'll be teleported back soon...");
				actionBar.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_ddd01e)));

				STitlePacket titlePacket = new STitlePacket(STitlePacket.Type.TITLE, title);
				STitlePacket subtitlePacket = new STitlePacket(STitlePacket.Type.SUBTITLE, entityName.deepCopy().append(subtitle));

				player.connection.sendPacket(titlePacket);
				player.connection.sendPacket(subtitlePacket);
				player.sendStatusMessage(actionBar, true);

				IFormattableTextComponent playerName = player.getDisplayName().deepCopy();
				playerName.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_983198)));

				StringTextComponent text = new StringTextComponent(" cleared a Vault by defeating ");
				text.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_ffffff)));

				StringTextComponent punctuation = new StringTextComponent("!");
				punctuation.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_ffffff)));

				world.getServer().getPlayerList().func_232641_a_(
						playerName.append(text).append(entityName).append(punctuation),
						ChatType.CHAT,
						player.getUniqueID()
				);
			});
		}
	}

    @SubscribeEvent
    public static void onEntityDrops(LivingDropsEvent event) {
        if (event.getEntity().world.isRemote) return;
        if (event.getEntity().world.getDimensionKey() != Vault.VAULT_KEY) return;
        if (event.getEntity() instanceof VaultGuardianEntity) return;
        if (event.getEntity() instanceof VaultFighterEntity) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEntitySpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getEntity().getEntityWorld().getDimensionKey() == Vault.VAULT_KEY && !event.isSpawner()) {
            event.setCanceled(true);
        }
    }

	@SubscribeEvent
	public static void onPlayerDeathInVaults(LivingDeathEvent event) {
		LivingEntity entityLiving = event.getEntityLiving();

		if(entityLiving.world.isRemote)return;
		if(!(entityLiving instanceof ServerPlayerEntity))return;
		if(entityLiving.world.getDimensionKey() != Vault.VAULT_KEY)return;

		ServerPlayerEntity player = (ServerPlayerEntity)entityLiving;
        Vector3d position = player.getPositionVec();
		player.getServerWorld().playSound(null, position.x, position.y, position.z,
                ModSounds.TIMER_KILL_SFX, SoundCategory.MASTER, 0.75F, 1F);

		VaultRaid raid = VaultRaidData.get((ServerWorld)event.getEntity().world).getAt(player.getPosition());
		if(raid == null)return;
		raid.finished = true;
	}

	@SubscribeEvent
	public static void onPlayerHurt(LivingDamageEvent event) {
		if(!(event.getEntity() instanceof PlayerEntity) || event.getEntity().world.isRemote) return;
		ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();

		VaultRaid raid = VaultRaidData.get((ServerWorld) event.getEntity().world).getAt(player.getPosition());
		if(raid == null) return;

		if(raid.won) {
			event.setCanceled(true);
		}

		if(raid.isFinalVault) {
			if(player.getHealth() - event.getAmount() <= 0) {
				player.getServerWorld().playSound(null, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(),
						ModSounds.TIMER_KILL_SFX, SoundCategory.MASTER, 0.75F, 1F);
				event.setCanceled(true);

				IFormattableTextComponent text = new StringTextComponent("");
				text.append(new StringTextComponent(player.getName().getString()).mergeStyle(TextFormatting.GREEN));
				text.append(new StringTextComponent(" has fallen, F."));
				player.getServer().getPlayerList().func_232641_a_(text, ChatType.CHAT, player.getUniqueID());


				raid.addSpectator(player);
			}
		}
	}

	@SubscribeEvent
	public static void onVaultGuardianDamage(LivingDamageEvent event) {
		LivingEntity entityLiving = event.getEntityLiving();

		if(entityLiving.world.isRemote)return;

		if(entityLiving instanceof VaultGuardianEntity) {
			Entity trueSource = event.getSource().getTrueSource();
			if (trueSource instanceof LivingEntity) {
				LivingEntity attacker = (LivingEntity) trueSource;
				attacker.attackEntityFrom(DamageSource.causeThornsDamage(entityLiving), 20);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingHurtCrit(LivingHurtEvent event) {
		if(!(event.getSource().getTrueSource() instanceof LivingEntity))return;
		LivingEntity source = (LivingEntity)event.getSource().getTrueSource();
		if(source.world.isRemote)return;

		if(source.getAttributeManager().hasAttributeInstance(ModAttributes.CRIT_CHANCE)) {
			double chance = source.getAttributeValue(ModAttributes.CRIT_CHANCE);

			if(source.getAttributeManager().hasAttributeInstance(ModAttributes.CRIT_MULTIPLIER)) {
				double multiplier = source.getAttributeValue(ModAttributes.CRIT_MULTIPLIER);

				if(source.world.rand.nextDouble() < chance) {
					source.world.playSound(null, source.getPosX(), source.getPosY(), source.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, source.getSoundCategory(), 1.0F, 1.0F);
					event.setAmount((float)(event.getAmount() * multiplier));
				}
			}
		}
	}

    @SubscribeEvent
    public static void onLivingHurtTp(LivingHurtEvent event) {
        if (event.getEntityLiving().world.isRemote) return;

        boolean direct = event.getSource().getImmediateSource() == event.getSource().getTrueSource();

        if (direct && event.getEntityLiving().getAttributeManager().hasAttributeInstance(ModAttributes.TP_CHANCE)) {
            double chance = event.getEntityLiving().getAttributeValue(ModAttributes.TP_CHANCE);

            if (event.getEntityLiving().getAttributeManager().hasAttributeInstance(ModAttributes.TP_RANGE)) {
                double range = event.getEntityLiving().getAttributeValue(ModAttributes.TP_RANGE);

                if (event.getEntityLiving().world.rand.nextDouble() < chance) {
                    for (int i = 0; i < 64; ++i) {
                        if (teleportRandomly(event.getEntityLiving(), range)) {
                            event.getEntityLiving().world.playSound(null,
                                    event.getEntityLiving().prevPosX,
                                    event.getEntityLiving().prevPosY,
                                    event.getEntityLiving().prevPosZ,
                                    ModSounds.BOSS_TP_SFX, event.getEntityLiving().getSoundCategory(), 1.0F, 1.0F);
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        } else if (!direct && event.getEntityLiving().getAttributeManager().hasAttributeInstance(ModAttributes.TP_INDIRECT_CHANCE)) {
            double chance = event.getEntityLiving().getAttributeValue(ModAttributes.TP_INDIRECT_CHANCE);

            if (event.getEntityLiving().getAttributeManager().hasAttributeInstance(ModAttributes.TP_RANGE)) {
                double range = event.getEntityLiving().getAttributeValue(ModAttributes.TP_RANGE);

                if (event.getEntityLiving().world.rand.nextDouble() < chance) {
                    for (int i = 0; i < 64; ++i) {
                        if (teleportRandomly(event.getEntityLiving(), range)) {
                            event.getEntityLiving().world.playSound(null,
                                    event.getEntityLiving().prevPosX,
                                    event.getEntityLiving().prevPosY,
                                    event.getEntityLiving().prevPosZ,
                                    ModSounds.BOSS_TP_SFX, event.getEntityLiving().getSoundCategory(), 1.0F, 1.0F);
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    private static boolean teleportRandomly(LivingEntity entity, double range) {
        if (!entity.world.isRemote() && entity.isAlive()) {
            double d0 = entity.getPosX() + (entity.world.rand.nextDouble() - 0.5D) * (range * 2.0D);
            double d1 = entity.getPosY() + (entity.world.rand.nextInt((int) (range * 2.0D)) - range);
            double d2 = entity.getPosZ() + (entity.world.rand.nextDouble() - 0.5D) * (range * 2.0D);
            return entity.attemptTeleport(d0, d1, d2, true);
        }

        return false;
    }

    @SubscribeEvent
    public static void onEntityDestroy(LivingDestroyBlockEvent event) {
        if (event.getState().getBlock() instanceof VaultDoorBlock) {
            event.setCanceled(true);
        }
    }

}
