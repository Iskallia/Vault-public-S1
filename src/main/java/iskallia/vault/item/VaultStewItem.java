package iskallia.vault.item;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SoupItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.minecraft.item.Item.Properties;

public class VaultStewItem extends SoupItem {

	public static Food FOOD = new Food.Builder().saturationMod(0).nutrition(0).fast().alwaysEat().build();
	private final Rarity rarity;

	public VaultStewItem(ResourceLocation id, Rarity rarity, Properties builder) {
		super(builder);
		this.setRegistryName(id);
		this.rarity = rarity;
	}

	public Rarity getRarity() {
		return this.rarity;
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (!world.isClientSide && this.getRarity() == Rarity.MYSTERY) {
			ItemStack heldStack = player.getItemInHand(hand);
			String randomPart = ModConfigs.VAULT_STEW.STEW_POOL.getRandom(world.random);
			ItemStack stackToDrop = new ItemStack(Registry.ITEM.getOptional(new ResourceLocation(randomPart)).orElse(Items.AIR));
			ItemRelicBoosterPack.successEffects(world, player.position());

			player.drop(stackToDrop, false, false);
			heldStack.shrink(1);
		}

		return super.use(world, player, hand);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
		if(this.getRarity() != Rarity.MYSTERY && entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity)entity;
			PlayerVaultStatsData statsData = PlayerVaultStatsData.get((ServerWorld) world);
			PlayerVaultStats stats = statsData.getVaultStats(player);
			statsData.addVaultExp(player, (int)(stats.getTnl() * this.getRarity().tnlProgress));
		}

		return super.finishUsingItem(stack, world, entity);
	}

	public enum Rarity {
		MYSTERY(0.0F),
		NORMAL(0.2F),
		RARE(0.4F),
		EPIC(0.65F),
		OMEGA(0.99F);

		public final float tnlProgress;

		Rarity(float tnlProgress) {
			this.tnlProgress = tnlProgress;
		}
	}

}
