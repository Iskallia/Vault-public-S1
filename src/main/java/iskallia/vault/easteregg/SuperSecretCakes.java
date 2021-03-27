package iskallia.vault.easteregg;

import iskallia.vault.Vault;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SuperSecretCakes {

    public static final String[] CAKE_QUOTES = {
            "The cake is a lie",
            "You can have cake and eat it too?",
            "Would like some tea with that?",
            "The cake equals π (Pi) ?",
            "This cake is made with love",
            "DONT GET GREEDY",
            "The cake is a pine?",
            "That'll go right to your thighs",
            "Have you got the coffee?",
            "When life gives you cake you eat it",
            "The cake says 'goodbye'",
            "The pie want to cry",
            "It's a piece of cake to bake a pretty cake",
            "The cherries are a lie",
            "1000 calories",
            "Icing on the cake!",
            "Happy Birthday! Is it your birthday?",
            "This is caketastic!",
            "An actual pie chart",
            "Arrr! I'm a Pie-rate",
            "Not every pies in the world is round, sometimes... pi * r ^ 2",
            "HALLO!",
            "#NeverLeaving cause cake sticks to you",
            "Tell me lies, tell me sweet little pies",
            "Diet...what diet!!!!",
            "I'll take the three story pie and a diet coke... don't want to get fat",
            "This is the end of all cake",
    };

    @SubscribeEvent
    public static void onCakePlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.getWorld().isRemote()) return;
        if (((ServerWorld) event.getWorld()).getDimensionKey() != Vault.VAULT_KEY) return;

        // Cancel if put by players or tile entities
        if (event.getPlacedBlock().getBlock() == Blocks.CAKE) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onCakeEat(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isRemote()) return;
        if (event.getWorld().getDimensionKey() != Vault.VAULT_KEY) return;

        if (event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.CAKE) {
            if (event.getSide() == LogicalSide.CLIENT) {
                Random random = new Random();
                String cakeQuote = CAKE_QUOTES[random.nextInt(CAKE_QUOTES.length)];
                StringTextComponent text = new StringTextComponent("\"" + cakeQuote + "\"");
                text.setStyle(Style.EMPTY.setItalic(true).setColor(Color.fromInt(0xFF_FFC411)));
                event.getPlayer().sendStatusMessage(text, true);

            } else {
                event.getPlayer().addPotionEffect(new EffectInstance(Effects.ABSORPTION, 20 * 60, 0));
                event.getWorld().destroyBlock(event.getPos(), false);
                Advancement advancement = event.getPlayer().getServer().getAdvancementManager().getAdvancement(Vault.id("super_secret_cakes"));
                ((ServerPlayerEntity) event.getPlayer()).getAdvancements()
                        .grantCriterion(advancement, "cake_consumed");
                event.setCanceled(true);
            }
        }
    }

}
