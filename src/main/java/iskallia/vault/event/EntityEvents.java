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
        if (event.getEntity().level.isClientSide
                || !(event.getEntity() instanceof MonsterEntity)
                || event.getEntity() instanceof EternalEntity
                || event.getEntity().level.dimension() != Vault.VAULT_KEY
                || event.getEntity().getTags().contains("VaultScaled")) return;

        MonsterEntity entity = (MonsterEntity) event.getEntity();
        VaultRaid raid = VaultRaidData.get((ServerWorld) entity.level).getAt(entity.blockPosition());
        if (raid == null) return;

        EntityScaler.scaleVault(entity, raid.level, new Random(), EntityScaler.Type.MOB);
        entity.getTags().add("VaultScaled");
        entity.setPersistenceRequired();
    }

    @SubscribeEvent
    public static void onEntityTick2(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity().level.isClientSide
                || !(event.getEntity() instanceof FighterEntity)) return;

        ((FighterEntity) event.getEntity()).setPersistenceRequired();
    }

    @SubscribeEvent
    public static void onEntityTick3(EntityEvent.EntityConstructing event) {
        if (event.getEntity().level.isClientSide
                || !(event.getEntity() instanceof AreaEffectCloudEntity)
                || event.getEntity().level.dimension() != Vault.VAULT_KEY) return;

        event.getEntity().getServer().tell(new TickDelayedTask(event.getEntity().getServer().getTickCount() + 2, () -> {
            if (!event.getEntity().getTags().contains("vault_door")) return;

            for (int ox = -1; ox <= 1; ox++) {
                for (int oz = -1; oz <= 1; oz++) {
                    BlockPos pos = event.getEntity().blockPosition().offset(ox, 0, oz);
                    BlockState state = event.getEntity().level.getBlockState(pos);

                    if (state.getBlock() == Blocks.IRON_DOOR) {
                        BlockState newState = VaultDoorBlock.VAULT_DOORS.get(event.getEntity().level.random.nextInt(VaultDoorBlock.VAULT_DOORS.size())).defaultBlockState()
                                .setValue(DoorBlock.FACING, state.getValue(DoorBlock.FACING))
                                .setValue(DoorBlock.OPEN, state.getValue(DoorBlock.OPEN))
                                .setValue(DoorBlock.HINGE, state.getValue(DoorBlock.HINGE))
                                .setValue(DoorBlock.POWERED, state.getValue(DoorBlock.POWERED))
                                .setValue(DoorBlock.HALF, state.getValue(DoorBlock.HALF));

                        PortalPlacer placer = new PortalPlacer((pos1, random, facing) -> null, (pos1, random, facing) -> Blocks.BEDROCK.defaultBlockState());
                        placer.place(event.getEntity().level, pos, state.getValue(DoorBlock.FACING).getCounterClockWise(), 1, 2);
                        placer.place(event.getEntity().level, pos.relative(state.getValue(DoorBlock.FACING).getOpposite()), state.getValue(DoorBlock.FACING).getCounterClockWise(), 1, 2);
                        placer.place(event.getEntity().level, pos.relative(state.getValue(DoorBlock.FACING).getOpposite(), 2), state.getValue(DoorBlock.FACING).getCounterClockWise(), 1, 2);
                        placer.place(event.getEntity().level, pos.relative(state.getValue(DoorBlock.FACING)), state.getValue(DoorBlock.FACING).getCounterClockWise(), 1, 2);
                        placer.place(event.getEntity().level, pos.relative(state.getValue(DoorBlock.FACING), 2), state.getValue(DoorBlock.FACING).getCounterClockWise(), 1, 2);

                        event.getEntity().level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 27);
                        event.getEntity().level.setBlock(pos, newState, 11);
                        event.getEntity().level.setBlock(pos.above(), newState.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), 11);

                        for (int x = -30; x <= 30; x++) {
                            for (int z = -30; z <= 30; z++) {
                                for (int y = -15; y <= 15; y++) {
                                    BlockPos c = pos.offset(x, y, z);
                                    BlockState s = event.getEntity().level.getBlockState(c);

                                    if (s.getBlock() == Blocks.PINK_WOOL) {
                                        event.getEntity().level.setBlock(c, Blocks.CHEST.defaultBlockState()
                                                .setValue(ChestBlock.FACING, Direction.from2DDataValue(event.getEntity().level.random.nextInt(4))), 2);
                                        TileEntity te = event.getEntity().level.getBlockEntity(c);

                                        if (te instanceof ChestTileEntity) {
                                            ((ChestTileEntity) te).setLootTable(Vault.id("chest/treasure"), 0L);
                                        }
                                    } else if (s.getBlock() == Blocks.PURPLE_WOOL) {
                                        event.getEntity().level.setBlock(c, Blocks.CHEST.defaultBlockState()
                                                .setValue(ChestBlock.FACING, Direction.from2DDataValue(event.getEntity().level.random.nextInt(4))), 2);
                                        TileEntity te = event.getEntity().level.getBlockEntity(c);

                                        if (te instanceof ChestTileEntity) {
                                            ((ChestTileEntity) te).setLootTable(Vault.id("chest/treasure_extra"), 0L);
                                        }
                                    } else if (s.getBlock() == Blocks.BEDROCK) {
                                        event.getEntity().level.setBlockAndUpdate(c, ModBlocks.VAULT_BEDROCK.defaultBlockState());
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
        if (event.getEntity().level.isClientSide
                || event.getEntity().level.dimension() != Vault.VAULT_KEY
                || !(event.getEntity() instanceof ArmorStandEntity)
            //|| !event.getEntity().getTags().contains("vault_obelisk")
        ) return;

        event.getEntityLiving().level.setBlockAndUpdate(event.getEntityLiving().blockPosition(), ModBlocks.OBELISK.defaultBlockState());
        event.getEntityLiving().remove();
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntity().level.isClientSide
                || event.getEntity().level.dimension() != Vault.VAULT_KEY
                || !event.getEntity().getTags().contains("VaultBoss")) return;

        ServerWorld world = (ServerWorld) event.getEntityLiving().level;
        VaultRaid raid = VaultRaidData.get(world).getAt(event.getEntity().blockPosition());

		if(raid != null) {
			raid.bosses.remove(event.getEntity().getUUID());
			if(raid.isFinalVault)return;

			raid.runForPlayers(world.getServer(), player -> {
				for(EquipmentSlotType slot: EquipmentSlotType.values()) {
					ItemStack stack = player.getItemBySlot(slot);
					float chance = ModAttributes.GEAR_LEVEL_CHANCE.getOrDefault(stack, 1.0F).getValue(stack);

					if(world.getRandom().nextFloat() < chance) {
						VaultGear.addLevel(stack, 1.0F);
					}
				}

				LootContext.Builder builder = (new LootContext.Builder(world)).withRandom(world.random)
						.withParameter(LootParameters.THIS_ENTITY, player)
						.withParameter(LootParameters.ORIGIN, event.getEntity().position())
						.withParameter(LootParameters.DAMAGE_SOURCE, event.getSource())
						.withOptionalParameter(LootParameters.KILLER_ENTITY, event.getSource().getEntity())
						.withOptionalParameter(LootParameters.DIRECT_KILLER_ENTITY, event.getSource().getDirectEntity())
						.withParameter(LootParameters.LAST_DAMAGE_PLAYER, player).withLuck(player.getLuck());

				LootContext ctx = builder.create(LootParameterSets.ENTITY);

				NonNullList<ItemStack> stacks = NonNullList.create();
				stacks.addAll(world.getServer().getLootTables().get(Vault.id("chest/boss")).getRandomItems(ctx));

				if(raid.playerBossName != null && !raid.playerBossName.isEmpty()) {
					stacks.add(LootStatueBlockItem.forVaultBoss(event.getEntity().getCustomName().getString(), StatueType.VAULT_BOSS.ordinal(), false));

					if(world.random.nextInt(4) != 0) {
						stacks.add(ItemTraderCore.generate(event.getEntity().getCustomName().getString(), 100, true, ItemTraderCore.CoreType.RAFFLE));
					}
				}

				int count = EternalsData.get(world).getTotalEternals();

				if(count != 0) {
					stacks.add(new ItemStack(ModItems.ETERNAL_SOUL, world.random.nextInt(count) + 1));
				}

				ItemStack crate = VaultCrateBlock.getCrateWithLoot(ModBlocks.VAULT_CRATE, stacks);

				event.getEntity().spawnAtLocation(crate);

				FireworkRocketEntity fireworks = new FireworkRocketEntity(world, event.getEntity().getX(),
						event.getEntity().getY(), event.getEntity().getZ(), new ItemStack(Items.FIREWORK_ROCKET));
				world.addFreshEntity(fireworks);
				//world.getServer().getLootTableManager().getLootTableFromLocation(Vault.id("chest/boss")).generate(ctx).forEach(stack -> {
				//	if(!player.addItemStackToInventory(stack)) {
				//		//TODO: drop the item at spawn
				//	}
				//});

				world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 1.0F, 1.0F);

				StringTextComponent title = new StringTextComponent("Vault Cleared!");
				title.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_ddd01e)));

				Entity entity = event.getEntity();

				IFormattableTextComponent entityName = entity instanceof FighterEntity
						? entity.getName().copy() : entity.getType().getDescription().copy();
				entityName.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_dd711e)));
				IFormattableTextComponent subtitle = new StringTextComponent(" is defeated.");
				subtitle.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_ddd01e)));

				StringTextComponent actionBar = new StringTextComponent("You'll be teleported back soon...");
				actionBar.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_ddd01e)));

				STitlePacket titlePacket = new STitlePacket(STitlePacket.Type.TITLE, title);
				STitlePacket subtitlePacket = new STitlePacket(STitlePacket.Type.SUBTITLE, entityName.copy().append(subtitle));

				player.connection.send(titlePacket);
				player.connection.send(subtitlePacket);
				player.displayClientMessage(actionBar, true);

				IFormattableTextComponent playerName = player.getDisplayName().copy();
				playerName.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_983198)));

				StringTextComponent text = new StringTextComponent(" cleared a Vault by defeating ");
				text.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_ffffff)));

				StringTextComponent punctuation = new StringTextComponent("!");
				punctuation.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_ffffff)));

				world.getServer().getPlayerList().broadcastMessage(
						playerName.append(text).append(entityName).append(punctuation),
						ChatType.CHAT,
						player.getUUID()
				);
			});
		}
	}

    @SubscribeEvent
    public static void onEntityDrops(LivingDropsEvent event) {
        if (event.getEntity().level.isClientSide) return;
        if (event.getEntity().level.dimension() != Vault.VAULT_KEY) return;
        if (event.getEntity() instanceof VaultGuardianEntity) return;
        if (event.getEntity() instanceof VaultFighterEntity) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEntitySpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getEntity().getCommandSenderWorld().dimension() == Vault.VAULT_KEY && !event.isSpawner()) {
            event.setCanceled(true);
        }
    }

	@SubscribeEvent
	public static void onPlayerDeathInVaults(LivingDeathEvent event) {
		LivingEntity entityLiving = event.getEntityLiving();

		if(entityLiving.level.isClientSide)return;
		if(!(entityLiving instanceof ServerPlayerEntity))return;
		if(entityLiving.level.dimension() != Vault.VAULT_KEY)return;

		ServerPlayerEntity player = (ServerPlayerEntity)entityLiving;
        Vector3d position = player.position();
		player.getLevel().playSound(null, position.x, position.y, position.z,
                ModSounds.TIMER_KILL_SFX, SoundCategory.MASTER, 0.75F, 1F);

		VaultRaid raid = VaultRaidData.get((ServerWorld)event.getEntity().level).getAt(player.blockPosition());
		if(raid == null)return;
		raid.finished = true;
	}

	@SubscribeEvent
	public static void onPlayerHurt(LivingDamageEvent event) {
		if(!(event.getEntity() instanceof PlayerEntity) || event.getEntity().level.isClientSide) return;
		ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();

		VaultRaid raid = VaultRaidData.get((ServerWorld) event.getEntity().level).getAt(player.blockPosition());
		if(raid == null) return;

		if(raid.won) {
			event.setCanceled(true);
		}

		if(raid.isFinalVault) {
			if(player.getHealth() - event.getAmount() <= 0) {
				player.getLevel().playSound(null, player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ(),
						ModSounds.TIMER_KILL_SFX, SoundCategory.MASTER, 0.75F, 1F);
				event.setCanceled(true);

				IFormattableTextComponent text = new StringTextComponent("");
				text.append(new StringTextComponent(player.getName().getString()).withStyle(TextFormatting.GREEN));
				text.append(new StringTextComponent(" has fallen, F."));
				player.getServer().getPlayerList().broadcastMessage(text, ChatType.CHAT, player.getUUID());


				raid.addSpectator(player);
			}
		}
	}

	@SubscribeEvent
	public static void onVaultGuardianDamage(LivingDamageEvent event) {
		LivingEntity entityLiving = event.getEntityLiving();

		if(entityLiving.level.isClientSide)return;

		if(entityLiving instanceof VaultGuardianEntity) {
			Entity trueSource = event.getSource().getEntity();
			if (trueSource instanceof LivingEntity) {
				LivingEntity attacker = (LivingEntity) trueSource;
				attacker.hurt(DamageSource.thorns(entityLiving), 20);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingHurtCrit(LivingHurtEvent event) {
		if(!(event.getSource().getEntity() instanceof LivingEntity))return;
		LivingEntity source = (LivingEntity)event.getSource().getEntity();
		if(source.level.isClientSide)return;

		if(source.getAttributes().hasAttribute(ModAttributes.CRIT_CHANCE)) {
			double chance = source.getAttributeValue(ModAttributes.CRIT_CHANCE);

			if(source.getAttributes().hasAttribute(ModAttributes.CRIT_MULTIPLIER)) {
				double multiplier = source.getAttributeValue(ModAttributes.CRIT_MULTIPLIER);

				if(source.level.random.nextDouble() < chance) {
					source.level.playSound(null, source.getX(), source.getY(), source.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, source.getSoundSource(), 1.0F, 1.0F);
					event.setAmount((float)(event.getAmount() * multiplier));
				}
			}
		}
	}

    @SubscribeEvent
    public static void onLivingHurtTp(LivingHurtEvent event) {
        if (event.getEntityLiving().level.isClientSide) return;

        boolean direct = event.getSource().getDirectEntity() == event.getSource().getEntity();

        if (direct && event.getEntityLiving().getAttributes().hasAttribute(ModAttributes.TP_CHANCE)) {
            double chance = event.getEntityLiving().getAttributeValue(ModAttributes.TP_CHANCE);

            if (event.getEntityLiving().getAttributes().hasAttribute(ModAttributes.TP_RANGE)) {
                double range = event.getEntityLiving().getAttributeValue(ModAttributes.TP_RANGE);

                if (event.getEntityLiving().level.random.nextDouble() < chance) {
                    for (int i = 0; i < 64; ++i) {
                        if (teleportRandomly(event.getEntityLiving(), range)) {
                            event.getEntityLiving().level.playSound(null,
                                    event.getEntityLiving().xo,
                                    event.getEntityLiving().yo,
                                    event.getEntityLiving().zo,
                                    ModSounds.BOSS_TP_SFX, event.getEntityLiving().getSoundSource(), 1.0F, 1.0F);
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        } else if (!direct && event.getEntityLiving().getAttributes().hasAttribute(ModAttributes.TP_INDIRECT_CHANCE)) {
            double chance = event.getEntityLiving().getAttributeValue(ModAttributes.TP_INDIRECT_CHANCE);

            if (event.getEntityLiving().getAttributes().hasAttribute(ModAttributes.TP_RANGE)) {
                double range = event.getEntityLiving().getAttributeValue(ModAttributes.TP_RANGE);

                if (event.getEntityLiving().level.random.nextDouble() < chance) {
                    for (int i = 0; i < 64; ++i) {
                        if (teleportRandomly(event.getEntityLiving(), range)) {
                            event.getEntityLiving().level.playSound(null,
                                    event.getEntityLiving().xo,
                                    event.getEntityLiving().yo,
                                    event.getEntityLiving().zo,
                                    ModSounds.BOSS_TP_SFX, event.getEntityLiving().getSoundSource(), 1.0F, 1.0F);
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    private static boolean teleportRandomly(LivingEntity entity, double range) {
        if (!entity.level.isClientSide() && entity.isAlive()) {
            double d0 = entity.getX() + (entity.level.random.nextDouble() - 0.5D) * (range * 2.0D);
            double d1 = entity.getY() + (entity.level.random.nextInt((int) (range * 2.0D)) - range);
            double d2 = entity.getZ() + (entity.level.random.nextDouble() - 0.5D) * (range * 2.0D);
            return entity.randomTeleport(d0, d1, d2, true);
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
