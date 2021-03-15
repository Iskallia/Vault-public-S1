package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.entity.EternalEntity;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.EternalsData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EternalConfig extends Config {

	@Expose private int EXTRA_HP_PER_ETERNAL;
	@Expose private int EXTRA_DAMAGE_PER_ETERNAL;
	@Expose private List<Level> LEVEL_OVERRIDES = new ArrayList<>();

	@Override
	public String getName() {
		return "eternal";
	}

	public Level getForLevel(int level) {
		for(int i = 0; i < this.LEVEL_OVERRIDES.size(); i++) {
			if(level < this.LEVEL_OVERRIDES.get(i).MIN_LEVEL) {
				if(i == 0)break;
				return this.LEVEL_OVERRIDES.get(i - 1);
			} else if(i == this.LEVEL_OVERRIDES.size() - 1) {
				return this.LEVEL_OVERRIDES.get(i);
			}
		}

		return Level.EMPTY;
	}

	@Override
	protected void reset() {
		this.EXTRA_HP_PER_ETERNAL = 30;
		this.EXTRA_DAMAGE_PER_ETERNAL = 10;
		this.LEVEL_OVERRIDES.add(new Level(5)
				.attribute(ModAttributes.CRIT_CHANCE, 0.5D)
				.attribute(ModAttributes.CRIT_MULTIPLIER, 5.0D)
				.attribute(Attributes.MAX_HEALTH, 60.0D));
	}

	@SubscribeEvent
	public static void onEternalScaled(LivingEvent.LivingUpdateEvent event) {
		if(event.getEntity().world.isRemote)return;
		if(!(event.getEntity() instanceof EternalEntity))return;
		if(event.getEntity().getTags().contains("VaultScaled"))return;
		EternalEntity eternal = (EternalEntity)event.getEntity();
		ServerWorld world = (ServerWorld)eternal.world;
		VaultRaid raid = VaultRaidData.get(world).getAt(eternal.getPosition());
		if(raid == null)return;

		Level level = ModConfigs.ETERNAL.getForLevel(raid.level);

		for(VaultMobsConfig.Mob.AttributeOverride override: level.ATTRIBUTES) {
			if(world.rand.nextDouble() >= override.ROLL_CHANCE)continue;
			Attribute attribute = Registry.ATTRIBUTE.getOptional(new ResourceLocation(override.NAME)).orElse(null);
			if(attribute == null)continue;
			ModifiableAttributeInstance instance = eternal.getAttribute(attribute);
			if(instance == null)continue;
			instance.setBaseValue(override.getValue(instance.getBaseValue(), world.getRandom()));
		}

		EternalsData.EternalGroup eternals = EternalsData.get(world).getEternals(eternal.getOwner());

		int extraHealth = eternals.getEternals().size() * ModConfigs.ETERNAL.EXTRA_HP_PER_ETERNAL;
		eternal.getAttribute(Attributes.MAX_HEALTH)
				.applyPersistentModifier(new AttributeModifier("Multiple eternals health bonus",
						extraHealth, AttributeModifier.Operation.ADDITION));

		int extraDamage = eternals.getEternals().size() * ModConfigs.ETERNAL.EXTRA_DAMAGE_PER_ETERNAL;
		eternal.getAttribute(Attributes.ATTACK_DAMAGE)
				.applyPersistentModifier(new AttributeModifier("Multiple eternals damage bonus",
						extraDamage, AttributeModifier.Operation.ADDITION));

		eternal.heal(1000000.0F);
		eternal.getTags().add("VaultScaled");
	}

	public static class Level {
		public static final Level EMPTY = new Level(0);

		@Expose public int MIN_LEVEL;
		@Expose public List<VaultMobsConfig.Mob.AttributeOverride> ATTRIBUTES;

		public Level(int minLevel) {
			this.MIN_LEVEL = minLevel;
			this.ATTRIBUTES = new ArrayList<>();
		}


		public Level attribute(Attribute attribute, double defaultValue) {
			this.ATTRIBUTES.add(new VaultMobsConfig.Mob.AttributeOverride(attribute, defaultValue));
			return this;
		}
	}

}
