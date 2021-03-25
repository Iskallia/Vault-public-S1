package iskallia.vault.item;

import iskallia.vault.Vault;
import iskallia.vault.init.ModSounds;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.Item.Properties;

public class ItemUnidentifiedArtifact extends Item {

    public ItemUnidentifiedArtifact(ItemGroup group, ResourceLocation id) {
        super(new Properties()
                .tab(group)
                .stacksTo(64));

        this.setRegistryName(id);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClientSide) {
            ItemStack heldStack = player.getItemInHand(hand);

            Vector3d position = player.position();

            ((ServerWorld) world).playSound(
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

            int randomIndex = world.random.nextInt(16) + 1;
            Item item = Registry.ITEM.get(Vault.id("artifact_" + randomIndex));
            ItemStack artifactStack = new ItemStack(item);

            player.drop(artifactStack, false, false);

            heldStack.shrink(1);
        }

        return super.use(world, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        StringTextComponent text = new StringTextComponent("Right click to identify.");
        text.setStyle(Style.EMPTY.withColor(Color.fromRgb(0xFF_ffdb00)));
        tooltip.add(text);

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

}
