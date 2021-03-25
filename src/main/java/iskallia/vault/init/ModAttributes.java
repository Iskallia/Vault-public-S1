package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.block.PuzzleRuneBlock;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.item.gear.attribute.*;
import iskallia.vault.skill.talent.type.EffectTalent;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModAttributes {

	public static Map<ResourceLocation, ItemAttribute<?, ?>> REGISTRY = new HashMap<>();

	public static Attribute CRIT_CHANCE, CRIT_MULTIPLIER;
	public static Attribute TP_CHANCE, TP_INDIRECT_CHANCE, TP_RANGE;
	public static Attribute POTION_RESISTANCE;

	//Attribute Modifiers
	public static ItemAttribute<Double, DoubleAttribute> ADD_ARMOR;
	public static ItemAttribute<Double, DoubleAttribute> ADD_ARMOR_TOUGHNESS;
	public static ItemAttribute<Double, DoubleAttribute> ADD_KNOCKBACK_RESISTANCE;
	public static ItemAttribute<Double, DoubleAttribute> ADD_ATTACK_DAMAGE;
	public static ItemAttribute<Double, DoubleAttribute> ADD_ATTACK_SPEED;
	public static ItemAttribute<Integer, IntegerAttribute> ADD_DURABILITY;

	//Vanilla Attributes
	public static ItemAttribute<Double, DoubleAttribute> ARMOR;
	public static ItemAttribute<Double, DoubleAttribute> ARMOR_TOUGHNESS;
	public static ItemAttribute<Double, DoubleAttribute> KNOCKBACK_RESISTANCE;
	public static ItemAttribute<Double, DoubleAttribute> ATTACK_DAMAGE;
	public static ItemAttribute<Double, DoubleAttribute> ATTACK_SPEED;
	public static ItemAttribute<Integer, IntegerAttribute> DURABILITY;

	//Gear Attributes
	public static ItemAttribute<Integer, IntegerAttribute> GEAR_MODEL;
	public static ItemAttribute<Integer, IntegerAttribute> GEAR_COLOR;
	public static ItemAttribute<VaultGear.Rarity, EnumAttribute<VaultGear.Rarity>> GEAR_RARITY;
	public static ItemAttribute<VaultGear.State, EnumAttribute<VaultGear.State>> GEAR_STATE;
	public static ItemAttribute<VaultGear.Set, EnumAttribute<VaultGear.Set>> GEAR_SET;
	public static ItemAttribute<Float, FloatAttribute> GEAR_LEVEL;
	public static ItemAttribute<Float, FloatAttribute> GEAR_LEVEL_CHANCE;
	public static ItemAttribute<Integer, IntegerAttribute> GEAR_MAX_LEVEL;
	public static ItemAttribute<Integer, IntegerAttribute> GEAR_MODIFIERS_TO_ROLL;
	public static ItemAttribute<Integer, IntegerAttribute> MAX_REPAIRS;
	public static ItemAttribute<Integer, IntegerAttribute> CURRENT_REPAIRS;
	public static ItemAttribute<Float, FloatAttribute> EXTRA_LEECH_RATIO;
	public static ItemAttribute<Float, FloatAttribute> EXTRA_PARRY_CHANCE;
	public static ItemAttribute<Float, FloatAttribute> EXTRA_HEALTH;
	public static ItemAttribute<List<EffectTalent>, EffectAttribute> EXTRA_EFFECTS;
	public static ItemAttribute<Integer, IntegerAttribute> MIN_VAULT_LEVEL;
	public static ItemAttribute<VaultGear.RollType, EnumAttribute<VaultGear.RollType>> GEAR_ROLL_TYPE;

	//Other Attributes
	public static ItemAttribute<PuzzleRuneBlock.Color, EnumAttribute<PuzzleRuneBlock.Color>> PUZZLE_COLOR;

	public static void register(RegistryEvent.Register<Attribute> event) {
		CRIT_CHANCE = register(event.getRegistry(), "generic.crit_chance", new RangedAttribute("attribute.name.generic.crit_chance", 0.0D, 0.0D, 1.0D)).setSyncable(true);
		CRIT_MULTIPLIER = register(event.getRegistry(), "generic.crit_multiplier", new RangedAttribute("attribute.name.generic.crit_multiplier", 0.0D, 0.0D, 1024.0D)).setSyncable(true);
		TP_CHANCE = register(event.getRegistry(), "generic.tp_chance", new RangedAttribute("attribute.name.generic.tp_chance", 0.0D, 0.0D, 1.0D)).setSyncable(true);
		TP_INDIRECT_CHANCE = register(event.getRegistry(), "generic.indirect_tp_chance", new RangedAttribute("attribute.name.generic.indirect_tp_chance", 0.0D, 0.0D, 1.0D)).setSyncable(true);
		TP_RANGE = register(event.getRegistry(), "generic.tp_range", new RangedAttribute("attribute.name.generic.tp_range", 32.0D, 0.0D, 1024.0D)).setSyncable(true);
		POTION_RESISTANCE = register(event.getRegistry(), "generic.potion_resistance", new RangedAttribute("attribute.name.generic.potion_resistance", 0.0D, 0.0D, 1.0D)).setSyncable(true);

		ADD_ARMOR = register(new ResourceLocation("minecraft", "add_armor"), () -> new DoubleAttribute((stack, parent, value) -> parent.getBaseValue() + value));
		ADD_ARMOR_TOUGHNESS = register(new ResourceLocation("minecraft", "add_armor_toughness"), () -> new DoubleAttribute((stack, parent, value) -> parent.getBaseValue() + value));
		ADD_KNOCKBACK_RESISTANCE = register(new ResourceLocation("minecraft", "add_knockback_resistance"), () -> new DoubleAttribute((stack, parent, value) -> parent.getBaseValue() + value));
		ADD_ATTACK_DAMAGE = register(new ResourceLocation("minecraft", "add_attack_damage"), () -> new DoubleAttribute((stack, parent, value) -> parent.getBaseValue() + value));
		ADD_ATTACK_SPEED = register(new ResourceLocation("minecraft", "add_attack_speed"), () -> new DoubleAttribute((stack, parent, value) -> parent.getBaseValue() + value));
		ADD_DURABILITY = register(new ResourceLocation(Vault.MOD_ID, "add_durability"), () -> new IntegerAttribute((stack, parent, value) -> parent.getBaseValue() + value));

		ARMOR = register(new ResourceLocation("minecraft", "armor"), DoubleAttribute::new, ADD_ARMOR);
		ARMOR_TOUGHNESS = register(new ResourceLocation("minecraft", "armor_toughness"), DoubleAttribute::new, ADD_ARMOR_TOUGHNESS);
		KNOCKBACK_RESISTANCE = register(new ResourceLocation("minecraft", "knockback_resistance"), DoubleAttribute::new, ADD_KNOCKBACK_RESISTANCE);
		ATTACK_DAMAGE = register(new ResourceLocation("minecraft", "attack_damage"), DoubleAttribute::new, ADD_ATTACK_DAMAGE);
		ATTACK_SPEED = register(new ResourceLocation("minecraft", "attack_speed"), DoubleAttribute::new, ADD_ATTACK_SPEED);
		DURABILITY = register(new ResourceLocation(Vault.MOD_ID, "durability"), IntegerAttribute::new, ADD_DURABILITY);

		GEAR_MODEL = register(new ResourceLocation(Vault.MOD_ID, "gear_model"), IntegerAttribute::new);
		GEAR_COLOR = register(new ResourceLocation(Vault.MOD_ID, "gear_color"), IntegerAttribute::new);
		GEAR_RARITY = register(new ResourceLocation(Vault.MOD_ID, "gear_rarity"), () -> new EnumAttribute<>(VaultGear.Rarity.class));
		GEAR_STATE = register(new ResourceLocation(Vault.MOD_ID, "gear_state"), () -> new EnumAttribute<>(VaultGear.State.class));
		GEAR_SET = register(new ResourceLocation(Vault.MOD_ID, "gear_set"), () -> new EnumAttribute<>(VaultGear.Set.class));
		GEAR_LEVEL = register(new ResourceLocation(Vault.MOD_ID, "gear_level"), FloatAttribute::new);
		GEAR_LEVEL_CHANCE = register(new ResourceLocation(Vault.MOD_ID, "gear_level_chance"), FloatAttribute::new);
		GEAR_MAX_LEVEL = register(new ResourceLocation(Vault.MOD_ID, "gear_max_level"), IntegerAttribute::new);
		GEAR_MODIFIERS_TO_ROLL = register(new ResourceLocation(Vault.MOD_ID, "gear_modifiers_to_roll"), IntegerAttribute::new);
		MAX_REPAIRS = register(new ResourceLocation(Vault.MOD_ID, "max_repairs"), IntegerAttribute::new);
		CURRENT_REPAIRS = register(new ResourceLocation(Vault.MOD_ID, "current_repairs"), IntegerAttribute::new);
		EXTRA_LEECH_RATIO = register(new ResourceLocation(Vault.MOD_ID, "extra_leech_ratio"), FloatAttribute::new);
		EXTRA_PARRY_CHANCE = register(new ResourceLocation(Vault.MOD_ID, "extra_parry_chance"), FloatAttribute::new);
		EXTRA_HEALTH = register(new ResourceLocation(Vault.MOD_ID, "extra_health"), FloatAttribute::new);
		EXTRA_EFFECTS = register(new ResourceLocation(Vault.MOD_ID, "extra_effects"), EffectAttribute::new);
		MIN_VAULT_LEVEL = register(new ResourceLocation(Vault.MOD_ID, "min_vault_level"), IntegerAttribute::new);
		GEAR_ROLL_TYPE = register(new ResourceLocation(Vault.MOD_ID, "gear_roll_type"), () -> new EnumAttribute<>(VaultGear.RollType.class));

		PUZZLE_COLOR = register(new ResourceLocation(Vault.MOD_ID, "key_color"), () -> new EnumAttribute<>(PuzzleRuneBlock.Color.class));
	}

	/* ------------------------------------------- */

	private static Attribute register(IForgeRegistry<Attribute> registry, String name, Attribute attribute) {
		registry.register(attribute.setRegistryName(Vault.id(name)));
		return attribute;
	}

	private static <T, I extends ItemAttribute.Instance<T>> ItemAttribute<T, I> register(ResourceLocation id, Supplier<I> instance, ItemAttribute<T, I>... modifiers) {
		ItemAttribute<T, I> attribute = new ItemAttribute<>(id, instance, modifiers);
		REGISTRY.put(id, attribute);
		return attribute;
	}

}
