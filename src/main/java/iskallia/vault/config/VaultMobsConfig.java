package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModEntities;
import iskallia.vault.util.WeightedList;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Consumer;

public class VaultMobsConfig extends Config {

	public static final Item[] LEATHER_ARMOR = { Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE,
			Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS };
	public static final Item[] GOLDEN_ARMOR = { Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE,
			Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS };
	public static final Item[] CHAINMAIL_ARMOR = { Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE,
			Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS };
	public static final Item[] IRON_ARMOR = { Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS,
			Items.IRON_BOOTS };
	public static final Item[] DIAMOND_ARMOR = { Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE,
			Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS };
	public static final Item[] NETHERITE_ARMOR = { Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE,
			Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS };

	public static final Item[] WOODEN_WEAPONS = { Items.WOODEN_SWORD, Items.WOODEN_AXE, Items.WOODEN_PICKAXE,
			Items.WOODEN_SHOVEL, Items.WOODEN_HOE };
	public static final Item[] STONE_WEAPONS = { Items.STONE_SWORD, Items.STONE_AXE, Items.STONE_PICKAXE,
			Items.STONE_SHOVEL, Items.STONE_HOE };
	public static final Item[] GOLDEN_WEAPONS = { Items.GOLDEN_SWORD, Items.GOLDEN_AXE, Items.GOLDEN_PICKAXE,
			Items.GOLDEN_SHOVEL, Items.GOLDEN_HOE };
	public static final Item[] IRON_WEAPONS = { Items.IRON_SWORD, Items.IRON_AXE, Items.IRON_PICKAXE, Items.IRON_SHOVEL,
			Items.IRON_HOE };
	public static final Item[] DIAMOND_WEAPONS = { Items.DIAMOND_SWORD, Items.DIAMOND_AXE, Items.DIAMOND_PICKAXE,
			Items.DIAMOND_SHOVEL, Items.DIAMOND_HOE };
	public static final Item[] NETHERITE_WEAPONS = { Items.NETHERITE_SWORD, Items.NETHERITE_AXE, Items.NETHERITE_PICKAXE,
			Items.NETHERITE_SHOVEL, Items.NETHERITE_HOE };

	@Expose private List<Level> LEVEL_OVERRIDES = new ArrayList<>();

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
	public String getName() {
		return "vault_mobs";
	}

    @Override
    protected void reset() {
        this.LEVEL_OVERRIDES.add(new Level(5)
                .mobAdd(Items.WOODEN_SWORD, 1)
                .mobAdd(Items.STONE_SWORD, 2)
                .bossAdd(Items.STONE_SWORD, 1)
                .bossAdd(Items.GOLDEN_SWORD, 2)
                .raffleAdd(Items.DIAMOND_SWORD, 1)
                .mob(EntityType.ZOMBIE, 1, mob -> mob
                        .attribute(ModAttributes.CRIT_CHANCE, 1.0D)
                        .attribute(ModAttributes.CRIT_MULTIPLIER, 5.0D)
                        .attribute(Attributes.MAX_HEALTH, 20.0D)
                ).boss(ModEntities.ROBOT, 1, mob -> mob
                        .attribute(ModAttributes.TP_CHANCE, 0.5D)
                        .attribute(ModAttributes.TP_RANGE, 32.0D)
                ).mobMisc(3, 1, 3).bossMisc(3, 1).raffleMisc(3, 1));
    }

	public static class Level {
		public static final Level EMPTY = new Level(0);

		@Expose public int MIN_LEVEL;
		@Expose public Map<String, WeightedList<String>> MOB_LOOT;
		@Expose public Map<String, WeightedList<String>> BOSS_LOOT;
		@Expose public Map<String, WeightedList<String>> RAFFLE_BOSS_LOOT;

		@Expose public WeightedList<Mob> MOB_POOL;
		@Expose public WeightedList<Mob> BOSS_POOL;
		@Expose public WeightedList<Mob> RAFFLE_BOSS_POOL;

		@Expose public MobMisc MOB_MISC;
		@Expose public BossMisc BOSS_MISC;
		@Expose public BossMisc RAFFLE_BOSS_MISC;

