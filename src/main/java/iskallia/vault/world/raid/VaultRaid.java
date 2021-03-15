package iskallia.vault.world.raid;

import iskallia.vault.Vault;
import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.CrystalData;
import iskallia.vault.network.message.VaultInfoMessage;
import iskallia.vault.network.message.VaultRaidTickMessage;
import iskallia.vault.util.NetcodeUtils;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.world.data.VaultSetsData;
import iskallia.vault.world.gen.PortalPlacer;
import iskallia.vault.world.gen.structure.VaultStructure;
import iskallia.vault.world.raid.modifier.VaultModifiers;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class VaultRaid implements INBTSerializable<CompoundNBT> {

    public static final PortalPlacer PORTAL_PLACER = new PortalPlacer((pos, random, facing) -> {
        return ModBlocks.VAULT_PORTAL.getDefaultState().with(VaultPortalBlock.AXIS, facing.getAxis());
    }, (pos, random, facing) -> {
        Block[] blocks = {
                Blocks.BLACKSTONE, Blocks.BLACKSTONE, Blocks.POLISHED_BLACKSTONE,
                Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS
        };

        return blocks[random.nextInt(blocks.length)].getDefaultState();
    });

    public static final int REGION_SIZE = 1 << 11;

    public UUID playerId;
    public MutableBoundingBox box;
    public int level;
    public int rarity;
    public int sTickLeft;
    public int ticksLeft;
    public String playerBossName;

    public BlockPos start;
    public Direction facing;
    public boolean won;
    public boolean cannotExit;

    public VaultSpawner spawner = new VaultSpawner(this);
    public VaultModifiers modifiers = new VaultModifiers(this);
    public boolean finished = false;
    public int timer = 20 * 60;

    protected VaultRaid() {

    }

    public VaultRaid(ServerPlayerEntity player, MutableBoundingBox box, int level, int rarity, String playerBossName) {
        this.playerId = player.getUniqueID();
        this.box = box;
        this.level = level;
        this.rarity = rarity;
        this.playerBossName = playerBossName;

        this.sTickLeft = ModConfigs.VAULT_TIMER.getForLevel(this.level);
        this.ticksLeft = this.sTickLeft;
        this.sTickLeft += VaultSetsData.get(player.getServerWorld()).getExtraTime(this.playerId);
        this.ticksLeft += VaultSetsData.get(player.getServerWorld()).getExtraTime(this.playerId);
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public boolean isComplete() {
        return this.ticksLeft <= 0 || this.finished;
    }

    public void tick(ServerWorld world) {
        if (this.finished) return;

        this.runIfPresent(world.getServer(), player -> {
            this.modifiers.tick(world, player);
            this.ticksLeft--;
            this.syncTicksLeft(world.getServer());
        });

        if (this.ticksLeft <= 0) {
            if (this.won) {
                this.runIfPresent(world.getServer(), playerEntity -> {
                    this.teleportToStart(world, playerEntity);
                });
            } else {
                this.runIfPresent(world.getServer(), playerEntity -> {
                    playerEntity.sendMessage(new StringTextComponent("Time has run out!").mergeStyle(TextFormatting.GREEN), this.playerId);
                    playerEntity.inventory.func_234564_a_(stack -> true, -1, playerEntity.container.func_234641_j_());
                    playerEntity.openContainer.detectAndSendChanges();
                    playerEntity.container.onCraftMatrixChanged(playerEntity.inventory);
                    playerEntity.updateHeldItem();

                    DamageSource source = new DamageSource("vaultFailed").setDamageBypassesArmor().setDamageAllowedInCreativeMode();
                    playerEntity.attackEntityFrom(source, 100000000.0F);

                    this.finish(world, this.playerId);
                    this.finished = true;
                });
            }
        } else {
            this.runIfPresent(world.getServer(), player -> {
                if (this.ticksLeft + 20 < this.sTickLeft
                        && player.world.getDimensionKey() != Vault.VAULT_KEY) {
                    if (player.world.getDimensionKey() == World.OVERWORLD) {
                        this.finished = true;
                    } else {
                        this.ticksLeft = 1;
                    }
                } else {
                    this.spawner.tick(player);
                }
            });
        }

        this.timer--;
    }

    public void finish(ServerWorld server, UUID playerId) {
        Scoreboard scoreboard = server.getScoreboard();
        ScoreObjective objective = getOrCreateObjective(scoreboard, "VaultRarity", ScoreCriteria.DUMMY, ScoreCriteria.RenderType.INTEGER);
        scoreboard.removeObjectiveFromEntity(playerId.toString(), objective);
    }

    public static ScoreObjective getOrCreateObjective(Scoreboard scoreboard, String name, ScoreCriteria criteria, ScoreCriteria.RenderType renderType) {
        if (!scoreboard.func_197897_d().contains(name)) {
            scoreboard.addObjective(name, criteria, new StringTextComponent(name), renderType);
        }

        return scoreboard.getObjective(name);
    }

    public boolean runIfPresent(MinecraftServer server, Consumer<ServerPlayerEntity> action) {
        if (server == null) return false;
        ServerPlayerEntity player = server.getPlayerList().getPlayerByUUID(this.playerId);
        if (player == null) return false;
        action.accept(player);
        return true;
    }

    public void syncTicksLeft(MinecraftServer server) {
        NetcodeUtils.runIfPresent(server, this.playerId, player -> {
            ModNetwork.CHANNEL.sendTo(
                    new VaultRaidTickMessage(this.ticksLeft),
                    player.connection.netManager,
                    NetworkDirection.PLAY_TO_CLIENT
            );
        });
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putUniqueId("PlayerId", this.playerId);
        nbt.put("Box", this.box.toNBTTagIntArray());
        nbt.putInt("Level", this.level);
        nbt.putInt("Rarity", this.rarity);
        nbt.putInt("StartTicksLeft", this.sTickLeft);
        nbt.putInt("TicksLeft", this.ticksLeft);
        nbt.putString("PlayerBossName", this.playerBossName);
        nbt.putBoolean("Won", this.won);
        nbt.putBoolean("CannotExit", this.cannotExit);
        nbt.putInt("Spawner.MaxMobs", this.spawner.maxMobs);
        nbt.put("Modifiers", this.modifiers.serializeNBT());

        if (this.start != null) {
            CompoundNBT startNBT = new CompoundNBT();
            startNBT.putInt("x", this.start.getX());
            startNBT.putInt("y", this.start.getY());
            startNBT.putInt("z", this.start.getZ());
            nbt.put("Start", startNBT);
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.playerId = nbt.getUniqueId("PlayerId");
        this.box = new MutableBoundingBox(nbt.getIntArray("Box"));
        this.level = nbt.getInt("Level");
        this.rarity = nbt.getInt("Rarity");
        this.sTickLeft = nbt.getInt("StartTicksLeft");
        this.ticksLeft = nbt.getInt("TicksLeft");
        this.playerBossName = nbt.getString("PlayerBossName");
        this.won = nbt.getBoolean("Won");
        this.cannotExit = nbt.getBoolean("CannotExit");
        this.spawner.maxMobs = nbt.getInt("Spawner.MaxMobs");
        this.modifiers.deserializeNBT(nbt.getCompound("Modifiers"));

        if (nbt.contains("Start", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT startNBT = nbt.getCompound("Start");
            this.start = new BlockPos(startNBT.getInt("x"), startNBT.getInt("y"), startNBT.getInt("z"));
        }
    }

    public static VaultRaid fromNBT(CompoundNBT nbt) {
        VaultRaid raid = new VaultRaid();
        raid.deserializeNBT(nbt);
        return raid;
    }

    public void teleportToStart(ServerWorld world, ServerPlayerEntity player) {
        if (this.start == null) {
            Vault.LOGGER.warn("No vault start was found.");
            player.teleport(world, this.box.minX + this.box.getXSize() / 2.0F, 256,
                    this.box.minZ + this.box.getZSize() / 2.0F, player.rotationYaw, player.rotationPitch);
            return;
        }

        player.teleport(world, this.start.getX() + 0.5D, this.start.getY() + 0.2D, this.start.getZ() + 0.5D,
                this.facing == null ? world.getRandom().nextFloat() * 360.0F : this.facing.rotateY().getHorizontalAngle(), 0.0F);

        player.setOnGround(true);
    }

    public void start(ServerWorld world, ServerPlayerEntity player, ChunkPos chunkPos, CrystalData data) {
        this.spawner.init();

        loop:
        for (int x = -48; x < 48; x++) {
            for (int z = -48; z < 48; z++) {
                for (int y = 0; y < 48; y++) {
                    BlockPos pos = chunkPos.asBlockPos().add(x, VaultStructure.START_Y + y, z);
                    if (world.getBlockState(pos).getBlock() != Blocks.CRIMSON_PRESSURE_PLATE) continue;
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());

                    this.start = pos;

                    for (Direction direction : Direction.Plane.HORIZONTAL) {
                        int count = 1;

                        while (world.getBlockState(pos.offset(direction, count)).getBlock() == Blocks.WARPED_PRESSURE_PLATE) {
                            world.setBlockState(pos.offset(direction, count), Blocks.AIR.getDefaultState());
                            count++;
                        }

                        if (count != 1) {
                            PORTAL_PLACER.place(world, pos, this.facing = direction, count, count + 1);
                            break loop;
                        }
                    }
                }
            }
        }

        this.teleportToStart(world, player);
        player.func_242279_ag();

        Scoreboard scoreboard = player.getWorldScoreboard();
        ScoreObjective objective = getOrCreateObjective(scoreboard, "VaultRarity", ScoreCriteria.DUMMY, ScoreCriteria.RenderType.INTEGER);
        scoreboard.getOrCreateScore(player.getName().getString(), objective).setScorePoints(this.rarity);

        this.runIfPresent(world.getServer(), playerEntity -> {
            long seconds = (this.ticksLeft / 20) % 60;
            long minutes = ((this.ticksLeft / 20) / 60) % 60;
            String duration = String.format("%02d:%02d", minutes, seconds);

            StringTextComponent title = new StringTextComponent("The Vault");
            title.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_ddd01e)));

            IFormattableTextComponent subtitle = this.cannotExit
                    ? new StringTextComponent("No exit this time, ").append(player.getName()).append(new StringTextComponent("!"))
                    : new StringTextComponent("Good luck, ").append(player.getName()).append(new StringTextComponent("!"));
            subtitle.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_ddd01e)));

            StringTextComponent actionBar = new StringTextComponent("You have " + duration + " minutes to complete the raid.");
            actionBar.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_ddd01e)));

            STitlePacket titlePacket = new STitlePacket(STitlePacket.Type.TITLE, title);
            STitlePacket subtitlePacket = new STitlePacket(STitlePacket.Type.SUBTITLE, subtitle);

            playerEntity.connection.sendPacket(titlePacket);
            playerEntity.connection.sendPacket(subtitlePacket);
            playerEntity.sendStatusMessage(actionBar, true);

            //ModNetwork.CHANNEL.sendTo(
            //        new VaultBeginMessage(this.cannotExit),
            //        playerEntity.connection.netManager,
            //        NetworkDirection.PLAY_TO_CLIENT
            //);

            this.modifiers.generate(world.getRandom(), this.level, this.playerBossName != null && !this.playerBossName.isEmpty());
            data.apply(this, world.getRandom());
            this.modifiers.apply();

            ModNetwork.CHANNEL.sendTo(
                    new VaultInfoMessage(this),
                    playerEntity.connection.netManager,
                    NetworkDirection.PLAY_TO_CLIENT
            );

            IFormattableTextComponent playerName = player.getDisplayName().deepCopy();
            playerName.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_983198)));

            StringTextComponent text = new StringTextComponent("");
            AtomicBoolean startsWithVowel = new AtomicBoolean(false);

            this.modifiers.forEach((i, modifier) -> {
                StringTextComponent s = new StringTextComponent(modifier.getName());

                s.setStyle(Style.EMPTY.setColor(Color.fromInt(modifier.getColor()))
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(modifier.getDescription()))));

                text.append(s);

                if (i == 0) {
                    char c = modifier.getName().toLowerCase().charAt(0);
                    startsWithVowel.set(c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u');
                }

                if (i != this.modifiers.size() - 1) {
                    text.append(new StringTextComponent(", "));
                }
            });

            StringTextComponent prefix = new StringTextComponent(startsWithVowel.get() ? " entered an " : " entered a ");
            if (this.modifiers.size() != 0) text.append(new StringTextComponent(" "));

            String rarityName = VaultRarity.values()[this.rarity].name().toLowerCase();
            rarityName = rarityName.substring(0, 1).toUpperCase() + rarityName.substring(1);

            text.append(new StringTextComponent(rarityName).mergeStyle(VaultRarity.values()[this.rarity].color));
            text.append(new StringTextComponent(" Vault!"));
            prefix.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_ffffff)));
            text.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_ffffff)));

            world.getServer().getPlayerList().func_232641_a_(
                    playerName.append(prefix).append(text),
                    ChatType.CHAT,
                    playerId
            );

            Advancement advancement = player.getServer().getAdvancementManager().getAdvancement(Vault.id("root"));
            player.getAdvancements().grantCriterion(advancement, "entered_vault");
        });
    }

}
