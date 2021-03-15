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

public class VaultXPFoodItem extends Item {

    public static Food FOOD = new Food.Builder().saturation(0).hunger(0).fastToEat().setAlwaysEdible().build();
    private final Supplier<Float> min;
    private final Supplier<Float> max;

    public VaultXPFoodItem(ResourceLocation id, Supplier<Float> min, Supplier<Float> max, Properties properties) {
        super(properties.food(FOOD));
        this.min = min;
        this.max = max;
        this.setRegistryName(id);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entityLiving) {
        if (!world.isRemote && entityLiving instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entityLiving;
            PlayerVaultStatsData statsData = PlayerVaultStatsData.get((ServerWorld) world);
            PlayerVaultStats stats = statsData.getVaultStats(player);
            float randomPercentage = MathUtilities.randomFloat(this.min.get(), this.max.get());
            statsData.addVaultExp(player, (int) (stats.getTnl() * randomPercentage));
        }

        return super.onItemUseFinish(stack, world, entityLiving);
    }

}