		public Level(int minLevel) {
			this.MIN_LEVEL = minLevel;
			this.MOB_LOOT = new LinkedHashMap<>();
			this.BOSS_LOOT = new LinkedHashMap<>();
			this.RAFFLE_BOSS_LOOT = new LinkedHashMap<>();

			this.MOB_POOL = new WeightedList<>();
			this.BOSS_POOL = new WeightedList<>();
			this.RAFFLE_BOSS_POOL = new WeightedList<>();

			this.MOB_MISC = new MobMisc(0, 0, 0);
			this.BOSS_MISC = new BossMisc(0, 0);
			this.RAFFLE_BOSS_MISC = new BossMisc(0, 0);
		}

		public Level mobAdd(Item item, int weight) {
			if(item instanceof ArmorItem) {
				this.MOB_LOOT.computeIfAbsent(((ArmorItem)item).getSlot().getName(), s -> new WeightedList<>()).add(item.getRegistryName().toString(), weight);
			} else {
				this.MOB_LOOT.computeIfAbsent(EquipmentSlotType.MAINHAND.getName(), s -> new WeightedList<>()).add(item.getRegistryName().toString(), weight);
				this.MOB_LOOT.computeIfAbsent(EquipmentSlotType.OFFHAND.getName(), s -> new WeightedList<>()).add(item.getRegistryName().toString(), weight);
			}

			return this;
		}

		public Level bossAdd(Item item, int weight) {
			if(item instanceof ArmorItem) {
				this.BOSS_LOOT.computeIfAbsent(((ArmorItem)item).getSlot().getName(), s -> new WeightedList<>()).add(item.getRegistryName().toString(), weight);
			} else {
				this.BOSS_LOOT.computeIfAbsent(EquipmentSlotType.MAINHAND.getName(), s -> new WeightedList<>()).add(item.getRegistryName().toString(), weight);
				this.BOSS_LOOT.computeIfAbsent(EquipmentSlotType.OFFHAND.getName(), s -> new WeightedList<>()).add(item.getRegistryName().toString(), weight);
			}

			return this;
		}

		public Level raffleAdd(Item item, int weight) {
			if(item instanceof ArmorItem) {
				this.RAFFLE_BOSS_LOOT.computeIfAbsent(((ArmorItem)item).getSlot().getName(), s -> new WeightedList<>()).add(item.getRegistryName().toString(), weight);
			} else {
				this.RAFFLE_BOSS_LOOT.computeIfAbsent(EquipmentSlotType.MAINHAND.getName(), s -> new WeightedList<>()).add(item.getRegistryName().toString(), weight);
				this.RAFFLE_BOSS_LOOT.computeIfAbsent(EquipmentSlotType.OFFHAND.getName(), s -> new WeightedList<>()).add(item.getRegistryName().toString(), weight);
			}

			return this;
		}

		public Level mobMisc(int level, int trials, int maxMobs) {
			this.MOB_MISC = new MobMisc(level, trials, maxMobs);
			return this;
		}

		public Level bossMisc(int level, int trials) {
			this.BOSS_MISC = new BossMisc(level, trials);
			return this;
		}

		public Level raffleMisc(int level, int trials) {
			this.RAFFLE_BOSS_MISC = new BossMisc(level, trials);
			return this;
		}

		public Level mob(EntityType<? extends LivingEntity> type, int weight) {
			this.MOB_POOL.add(new Mob(type), weight);
			return this;
		}

		public Level mob(EntityType<? extends LivingEntity> type, int weight, Consumer<Mob> action) {
			Mob mob = new Mob(type);
			action.accept(mob);
			this.MOB_POOL.add(mob, weight);
			return this;
		}

		public Level boss(EntityType<? extends LivingEntity> type, int weight) {
			this.BOSS_POOL.add(new Mob(type), weight);
			return this;
		}

		public Level boss(EntityType<? extends LivingEntity> type, int weight, Consumer<Mob> action) {
			Mob mob = new Mob(type);
			action.accept(mob);
			this.BOSS_POOL.add(mob, weight);
			return this;
		}

		public Level raffle(EntityType<? extends LivingEntity> type, int weight) {
			this.RAFFLE_BOSS_POOL.add(new Mob(type), weight);
			return this;
		}

		public Level raffle(EntityType<? extends LivingEntity> type, int weight, Consumer<Mob> action) {
			Mob mob = new Mob(type);
			action.accept(mob);
			this.RAFFLE_BOSS_POOL.add(mob, weight);
			return this;
		}

