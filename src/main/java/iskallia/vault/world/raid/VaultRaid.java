package iskallia.vault.world.raid;

import iskallia.vault.Vault;
import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.CrystalData;
import iskallia.vault.network.message.VaultInfoMessage;
import iskallia.vault.network.message.VaultRaidTickMessage;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.util.NetcodeUtils;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.VaultSetsData;
import iskallia.vault.world.gen.PortalPlacer;
import iskallia.vault.world.gen.structure.VaultStructure;
import iskallia.vault.world.raid.modifier.VaultModifiers;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
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
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class VaultRaid implements INBTSerializable<CompoundNBT> {

    public static final PortalPlacer PORTAL_PLACER = new PortalPlacer((pos, random, facing) -> {
        return ModBlocks.VAULT_PORTAL.defaultBlockState().setValue(VaultPortalBlock.AXIS, facing.getAxis());
    }, (pos, random, facing) -> {
        Block[] blocks = {
                Blocks.BLACKSTONE, Blocks.BLACKSTONE, Blocks.POLISHED_BLACKSTONE,
                Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS
        };

        return blocks[random.nextInt(blocks.length)].defaultBlockState();
    });

    public static final PortalPlacer FINAL_PORTAL_PLACER = new PortalPlacer((pos, random, facing) -> {
        return ModBlocks.FINAL_VAULT_PORTAL.defaultBlockState().setValue(VaultPortalBlock.AXIS, facing.getAxis());
    }, (pos, random, facing) -> {
        Block[] blocks = {
                Blocks.BLACKSTONE, Blocks.BLACKSTONE, Blocks.POLISHED_BLACKSTONE,
                Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS
        };

        return blocks[random.nextInt(blocks.length)].defaultBlockState();
    });

    public static final DamageSource VAULT_FAILED = new DamageSource("vaultFailed").bypassArmor().bypassInvul();

    public static final int REGION_SIZE = 1 << 11;

    public List<UUID> playerIds;
    public List<UUID> spectatorIds; //Only used initially.
    public List<Spectator> spectators = new ArrayList<>();
    public List<UUID> bosses = new ArrayList<>();

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
    public boolean summonedBoss;

    public VaultSpawner spawner = new VaultSpawner(this);
    public VaultModifiers modifiers = new VaultModifiers(this);
    public boolean finished = false;
    public int timer = 20 * 60;

    public boolean isFinalVault; //This is disgusting but...

    protected VaultRaid() {

    }

    public VaultRaid(List<ServerPlayerEntity> players, List<ServerPlayerEntity> spectators,
                     MutableBoundingBox box, int level, int rarity, String playerBossName) {
        this.playerIds = players.stream().map(Entity::getUUID).collect(Collectors.toList());
        this.spectatorIds = spectators.stream().map(Entity::getUUID).collect(Collectors.toList());
        this.box = box;
        this.level = level;
        this.rarity = rarity;
        this.playerBossName = playerBossName;

        this.sTickLeft = ModConfigs.VAULT_TIMER.getForLevel(this.level);
        this.ticksLeft = this.sTickLeft;

        players.stream()
                .map(player -> VaultSetsData.get(player.getLevel()).getExtraTime(player.getUUID()))
                .max(Integer::compare)
                .ifPresent(extraTime -> {
                    this.sTickLeft += extraTime;
                    this.ticksLeft += extraTime;
                });
    }

    public List<UUID> getPlayerIds() {
        return this.playerIds;
    }

    public List<Spectator> getSpectators() {
        return this.spectators;
    }

    public boolean isComplete() {
        return this.ticksLeft <= 0 || this.finished;
    }

    public void tick(ServerWorld world) {
        if (this.finished) return;

        if (!this.won && this.summonedBoss && this.bosses.isEmpty()) {
            this.won = true;
            this.ticksLeft = 20 * 20;
        }

        if (this.playerIds.size() == 1) {
            this.runForPlayers(world.getServer(), player -> {

                this.modifiers.tick(world, player);
                this.ticksLeft--;
                this.syncTicksLeft(world.getServer());

            });
        } else {
            this.ticksLeft--;
            this.syncTicksLeft(world.getServer());
        }

        if (this.ticksLeft <= 0) {
            if (this.won) {
                this.onFinishRaid(world);
            } else {
                this.runForAll(world.getServer(), player -> {
                    player.sendMessage(new StringTextComponent("Time has run out!").withStyle(TextFormatting.GREEN), player.getUUID());
                    player.inventory.clearOrCountMatchingItems(stack -> true, -1, player.inventoryMenu.getCraftSlots());
                    player.containerMenu.broadcastChanges();
                    player.inventoryMenu.slotsChanged(player.inventory);
                    player.broadcastCarriedItem();
                    player.hurt(VAULT_FAILED, 100000000.0F);
                });

                this.onFinishRaid(world);
                this.finished = true;
            }
        } else {
            this.runForPlayers(world.getServer(), player -> {
                if (this.ticksLeft + 20 < this.sTickLeft
                        && player.level.dimension() != Vault.VAULT_KEY) {
                    if (player.level.dimension() == World.OVERWORLD) {
                        //This triggers when you go through the portal or TP out.
                        this.onFinishRaid(world);
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

    private void onFinishRaid(ServerWorld world) {
        this.finished = true;

        this.runForAll(world.getServer(), player -> {
            if (!player.removed && player.level.dimension() == Vault.VAULT_KEY) {
                this.teleportToStart(world, player);
            }

            this.finish(world, player.getUUID());

            List<UUID> list = this.spectators.stream().map(spectator -> spectator.uuid).collect(Collectors.toList());

            if (!player.removed && !list.contains(player.getUUID())) {
                float range = ModConfigs.VAULT_GENERAL.VAULT_EXIT_TNL_MAX - ModConfigs.VAULT_GENERAL.VAULT_EXIT_TNL_MIN;
                float tnl = ModConfigs.VAULT_GENERAL.VAULT_EXIT_TNL_MIN + world.random.nextFloat() * range;

                PlayerVaultStatsData statsData = PlayerVaultStatsData.get(world);
                PlayerVaultStats stats = statsData.getVaultStats(player);
                statsData.addVaultExp(player, (int) (stats.getTnl() * tnl));
            }
        });

        this.finishSpectators(world);
    }

    public void addBoss(LivingEntity entity) {
        this.bosses.add(entity.getUUID());
        this.summonedBoss = true;
    }

    private void finishSpectators(ServerWorld world) {
        this.spectators.forEach(spectator -> spectator.finish(world, this));
    }

    public void finish(ServerWorld server, UUID playerId) {
        Scoreboard scoreboard = server.getScoreboard();
        ScoreObjective objective = getOrCreateObjective(scoreboard, "VaultRarity", ScoreCriteria.DUMMY, ScoreCriteria.RenderType.INTEGER);
        scoreboard.resetPlayerScore(playerId.toString(), objective);
    }

    public static ScoreObjective getOrCreateObjective(Scoreboard scoreboard, String name, ScoreCriteria criteria, ScoreCriteria.RenderType renderType) {
        if (!scoreboard.getObjectiveNames().contains(name)) {
            scoreboard.addObjective(name, criteria, new StringTextComponent(name), renderType);
        }

        return scoreboard.getObjective(name);
    }

    public void runForPlayers(MinecraftServer server, Consumer<ServerPlayerEntity> action) {
        for (UUID uuid : this.playerIds) {
            if (server == null) return;
            ServerPlayerEntity player = server.getPlayerList().getPlayer(uuid);
            if (player == null) return;
            action.accept(player);
        }
    }

    public void runForSpectators(MinecraftServer server, Consumer<ServerPlayerEntity> action) {
        this.spectators.stream().map(spectator -> spectator.uuid).forEach(uuid -> {
            if (server == null) return;
            ServerPlayerEntity player = server.getPlayerList().getPlayer(uuid);
            if (player == null) return;
            action.accept(player);
        });
    }

    public void runForAll(MinecraftServer server, Consumer<ServerPlayerEntity> action) {
        this.runForPlayers(server, action);
        this.runForSpectators(server, action);
    }

    public void syncTicksLeft(MinecraftServer server) {
        this.runForAll(server, player -> {
            ModNetwork.CHANNEL.sendTo(
                    new VaultRaidTickMessage(this.ticksLeft),
                    player.connection.connection,
                    NetworkDirection.PLAY_TO_CLIENT
            );
        });
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        if (this.playerIds.size() == 1) {
            nbt.putUUID("PlayerId", this.playerIds.get(0));
        } else {
            ListNBT playerIdsList = new ListNBT();
            this.playerIds.forEach(uuid -> playerIdsList.add(NBTUtil.createUUID(uuid)));
            nbt.put("PlayerIds", playerIdsList);
        }

        nbt.put("Box", this.box.createTag());
        nbt.putInt("Level", this.level);
        nbt.putInt("Rarity", this.rarity);
        nbt.putInt("StartTicksLeft", this.sTickLeft);
        nbt.putInt("TicksLeft", this.ticksLeft);
        nbt.putString("PlayerBossName", this.playerBossName);
        nbt.putBoolean("Won", this.won);
        nbt.putBoolean("CannotExit", this.cannotExit);
        nbt.putBoolean("SummonedBoss", this.summonedBoss);
        nbt.putInt("Spawner.MaxMobs", this.spawner.maxMobs);
        nbt.put("Modifiers", this.modifiers.serializeNBT());

        if (this.start != null) {
            CompoundNBT startNBT = new CompoundNBT();
            startNBT.putInt("x", this.start.getX());
            startNBT.putInt("y", this.start.getY());
            startNBT.putInt("z", this.start.getZ());
            nbt.put("Start", startNBT);
        }

        ListNBT spectatorsList = new ListNBT();
        this.spectators.forEach(spectator -> spectatorsList.add(spectator.serializeNBT()));
        nbt.put("Spectators", spectatorsList);

        ListNBT bossesList = new ListNBT();
        this.bosses.forEach(boss -> bossesList.add(StringNBT.valueOf(boss.toString())));
        nbt.put("Bosses", bossesList);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("PlayerId")) {
            this.playerIds = Collections.singletonList(nbt.getUUID("PlayerId"));
        } else {
            ListNBT playerIdsList = nbt.getList("PlayerIds", Constants.NBT.TAG_INT_ARRAY);
            this.playerIds = playerIdsList.stream().map(NBTUtil::loadUUID).collect(Collectors.toList());
        }

        this.box = new MutableBoundingBox(nbt.getIntArray("Box"));
        this.level = nbt.getInt("Level");
        this.rarity = nbt.getInt("Rarity");
        this.sTickLeft = nbt.getInt("StartTicksLeft");
        this.ticksLeft = nbt.getInt("TicksLeft");
        this.playerBossName = nbt.getString("PlayerBossName");
        this.won = nbt.getBoolean("Won");
        this.cannotExit = nbt.getBoolean("CannotExit");
        this.summonedBoss = nbt.getBoolean("SummonedBoss");
        this.spawner.maxMobs = nbt.getInt("Spawner.MaxMobs");
        this.modifiers.deserializeNBT(nbt.getCompound("Modifiers"));

        if (nbt.contains("Start", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT startNBT = nbt.getCompound("Start");
            this.start = new BlockPos(startNBT.getInt("x"), startNBT.getInt("y"), startNBT.getInt("z"));
        }

        this.spectators.clear();
        ;
        ListNBT spectatorsList = nbt.getList("Spectators", Constants.NBT.TAG_COMPOUND);
        spectatorsList.stream()
                .map(inbt -> (CompoundNBT) inbt)
                .map(Spectator::fromNBT)
                .forEach(this.spectators::add);

        this.bosses.clear();
        ListNBT bossesList = nbt.getList("Bosses", Constants.NBT.TAG_STRING);
        bossesList.stream()
                .map(inbt -> ((StringNBT) inbt).getAsString())
                .map(UUID::fromString)
                .forEach(this.bosses::add);
    }

    public static VaultRaid fromNBT(CompoundNBT nbt) {
        VaultRaid raid = new VaultRaid();
        raid.deserializeNBT(nbt);
        return raid;
    }

    public void teleportToStart(ServerWorld world, ServerPlayerEntity player) {
        if (this.start == null) {
            Vault.LOGGER.warn("No vault start was found.");
            player.teleportTo(world, this.box.x0 + this.box.getXSpan() / 2.0F, 256,
                    this.box.z0 + this.box.getZSpan() / 2.0F, player.yRot, player.xRot);
            return;
        }

        player.teleportTo(world, this.start.getX() + 0.5D, this.start.getY() + 0.2D, this.start.getZ() + 0.5D,
                this.facing == null ? world.getRandom().nextFloat() * 360.0F : this.facing.getClockWise().toYRot(), 0.0F);

        player.setOnGround(true);
    }

    public void start(ServerWorld world, ChunkPos chunkPos, CrystalData data) {
        this.spawner.init();

        loop:
        for (int x = -48; x < 48; x++) {
            for (int z = -48; z < 48; z++) {
                for (int y = 0; y < 48; y++) {
                    BlockPos pos = chunkPos.getWorldPosition().offset(x, VaultStructure.START_Y + y, z);
                    if (world.getBlockState(pos).getBlock() != Blocks.CRIMSON_PRESSURE_PLATE) continue;
                    world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());

                    this.start = pos;

                    for (Direction direction : Direction.Plane.HORIZONTAL) {
                        int count = 1;

                        while (world.getBlockState(pos.relative(direction, count)).getBlock() == Blocks.WARPED_PRESSURE_PLATE) {
                            world.setBlockAndUpdate(pos.relative(direction, count), Blocks.AIR.defaultBlockState());
                            count++;
                        }

                        if (count != 1) {
                            (this.isFinalVault ? FINAL_PORTAL_PLACER : PORTAL_PLACER).place(world, pos, this.facing = direction, count, count + 1);
                            break loop;
                        }
                    }
                }
            }
        }

        this.spectatorIds.forEach(uuid -> {
            ServerPlayerEntity player = world.getServer().getPlayerList().getPlayer(uuid);
            if (player == null) return;
            this.addSpectator(player);
        });

        this.runForAll(world.getServer(), player -> {
            this.teleportToStart(world, player);
            player.setPortalCooldown();

            Scoreboard scoreboard = player.getScoreboard();
            ScoreObjective objective = getOrCreateObjective(scoreboard, "VaultRarity", ScoreCriteria.DUMMY, ScoreCriteria.RenderType.INTEGER);
            scoreboard.getOrCreatePlayerScore(player.getName().getString(), objective).setScore(this.rarity);

            long seconds = (this.ticksLeft / 20) % 60;
            long minutes = ((this.ticksLeft / 20) / 60) % 60;
            String duration = String.format("%02d:%02d", minutes, seconds);

            StringTextComponent title = new StringTextComponent("The Vault");
            title.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_ddd01e)));

            IFormattableTextComponent subtitle = this.cannotExit
                    ? new StringTextComponent("No exit this time, ").append(player.getName()).append(new StringTextComponent("!"))
                    : new StringTextComponent("Good luck, ").append(player.getName()).append(new StringTextComponent("!"));
            subtitle.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_ddd01e)));

            StringTextComponent actionBar = new StringTextComponent("You have " + duration + " minutes to complete the raid.");
            actionBar.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_ddd01e)));

            STitlePacket titlePacket = new STitlePacket(STitlePacket.Type.TITLE, title);
            STitlePacket subtitlePacket = new STitlePacket(STitlePacket.Type.SUBTITLE, subtitle);

            player.connection.send(titlePacket);
            player.connection.send(subtitlePacket);
            player.displayClientMessage(actionBar, true);

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
                    player.connection.connection,
                    NetworkDirection.PLAY_TO_CLIENT
            );

            StringTextComponent text = new StringTextComponent("");
            AtomicBoolean startsWithVowel = new AtomicBoolean(false);

            this.modifiers.forEach((i, modifier) -> {
                StringTextComponent s = new StringTextComponent(modifier.getName());

                s.setStyle(Style.EMPTY.withColor(Color.fromRgb(modifier.getColor()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(modifier.getDescription()))));

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

            text.append(new StringTextComponent(rarityName).withStyle(VaultRarity.values()[this.rarity].color));
            text.append(new StringTextComponent(" Vault!"));
            prefix.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_ffffff)));
            text.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_ffffff)));

            IFormattableTextComponent playerName = player.getDisplayName().copy();
            playerName.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_983198)));

            world.getServer().getPlayerList().broadcastMessage(playerName.append(prefix).append(text), ChatType.CHAT, player.getUUID());
            Advancement advancement = player.getServer().getAdvancements().getAdvancement(Vault.id("root"));
            player.getAdvancements().award(advancement, "entered_vault");
        });
    }

    public void addSpectator(ServerPlayerEntity player) {
        this.getPlayerIds().remove(player.getUUID());
        Spectator spectator = new Spectator();
        spectator.uuid = player.getUUID();
        spectator.oldGameType = player.gameMode.getGameModeForPlayer();
        player.setGameMode(GameType.SPECTATOR);

        if (player.level.dimension() != Vault.VAULT_KEY) {
            this.teleportToStart(player.getServer().getLevel(Vault.VAULT_KEY), player);
        }
    }

    public static class Spectator implements INBTSerializable<CompoundNBT> {
        public GameType oldGameType;
        public UUID uuid;

        public void finish(ServerWorld world, VaultRaid raid) {
            NetcodeUtils.runIfPresent(world.getServer(), this.uuid, player -> {
                player.setGameMode(this.oldGameType);
                raid.teleportToStart(world, player);
            });
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("GameType", this.oldGameType.ordinal());
            nbt.putUUID("PlayerId", this.uuid);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            this.oldGameType = GameType.values()[nbt.getInt("GameType")];
            this.uuid = nbt.getUUID("PlayerId");
        }

        public static Spectator fromNBT(CompoundNBT nbt) {
            Spectator spectator = new Spectator();
            spectator.deserializeNBT(nbt);
            return spectator;
        }
    }

}
