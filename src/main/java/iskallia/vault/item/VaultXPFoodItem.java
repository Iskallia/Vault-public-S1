package iskallia.vault.item;

import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Supplier;

import net.minecraft.item.Item.Properties;

public class VaultXPFoodItem extends Item {

    public static Food FOOD = new Food.Builder().saturationMod(0).nutrition(0).fast().alwaysEat().build();
    private final Supplier<Float> min;
    private final Supplier<Float> max;

    public VaultXPFoodItem(ResourceLocation id, Supplier<Float> min, Supplier<Float> max, Properties properties) {
        super(properties.food(FOOD));
        this.min = min;
        this.max = max;
        this.setRegistryName(id);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entityLiving) {
        if (!world.isClientSide && entityLiving instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entityLiving;
            PlayerVaultStatsData statsData = PlayerVaultStatsData.get((ServerWorld) world);
            PlayerVaultStats stats = statsData.getVaultStats(player);
            float randomPercentage = MathUtilities.randomFloat(this.min.get(), this.max.get());
            statsData.addVaultExp(player, (int) (stats.getTnl() * randomPercentage));
        }

        return super.finishUsingItem(stack, world, entityLiving);
    }

}
