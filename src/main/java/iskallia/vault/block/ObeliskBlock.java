package iskallia.vault.block;

import iskallia.vault.client.gui.overlay.VaultRaidOverlay;
import iskallia.vault.entity.EntityScaler;
import iskallia.vault.entity.FighterEntity;
import iskallia.vault.entity.VaultBoss;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.ObeliskInscriptionItem;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

import net.minecraft.block.AbstractBlock.Properties;

public class ObeliskBlock extends Block {

    public static final IntegerProperty COMPLETION = IntegerProperty.create("completion", 0, 4);

    public ObeliskBlock() {
        super(Properties.of(Material.STONE).sound(SoundType.METAL).strength(-1.0F, 3600000.0F).noDrops());
        this.registerDefaultState(this.stateDefinition.any().setValue(COMPLETION, 0));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return Block.box(4f, 0f, 4f, 12f, 32f, 12f);
//        return super.getShape(state, worldIn, pos, context);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack heldStack = player.getItemInHand(hand);

        if (heldStack.getItem() instanceof ObeliskInscriptionItem) {
            if (!player.isCreative()) {
                heldStack.shrink(1);
            }
        } else {
            return ActionResultType.PASS;
        }

        BlockState newState = state.setValue(COMPLETION, MathHelper.clamp(state.getValue(COMPLETION) + 1, 0, 4));
        world.setBlockAndUpdate(pos, newState);

        if (world.isClientSide) {
            if (newState.getValue(COMPLETION) == 4)
                startBossLoop();

            return ActionResultType.SUCCESS;
        }

        this.spawnParticles(world, pos);

        if (newState.getValue(COMPLETION) == 4) {
            VaultRaid raid = VaultRaidData.get((ServerWorld) world).getAt(pos);

            if(raid != null) {
                spawnBoss(raid, (ServerWorld)world, pos, EntityScaler.Type.BOSS);
            }

            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }

        return ActionResultType.SUCCESS;
    }

    public void spawnBoss(VaultRaid raid, ServerWorld world, BlockPos pos, EntityScaler.Type type) {
        LivingEntity boss;

        if(type == EntityScaler.Type.BOSS) {
            boss = ModConfigs.VAULT_MOBS.getForLevel(raid.level).BOSS_POOL.getRandom(world.getRandom()).create(world);
        } else {
            return;
        }

        if(boss instanceof FighterEntity)((FighterEntity)boss).changeSize(2.0F);
        boss.moveTo(pos.getX() + 0.5D, pos.getY() + 0.2D, pos.getZ() + 0.5D, 0.0F, 0.0F);
        world.addWithUUID(boss);

        boss.getTags().add("VaultBoss");
        raid.addBoss(boss);

        if(boss instanceof FighterEntity) {
            ((FighterEntity)boss).bossInfo.setVisible(true);
        }

        if(boss instanceof VaultBoss) {
            ((VaultBoss)boss).getServerBossInfo().setVisible(true);
        }

        EntityScaler.scaleVault(boss, raid.level, new Random(), EntityScaler.Type.BOSS);

        if(raid.playerBossName != null) {
            boss.setCustomName(new StringTextComponent(raid.playerBossName));
        } else {
            boss.setCustomName(new StringTextComponent("Boss"));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void startBossLoop() {
        VaultRaidOverlay.bossSummoned = true;
    }

    private void spawnParticles(World world, BlockPos pos) {
        for (int i = 0; i < 20; ++i) {
            double d0 = world.random.nextGaussian() * 0.02D;
            double d1 = world.random.nextGaussian() * 0.02D;
            double d2 = world.random.nextGaussian() * 0.02D;

            ((ServerWorld) world).sendParticles(ParticleTypes.POOF,
                    pos.getX() + world.random.nextDouble() - d0,
                    pos.getY() + world.random.nextDouble() - d1,
                    pos.getZ() + world.random.nextDouble() - d2, 10, d0, d1, d2, 1.0D);
        }

        world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(COMPLETION);
    }

}
