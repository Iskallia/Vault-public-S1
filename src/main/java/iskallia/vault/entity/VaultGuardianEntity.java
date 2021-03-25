package iskallia.vault.entity;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class VaultGuardianEntity extends PiglinBruteEntity {

    public VaultGuardianEntity(EntityType<? extends PiglinBruteEntity> type, World world) {
        super(type, world);
        this.setCanPickUpLoot(false);
        ModifiableAttributeInstance attribute = this.getAttribute(Attributes.ATTACK_KNOCKBACK);
        if (attribute != null) attribute.setBaseValue(6);
    }

    @Override
    protected void dropFromLootTable(DamageSource source, boolean attackedRecently) {
        if (getRandom().nextInt(ModConfigs.VAULT_GENERAL.getObeliskDropChance()) == 0)
            this.spawnAtLocation(new ItemStack(ModItems.OBELISK_INSCRIPTION));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!(source.getEntity() instanceof PlayerEntity)
                && !(source.getEntity() instanceof EternalEntity)
                && source != DamageSource.OUT_OF_WORLD) {
            return false;
        }

        if (this.isInvulnerableTo(source) || source == DamageSource.FALL) {
            return false;
        }

        playHurtSound(source);

        return super.hurt(source, amount);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source.isProjectile();
    }

    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.setImmuneToZombification(true);
        this.timeInOverworld = compound.getInt("TimeInOverworld");
    }

    @Override
    public void knockback(float strength, double ratioX, double ratioZ) {
        // Nope! No knockback allowed bruhhhh
    }

    @Override
    protected float getBlockSpeedFactor() {
        return 0.75f;
    }

}