		public Item getForMob(EquipmentSlotType slot) {
			if(this.MOB_LOOT.isEmpty() || !this.MOB_LOOT.containsKey(slot.getName()))return Items.AIR;
			String item = this.MOB_LOOT.get(slot.getName()).getRandom(new Random());
			return Registry.ITEM.getOptional(new ResourceLocation(item)).orElse(Items.AIR);
		}

		public Item getForBoss(EquipmentSlotType slot) {
			if(this.BOSS_LOOT.isEmpty() || !this.BOSS_LOOT.containsKey(slot.getName()))return Items.AIR;
			String item = this.BOSS_LOOT.get(slot.getName()).getRandom(new Random());
			return Registry.ITEM.getOptional(new ResourceLocation(item)).orElse(Items.AIR);
		}

		public Item getForRaffle(EquipmentSlotType slot) {
			if(this.RAFFLE_BOSS_LOOT.isEmpty() || !this.RAFFLE_BOSS_LOOT.containsKey(slot.getName()))return Items.AIR;
			String item = this.RAFFLE_BOSS_LOOT.get(slot.getName()).getRandom(new Random());
			return Registry.ITEM.getOptional(new ResourceLocation(item)).orElse(Items.AIR);
		}
	}

	public static class Mob {
		@Expose private String NAME;
		@Expose private List<AttributeOverride> ATTRIBUTES;

		public Mob(EntityType<?> type) {
			this.NAME = type.getRegistryName().toString();
			this.ATTRIBUTES = new ArrayList<>();
		}

		public Mob attribute(Attribute attribute, double defaultValue) {
			this.ATTRIBUTES.add(new AttributeOverride(attribute, defaultValue));
			return this;
		}

		public EntityType<?> getType() {
			return Registry.ENTITY_TYPE.getOptional(new ResourceLocation(this.NAME)).orElse(EntityType.BAT);
		}

		public LivingEntity create(World world) {
			LivingEntity entity = (LivingEntity)this.getType().create(world);

			for(AttributeOverride override: ATTRIBUTES) {
				if(world.random.nextDouble() >= override.ROLL_CHANCE)continue;
				Attribute attribute = Registry.ATTRIBUTE.getOptional(new ResourceLocation(override.NAME)).orElse(null);
				if(attribute == null)continue;
				ModifiableAttributeInstance instance = entity.getAttribute(attribute);
				if(instance == null)continue;
				instance.setBaseValue(override.getValue(instance.getBaseValue(), world.getRandom()));
			}

			entity.heal(1000000.0F);
			return entity;
		}

		public static class AttributeOverride {
			@Expose public String NAME;
			@Expose public double MIN;
			@Expose public double MAX;
			@Expose public String OPERATOR;
			@Expose public double ROLL_CHANCE;

			public AttributeOverride(Attribute attribute, double defaultValue) {
				this.NAME = attribute.getRegistryName().toString();
				this.MIN = defaultValue;
				this.MAX = defaultValue;
				this.OPERATOR = "set";
				this.ROLL_CHANCE = 1.0F;
			}

			public double getValue(double baseValue, Random random) {
				double value = Math.min(this.MIN, this.MAX) + random.nextFloat() * Math.abs(this.MAX - this.MIN);

				if(this.OPERATOR.equalsIgnoreCase("multiply")) {
					return baseValue * value;
				} else if(this.OPERATOR.equalsIgnoreCase("add")) {
					return baseValue + value;
				}  else if(this.OPERATOR.equalsIgnoreCase("set")) {
					return value;
				}

				return baseValue;
			}
		}
	}

	public static class MobMisc {
		@Expose public int ENCH_LEVEL;
		@Expose public int ENCH_TRIALS;
		@Expose public int MAX_MOBS;

		public MobMisc(int level, int trials, int maxMobs) {
			this.ENCH_LEVEL = level;
			this.ENCH_TRIALS = trials;
			this.MAX_MOBS = maxMobs;
		}
	}

	public static class BossMisc {
		@Expose public int ENCH_LEVEL;
		@Expose public int ENCH_TRIALS;

		public BossMisc(int level, int trials) {
			this.ENCH_LEVEL = level;
			this.ENCH_TRIALS = trials;
		}
	}

}
