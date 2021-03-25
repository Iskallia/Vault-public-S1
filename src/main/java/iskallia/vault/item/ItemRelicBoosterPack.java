package iskallia.vault.item;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.Item.Properties;

public class ItemRelicBoosterPack extends Item {

    public ItemRelicBoosterPack(ItemGroup group, ResourceLocation id) {
        super(new Properties()
                .tab(group)
                .stacksTo(64));

        this.setRegistryName(id);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClientSide) {
            float rand = world.random.nextFloat() * 100;
            ItemStack heldStack = player.getItemInHand(hand);
            ItemStack stackToDrop = null;

            if (rand >= 99) {
                RelicPartItem randomPart = ModConfigs.VAULT_RELICS.getRandomPart();
                stackToDrop = new ItemStack(randomPart);
                successEffects(world, player.position());

            } else if (rand >= 98) {
                stackToDrop = new ItemStack(ModItems.PANDORAS_BOX);
                successEffects(world, player.position());

            } else {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                ServerWorld serverWorld = serverPlayer.getLevel();
                int exp = ModConfigs.PLAYER_EXP.getRelicBoosterPackExp();
                float coef = MathUtilities.randomFloat(0.1f, 0.5f);
                PlayerVaultStatsData.get(serverWorld).addVaultExp(serverPlayer, (int) (exp * coef));
                failureEffects(world, player.position());
            }

            if (stackToDrop != null)
                player.drop(stackToDrop, false, false);
            heldStack.shrink(1);
        }

        return super.use(world, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, world, tooltip, flagIn);
    }

    public static void successEffects(World world, Vector3d position) {
        world.playSound(
                null,
                position.x,
                position.y,
                position.z,
                ModSounds.BOOSTER_PACK_SUCCESS_SFX,
                SoundCategory.PLAYERS,
                1f, 1f
        );

        ((ServerWorld) world).sendParticles(ParticleTypes.DRAGON_BREATH,
                position.x,
                position.y,
                position.z,
                500,
                1, 1, 1,
                0.5
        );
    }

    public static void failureEffects(World world, Vector3d position) {
        world.playSound(
                null,
                position.x,
                position.y,
                position.z,
                ModSounds.BOOSTER_PACK_FAIL_SFX,
                SoundCategory.PLAYERS,
                1f, 1f
        );

        ((ServerWorld) world).sendParticles(ParticleTypes.SMOKE,
                position.x,
                position.y,
                position.z,
                500,
                1, 1, 1,
                0.5
        );
    }

}
