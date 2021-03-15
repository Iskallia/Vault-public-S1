package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.skill.set.AssassinSet;
import iskallia.vault.skill.set.NinjaSet;
import iskallia.vault.skill.set.SetNode;
import iskallia.vault.skill.set.SetTree;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerSetsData;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ParryTalent extends PlayerTalent {

    @Expose private final float parryChance;

    public ParryTalent(int cost, float parryChance) {
        super(cost);
        this.parryChance = parryChance;
    }

    public float getParryChance() {
        return parryChance;
    }

    @SubscribeEvent
    public static void onPlayerDamage(LivingAttackEvent event) {
        if (event.getEntityLiving().world.isRemote) return;
        if (!(event.getEntityLiving() instanceof ServerPlayerEntity)) return;

        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
        float totalParryChance = 0.0F;

        TalentTree abilities = PlayerTalentsData.get(player.getServerWorld()).getTalents(player);

        for (TalentNode<?> node : abilities.getNodes()) {
            if (!(node.getTalent() instanceof ParryTalent)) continue;
            ParryTalent talent = (ParryTalent) node.getTalent();
            totalParryChance += talent.getParryChance();
        }

        SetTree sets = PlayerSetsData.get(player.getServerWorld()).getSets(player);

        for (SetNode<?> node : sets.getNodes()) {
            if (node.getSet() instanceof AssassinSet) {
                AssassinSet set = (AssassinSet) node.getSet();
                totalParryChance += set.getParryChance();
            } else if(node.getSet() instanceof NinjaSet) {
                NinjaSet set = (NinjaSet) node.getSet();
                totalParryChance += set.getParryChance();
            }
        }

        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            ItemStack stack = player.getItemStackFromSlot(slot);
            totalParryChance += ModAttributes.EXTRA_PARRY_CHANCE.getOrDefault(stack, 0.0F).getValue(stack);
        }

        if (event.getEntity().world.rand.nextFloat() <= totalParryChance) {
            player.world.playSound(
                    null,
                    player.getPosX(),
                    player.getPosY(),
                    player.getPosZ(),
                    SoundEvents.ITEM_SHIELD_BLOCK,
                    SoundCategory.MASTER,
                    1F, 1F
            );

            event.setCanceled(true);
        }
    }

}
