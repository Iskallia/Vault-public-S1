package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.item.gear.attribute.*;
import iskallia.vault.skill.talent.type.EffectTalent;
import iskallia.vault.util.WeightedList;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class VaultGearConfig extends Config {

	@Expose public Map<String, BaseAttributes> BASE_ATTRIBUTES;
	@Expose public Map<String, BaseModifiers> BASE_MODIFIERS;

	public VaultGearConfig() {

	}

	public static VaultGearConfig get(VaultGear.Rarity rarity) {
		switch(rarity) {
			case COMMON: return ModConfigs.VAULT_GEAR_COMMON;
			case RARE: return ModConfigs.VAULT_GEAR_RARE;
			case EPIC: return ModConfigs.VAULT_GEAR_EPIC;
			case OMEGA: return ModConfigs.VAULT_GEAR_OMEGA;
			default: return ModConfigs.VAULT_GEAR_SCRAPPY;
		}
	}

	public void initializeAttributes(ItemStack stack, Random random) {
		if(!(stack.getItem() instanceof VaultGear<?>))return;
		BaseAttributes attributes = this.BASE_ATTRIBUTES.get(stack.getItem().getRegistryName().toString());
		attributes.initialize(stack, random);
	}

	public void initializeModifiers(ItemStack stack, Random random) {
		if(!(stack.getItem() instanceof VaultGear<?>))return;
		BaseModifiers modifiers = this.BASE_MODIFIERS.get(stack.getItem().getRegistryName().toString());
		modifiers.initialize(stack, random);
	}

	@Override
	protected void reset() {
		this.resetAttributes();
		this.resetModifiers();
	}

	private void resetAttributes() {
		BaseAttributes SWORD = new BaseAttributes();
		BaseAttributes AXE = new BaseAttributes();
		BaseAttributes DAGGER = new BaseAttributes();
		BaseAttributes HELMET = new BaseAttributes();
		BaseAttributes CHESTPLATE = new BaseAttributes();
		BaseAttributes LEGGINGS = new BaseAttributes();
		BaseAttributes BOOTS = new BaseAttributes();
		BaseAttributes ETCHING = new BaseAttributes();

		//============================================================================================================//

		SWORD.ATTACK_DAMAGE = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.0D, PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(6.0D, DoubleAttribute.of(NumberAttribute.Type.SET), 2)
						.add(7.0D, DoubleAttribute.of(NumberAttribute.Type.SET), 1))
				.add(0.0D, PooledAttribute.Rolls.ofBinomial(20, 0.5D), pool -> pool
						.add(0.1D, DoubleAttribute.of(NumberAttribute.Type.ADD), 1))
				.add(-0.5D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.ADD));

		SWORD.ATTACK_SPEED = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.0D, PooledAttribute.Rolls.ofBinomial(16, 0.1D), pool -> pool
						.add(0.1D, DoubleAttribute.of(NumberAttribute.Type.ADD), 1))
				.add(-1.0D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.MULTIPLY));

		SWORD.DURABILITY = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(0, PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(1561, IntegerAttribute.of(NumberAttribute.Type.SET), 2)
						.add(2031, IntegerAttribute.of(NumberAttribute.Type.SET), 1))
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		SWORD.GEAR_LEVEL_CHANCE = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(0.5F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		SWORD.GEAR_MAX_LEVEL = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(15, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		SWORD.MAX_REPAIRS = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(3, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		SWORD.MIN_VAULT_LEVEL = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(5, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		//============================================================================================================//

		AXE.ATTACK_DAMAGE = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.0D, PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(8.0D, DoubleAttribute.of(NumberAttribute.Type.SET), 2)
						.add(9.0D, DoubleAttribute.of(NumberAttribute.Type.SET), 1))
				.add(0.0D, PooledAttribute.Rolls.ofBinomial(20, 0.5D), pool -> pool
						.add(0.1D, DoubleAttribute.of(NumberAttribute.Type.ADD), 1))
				.add(0.5D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.ADD));

		AXE.ATTACK_SPEED = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.0D, PooledAttribute.Rolls.ofBinomial(10, 0.1D), pool -> pool
						.add(0.1D, DoubleAttribute.of(NumberAttribute.Type.ADD), 1))
				.add(-1.0D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.MULTIPLY));

		AXE.DURABILITY = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(0, PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(1561, IntegerAttribute.of(NumberAttribute.Type.SET), 2)
						.add(2031, IntegerAttribute.of(NumberAttribute.Type.SET), 1))
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		AXE.GEAR_LEVEL_CHANCE = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(0.5F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		AXE.GEAR_MAX_LEVEL = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(15, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		AXE.MAX_REPAIRS = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(3, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		AXE.MIN_VAULT_LEVEL = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(5, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		//============================================================================================================//

		DAGGER.ATTACK_DAMAGE = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.0D, PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(3.0D, DoubleAttribute.of(NumberAttribute.Type.SET), 2)
						.add(4.0D, DoubleAttribute.of(NumberAttribute.Type.SET), 1))
				.add(0.0D, PooledAttribute.Rolls.ofBinomial(20, 0.5D), pool -> pool
						.add(0.1D, DoubleAttribute.of(NumberAttribute.Type.ADD), 1))
				.add(-0.5D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.ADD));

		DAGGER.ATTACK_SPEED = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.0D, PooledAttribute.Rolls.ofBinomial(20, 0.1D), pool -> pool
						.add(0.1D, DoubleAttribute.of(NumberAttribute.Type.ADD), 1))
				.add(-1.0D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.MULTIPLY));

		DAGGER.DURABILITY = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(0, PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(1561, IntegerAttribute.of(NumberAttribute.Type.SET), 2)
						.add(2031, IntegerAttribute.of(NumberAttribute.Type.SET), 1))
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		DAGGER.GEAR_LEVEL_CHANCE = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(0.5F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		DAGGER.GEAR_MAX_LEVEL = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(15, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		DAGGER.MAX_REPAIRS = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(3, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		DAGGER.MIN_VAULT_LEVEL = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(5, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		//============================================================================================================//

		HELMET.ARMOR = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(3.0D, PooledAttribute.Rolls.ofBinomial(2, 0.5D), pool -> pool
						.add(1.0D, DoubleAttribute.of(NumberAttribute.Type.ADD), 1))
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		HELMET.ARMOR_TOUGHNESS = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.0D, PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(2.0D, DoubleAttribute.of(NumberAttribute.Type.SET), 2)
						.add(3.0D, DoubleAttribute.of(NumberAttribute.Type.SET), 1))
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		HELMET.KNOCKBACK_RESISTANCE = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.0D, PooledAttribute.Rolls.ofBinomial(4, 0.4D), pool -> pool
						.add(0.1D, DoubleAttribute.of(NumberAttribute.Type.ADD), 1))
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		HELMET.DURABILITY = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(0, PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(1561, IntegerAttribute.of(NumberAttribute.Type.SET), 2)
						.add(2031, IntegerAttribute.of(NumberAttribute.Type.SET), 1))
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		HELMET.GEAR_LEVEL_CHANCE = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(0.5F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		HELMET.GEAR_MAX_LEVEL = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(15, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		HELMET.MAX_REPAIRS = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(3, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		HELMET.MIN_VAULT_LEVEL = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(5, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		//============================================================================================================//

		CHESTPLATE.ARMOR = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(8.0D, PooledAttribute.Rolls.ofBinomial(2, 0.5D), pool -> pool
						.add(1.0D, DoubleAttribute.of(NumberAttribute.Type.ADD), 1))
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		CHESTPLATE.ARMOR_TOUGHNESS = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.0D, PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(2.0D, DoubleAttribute.of(NumberAttribute.Type.SET), 2)
						.add(3.0D, DoubleAttribute.of(NumberAttribute.Type.SET), 1))
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		CHESTPLATE.KNOCKBACK_RESISTANCE = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.0D, PooledAttribute.Rolls.ofBinomial(4, 0.4D), pool -> pool
						.add(0.1D, DoubleAttribute.of(NumberAttribute.Type.ADD), 1))
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		CHESTPLATE.DURABILITY = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(0, PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(1561, IntegerAttribute.of(NumberAttribute.Type.SET), 2)
						.add(2031, IntegerAttribute.of(NumberAttribute.Type.SET), 1))
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		CHESTPLATE.GEAR_LEVEL_CHANCE = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(0.5F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		CHESTPLATE.GEAR_MAX_LEVEL = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(15, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		CHESTPLATE.MAX_REPAIRS = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(3, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		CHESTPLATE.MIN_VAULT_LEVEL = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(5, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		//============================================================================================================//

		LEGGINGS.ARMOR = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(6.0D, PooledAttribute.Rolls.ofBinomial(2, 0.5D), pool -> pool
						.add(1.0D, DoubleAttribute.of(NumberAttribute.Type.ADD), 1))
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		LEGGINGS.ARMOR_TOUGHNESS = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.0D, PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(2.0D, DoubleAttribute.of(NumberAttribute.Type.SET), 2)
						.add(3.0D, DoubleAttribute.of(NumberAttribute.Type.SET), 1))
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		LEGGINGS.KNOCKBACK_RESISTANCE = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.0D, PooledAttribute.Rolls.ofBinomial(4, 0.4D), pool -> pool
						.add(0.1D, DoubleAttribute.of(NumberAttribute.Type.ADD), 1))
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		LEGGINGS.DURABILITY = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(0, PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(1561, IntegerAttribute.of(NumberAttribute.Type.SET), 2)
						.add(2031, IntegerAttribute.of(NumberAttribute.Type.SET), 1))
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		LEGGINGS.GEAR_LEVEL_CHANCE = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(0.5F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		LEGGINGS.GEAR_MAX_LEVEL = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(15, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		LEGGINGS.MAX_REPAIRS = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(3, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		LEGGINGS.MIN_VAULT_LEVEL = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(5, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		//============================================================================================================//

		BOOTS.ARMOR = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(3.0D, PooledAttribute.Rolls.ofBinomial(2, 0.5D), pool -> pool
						.add(1.0D, DoubleAttribute.of(NumberAttribute.Type.ADD), 1))
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		BOOTS.ARMOR_TOUGHNESS = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.0D, PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(2.0D, DoubleAttribute.of(NumberAttribute.Type.SET), 2)
						.add(3.0D, DoubleAttribute.of(NumberAttribute.Type.SET), 1))
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		BOOTS.KNOCKBACK_RESISTANCE = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.0D, PooledAttribute.Rolls.ofBinomial(4, 0.4D), pool -> pool
						.add(0.1D, DoubleAttribute.of(NumberAttribute.Type.ADD), 1))
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		BOOTS.DURABILITY = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(0, PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(1561, IntegerAttribute.of(NumberAttribute.Type.SET), 2)
						.add(2031, IntegerAttribute.of(NumberAttribute.Type.SET), 1))
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		BOOTS.GEAR_LEVEL_CHANCE = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(0.5F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		BOOTS.GEAR_MAX_LEVEL = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(15, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		BOOTS.MAX_REPAIRS = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(3, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		BOOTS.MIN_VAULT_LEVEL = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(5, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		//============================================================================================================//

		ETCHING.GEAR_SET = (EnumAttribute.Generator<VaultGear.Set>)EnumAttribute.generator(VaultGear.Set.class)
				.add(VaultGear.Set.NONE, PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(VaultGear.Set.ASSASSIN, EnumAttribute.of(EnumAttribute.Type.SET), 1)
						.add(VaultGear.Set.DRAGON, EnumAttribute.of(EnumAttribute.Type.SET), 1)
						.add(VaultGear.Set.GOBLIN, EnumAttribute.of(EnumAttribute.Type.SET), 1)
						.add(VaultGear.Set.GOLEM, EnumAttribute.of(EnumAttribute.Type.SET), 1)
//						.add(VaultGear.Set.PHOENIX, EnumAttribute.of(EnumAttribute.Type.SET), 1)
						.add(VaultGear.Set.RIFT, EnumAttribute.of(EnumAttribute.Type.SET), 1)
						.add(VaultGear.Set.VAMPIRE, EnumAttribute.of(EnumAttribute.Type.SET), 1)
						.add(VaultGear.Set.BRUTE, EnumAttribute.of(EnumAttribute.Type.SET), 1)
						.add(VaultGear.Set.TITAN, EnumAttribute.of(EnumAttribute.Type.SET), 1)
						.add(VaultGear.Set.DRYAD, EnumAttribute.of(EnumAttribute.Type.SET), 1)
						.add(VaultGear.Set.NINJA, EnumAttribute.of(EnumAttribute.Type.SET), 1)
						.add(VaultGear.Set.TREASURE_HUNTER, EnumAttribute.of(EnumAttribute.Type.SET), 1))
				.collect(EnumAttribute.of(EnumAttribute.Type.SET));

		//============================================================================================================//

		this.BASE_ATTRIBUTES = new LinkedHashMap<>();
		this.BASE_ATTRIBUTES.put(ModItems.SWORD.getRegistryName().toString(), SWORD);
		this.BASE_ATTRIBUTES.put(ModItems.AXE.getRegistryName().toString(), AXE);
		this.BASE_ATTRIBUTES.put(ModItems.DAGGER.getRegistryName().toString(), DAGGER);
		this.BASE_ATTRIBUTES.put(ModItems.HELMET.getRegistryName().toString(), HELMET);
		this.BASE_ATTRIBUTES.put(ModItems.CHESTPLATE.getRegistryName().toString(), CHESTPLATE);
		this.BASE_ATTRIBUTES.put(ModItems.LEGGINGS.getRegistryName().toString(), LEGGINGS);
		this.BASE_ATTRIBUTES.put(ModItems.BOOTS.getRegistryName().toString(), BOOTS);
		this.BASE_ATTRIBUTES.put(ModItems.ETCHING.getRegistryName().toString(), ETCHING);
	}

	private void resetModifiers() {
		BaseModifiers SWORD = new BaseModifiers();
		BaseModifiers AXE = new BaseModifiers();
		BaseModifiers DAGGER = new BaseModifiers();
		BaseModifiers HELMET = new BaseModifiers();
		BaseModifiers CHESTPLATE = new BaseModifiers();
		BaseModifiers LEGGINGS = new BaseModifiers();
		BaseModifiers BOOTS = new BaseModifiers();
		BaseModifiers ETCHING = new BaseModifiers();

		//============================================================================================================//

		SWORD.ADD_ATTACK_DAMAGE = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(2.0D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		SWORD.ADD_ATTACK_SPEED = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.3D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		SWORD.ADD_DURABILITY = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(500, PooledAttribute.Rolls.ofConstant(1), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		SWORD.EXTRA_LEECH_RATIO = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(0.2F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		SWORD.EXTRA_PARRY_CHANCE = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(0.05F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		SWORD.EXTRA_HEALTH = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(2.0F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		SWORD.EXTRA_EFFECTS = (EffectAttribute.Generator)EffectAttribute.generator()
				.add(new ArrayList<>(), PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(Collections.singletonList(new EffectTalent(0, Effects.RESISTANCE, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 2)
						.add(Collections.singletonList(new EffectTalent(0, Effects.RESISTANCE, 2, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 1)
						.add(Collections.singletonList(new EffectTalent(0, Effects.LUCK, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 2)
						.add(Collections.singletonList(new EffectTalent(0, Effects.LUCK, 2, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 1))
				.collect(EffectAttribute.of(EffectAttribute.Type.MERGE));

		//============================================================================================================//

		AXE.ADD_ATTACK_DAMAGE = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(2.0D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		AXE.ADD_ATTACK_SPEED = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.3D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		AXE.ADD_DURABILITY = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(500, PooledAttribute.Rolls.ofConstant(1), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		AXE.EXTRA_LEECH_RATIO = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(0.2F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		AXE.EXTRA_PARRY_CHANCE = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(0.05F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		AXE.EXTRA_HEALTH = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(2.0F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		AXE.EXTRA_EFFECTS = (EffectAttribute.Generator)EffectAttribute.generator()
				.add(new ArrayList<>(), PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(Collections.singletonList(new EffectTalent(0, Effects.RESISTANCE, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 2)
						.add(Collections.singletonList(new EffectTalent(0, Effects.RESISTANCE, 2, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 1)
						.add(Collections.singletonList(new EffectTalent(0, Effects.LUCK, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 2)
						.add(Collections.singletonList(new EffectTalent(0, Effects.LUCK, 2, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 1))
				.collect(EffectAttribute.of(EffectAttribute.Type.MERGE));

		//============================================================================================================//

		DAGGER.ADD_ATTACK_DAMAGE = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(2.0D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		DAGGER.ADD_ATTACK_SPEED = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.3D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		DAGGER.ADD_DURABILITY = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(500, PooledAttribute.Rolls.ofConstant(1), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		DAGGER.EXTRA_LEECH_RATIO = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(0.2F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		DAGGER.EXTRA_PARRY_CHANCE = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(0.05F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		DAGGER.EXTRA_HEALTH = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(2.0F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		DAGGER.EXTRA_EFFECTS = (EffectAttribute.Generator)EffectAttribute.generator()
				.add(new ArrayList<>(), PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(Collections.singletonList(new EffectTalent(0, Effects.RESISTANCE, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 2)
						.add(Collections.singletonList(new EffectTalent(0, Effects.RESISTANCE, 2, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 1)
						.add(Collections.singletonList(new EffectTalent(0, Effects.LUCK, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 2)
						.add(Collections.singletonList(new EffectTalent(0, Effects.LUCK, 2, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 1))
				.collect(EffectAttribute.of(EffectAttribute.Type.MERGE));

		//============================================================================================================//

		HELMET.ADD_ARMOR = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(1.0D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		HELMET.ADD_ARMOR_TOUGHNESS = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(2.0D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		HELMET.ADD_KNOCKBACK_RESISTANCE = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.2D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		HELMET.ADD_DURABILITY = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(500, PooledAttribute.Rolls.ofConstant(1), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		HELMET.EXTRA_PARRY_CHANCE = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(0.05F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		HELMET.EXTRA_HEALTH = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(2.0F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		HELMET.EXTRA_EFFECTS = (EffectAttribute.Generator)EffectAttribute.generator()
				.add(new ArrayList<>(), PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(Collections.singletonList(new EffectTalent(0, Effects.RESISTANCE, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 2)
						.add(Collections.singletonList(new EffectTalent(0, Effects.RESISTANCE, 2, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 1)
						.add(Collections.singletonList(new EffectTalent(0, Effects.LUCK, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 2)
						.add(Collections.singletonList(new EffectTalent(0, Effects.LUCK, 2, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 1))
				.collect(EffectAttribute.of(EffectAttribute.Type.MERGE));

		//============================================================================================================//

		CHESTPLATE.ADD_ARMOR = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(1.0D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		CHESTPLATE.ADD_ARMOR_TOUGHNESS = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(2.0D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		CHESTPLATE.ADD_KNOCKBACK_RESISTANCE = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.2D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		CHESTPLATE.ADD_DURABILITY = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(500, PooledAttribute.Rolls.ofConstant(1), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		CHESTPLATE.EXTRA_PARRY_CHANCE = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(0.05F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		CHESTPLATE.EXTRA_HEALTH = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(2.0F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		CHESTPLATE.EXTRA_EFFECTS = (EffectAttribute.Generator)EffectAttribute.generator()
				.add(new ArrayList<>(), PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(Collections.singletonList(new EffectTalent(0, Effects.RESISTANCE, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 2)
						.add(Collections.singletonList(new EffectTalent(0, Effects.RESISTANCE, 2, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 1)
						.add(Collections.singletonList(new EffectTalent(0, Effects.LUCK, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 2)
						.add(Collections.singletonList(new EffectTalent(0, Effects.LUCK, 2, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 1))
				.collect(EffectAttribute.of(EffectAttribute.Type.MERGE));

		//============================================================================================================//

		LEGGINGS.ADD_ARMOR = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(1.0D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		LEGGINGS.ADD_ARMOR_TOUGHNESS = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(2.0D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		LEGGINGS.ADD_KNOCKBACK_RESISTANCE = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.2D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		LEGGINGS.ADD_DURABILITY = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(500, PooledAttribute.Rolls.ofConstant(1), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		LEGGINGS.EXTRA_PARRY_CHANCE = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(0.05F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		LEGGINGS.EXTRA_HEALTH = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(2.0F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		LEGGINGS.EXTRA_EFFECTS = (EffectAttribute.Generator)EffectAttribute.generator()
				.add(new ArrayList<>(), PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(Collections.singletonList(new EffectTalent(0, Effects.RESISTANCE, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 2)
						.add(Collections.singletonList(new EffectTalent(0, Effects.RESISTANCE, 2, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 1)
						.add(Collections.singletonList(new EffectTalent(0, Effects.LUCK, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 2)
						.add(Collections.singletonList(new EffectTalent(0, Effects.LUCK, 2, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 1))
				.collect(EffectAttribute.of(EffectAttribute.Type.MERGE));

		//============================================================================================================//

		BOOTS.ADD_ARMOR = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(1.0D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		BOOTS.ADD_ARMOR_TOUGHNESS = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(2.0D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		BOOTS.ADD_KNOCKBACK_RESISTANCE = (DoubleAttribute.Generator)DoubleAttribute.generator()
				.add(0.2D, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(DoubleAttribute.of(NumberAttribute.Type.SET));

		BOOTS.ADD_DURABILITY = (IntegerAttribute.Generator)IntegerAttribute.generator()
				.add(500, PooledAttribute.Rolls.ofConstant(1), pool -> {})
				.collect(IntegerAttribute.of(NumberAttribute.Type.SET));

		BOOTS.EXTRA_PARRY_CHANCE = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(0.05F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		BOOTS.EXTRA_HEALTH = (FloatAttribute.Generator)FloatAttribute.generator()
				.add(2.0F, PooledAttribute.Rolls.ofEmpty(), pool -> {})
				.collect(FloatAttribute.of(NumberAttribute.Type.SET));

		BOOTS.EXTRA_EFFECTS = (EffectAttribute.Generator)EffectAttribute.generator()
				.add(new ArrayList<>(), PooledAttribute.Rolls.ofConstant(1), pool -> pool
						.add(Collections.singletonList(new EffectTalent(0, Effects.RESISTANCE, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 2)
						.add(Collections.singletonList(new EffectTalent(0, Effects.RESISTANCE, 2, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 1)
						.add(Collections.singletonList(new EffectTalent(0, Effects.LUCK, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 2)
						.add(Collections.singletonList(new EffectTalent(0, Effects.LUCK, 2, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)),
								EffectAttribute.of(EffectAttribute.Type.SET), 1))
				.collect(EffectAttribute.of(EffectAttribute.Type.MERGE));

		//============================================================================================================//

		this.BASE_MODIFIERS = new LinkedHashMap<>();
		this.BASE_MODIFIERS.put(ModItems.SWORD.getRegistryName().toString(), SWORD);
		this.BASE_MODIFIERS.put(ModItems.AXE.getRegistryName().toString(), AXE);
		this.BASE_MODIFIERS.put(ModItems.DAGGER.getRegistryName().toString(), DAGGER);
		this.BASE_MODIFIERS.put(ModItems.HELMET.getRegistryName().toString(), HELMET);
		this.BASE_MODIFIERS.put(ModItems.CHESTPLATE.getRegistryName().toString(), CHESTPLATE);
		this.BASE_MODIFIERS.put(ModItems.LEGGINGS.getRegistryName().toString(), LEGGINGS);
		this.BASE_MODIFIERS.put(ModItems.BOOTS.getRegistryName().toString(), BOOTS);
		this.BASE_MODIFIERS.put(ModItems.ETCHING.getRegistryName().toString(), ETCHING);
	}

	public static class BaseAttributes {
		@Expose public DoubleAttribute.Generator ARMOR;
		@Expose public DoubleAttribute.Generator ARMOR_TOUGHNESS;
		@Expose public DoubleAttribute.Generator KNOCKBACK_RESISTANCE;
		@Expose public DoubleAttribute.Generator ATTACK_DAMAGE;
		@Expose public DoubleAttribute.Generator ATTACK_SPEED;
		@Expose public IntegerAttribute.Generator DURABILITY;
		@Expose public EnumAttribute.Generator<VaultGear.Set> GEAR_SET;
		@Expose public FloatAttribute.Generator GEAR_LEVEL_CHANCE;
		@Expose public IntegerAttribute.Generator GEAR_MAX_LEVEL;
		@Expose public IntegerAttribute.Generator GEAR_MODIFIERS_TO_ROLL;
		@Expose public IntegerAttribute.Generator MAX_REPAIRS;
		@Expose public IntegerAttribute.Generator MIN_VAULT_LEVEL;

		public void initialize(ItemStack stack, Random random) {
			if(this.ARMOR != null)ModAttributes.ARMOR.create(stack, random, this.ARMOR);
			if(this.ARMOR_TOUGHNESS != null)ModAttributes.ARMOR_TOUGHNESS.create(stack, random, this.ARMOR_TOUGHNESS);
			if(this.KNOCKBACK_RESISTANCE != null)ModAttributes.KNOCKBACK_RESISTANCE.create(stack, random, this.KNOCKBACK_RESISTANCE);
			if(this.ATTACK_DAMAGE != null)ModAttributes.ATTACK_DAMAGE.create(stack, random, this.ATTACK_DAMAGE);
			if(this.ATTACK_SPEED != null)ModAttributes.ATTACK_SPEED.create(stack, random, this.ATTACK_SPEED);
			if(this.DURABILITY != null)ModAttributes.DURABILITY.create(stack, random, this.DURABILITY);
			if(this.GEAR_SET != null)ModAttributes.GEAR_SET.create(stack, random, this.GEAR_SET);
			if(this.GEAR_LEVEL_CHANCE != null)ModAttributes.GEAR_LEVEL_CHANCE.create(stack, random, this.GEAR_LEVEL_CHANCE);
			if(this.GEAR_MAX_LEVEL != null)ModAttributes.GEAR_MAX_LEVEL.create(stack, random, this.GEAR_MAX_LEVEL);
			if(this.GEAR_MODIFIERS_TO_ROLL != null)ModAttributes.GEAR_MODIFIERS_TO_ROLL.create(stack, random, this.GEAR_MODIFIERS_TO_ROLL);
			if(this.MAX_REPAIRS != null)ModAttributes.MAX_REPAIRS.create(stack, random, this.MAX_REPAIRS);
			if(this.MIN_VAULT_LEVEL != null)ModAttributes.MIN_VAULT_LEVEL.create(stack, random, this.MIN_VAULT_LEVEL);
		}
	}

	public static class BaseModifiers {
		@Expose public DoubleAttribute.Generator ADD_ARMOR;
		@Expose public DoubleAttribute.Generator ADD_ARMOR_TOUGHNESS;
		@Expose public DoubleAttribute.Generator ADD_KNOCKBACK_RESISTANCE;
		@Expose public DoubleAttribute.Generator ADD_ATTACK_DAMAGE;
		@Expose public DoubleAttribute.Generator ADD_ATTACK_SPEED;
		@Expose public IntegerAttribute.Generator ADD_DURABILITY;
		@Expose public FloatAttribute.Generator EXTRA_LEECH_RATIO;
		@Expose public FloatAttribute.Generator EXTRA_PARRY_CHANCE;
		@Expose public FloatAttribute.Generator EXTRA_HEALTH;
		@Expose public EffectAttribute.Generator EXTRA_EFFECTS;

		public void initialize(ItemStack stack, Random random) {
			int rolls = ModAttributes.GEAR_MODIFIERS_TO_ROLL.getOrDefault(stack, 0).getValue(stack);
			if(rolls == 0)return;

			List<ItemAttribute.Instance.Generator<?>> generators = Arrays.asList(
					this.ADD_ARMOR, this.ADD_ARMOR_TOUGHNESS, this.ADD_KNOCKBACK_RESISTANCE, this.ADD_ATTACK_DAMAGE,
					this.ADD_ATTACK_SPEED, this.ADD_DURABILITY, this.EXTRA_LEECH_RATIO,
					this.EXTRA_PARRY_CHANCE, this.EXTRA_HEALTH, this.EXTRA_EFFECTS);

			List<Boolean> existing = Arrays.asList(
					ModAttributes.ADD_ARMOR.exists(stack), ModAttributes.ADD_ARMOR_TOUGHNESS.exists(stack),
					ModAttributes.ADD_KNOCKBACK_RESISTANCE.exists(stack), ModAttributes.ADD_ATTACK_DAMAGE.exists(stack),
					ModAttributes.ADD_ATTACK_SPEED.exists(stack), ModAttributes.ADD_DURABILITY.exists(stack),
					ModAttributes.EXTRA_LEECH_RATIO.exists(stack), ModAttributes.EXTRA_PARRY_CHANCE.exists(stack),
					ModAttributes.EXTRA_HEALTH.exists(stack), ModAttributes.EXTRA_EFFECTS.exists(stack)
			);

			List<Integer> picked = IntStream.range(0, generators.size())
					.filter(i -> generators.get(i) != null)
					.filter(i -> !existing.get(i))
					.boxed()
					.collect(Collectors.toList());

			Collections.shuffle(picked, random);
			int added = Math.min(rolls, picked.size());

			for(int i = 0; i < added; i++) {
				if(this.ADD_ARMOR == generators.get(picked.get(i)))ModAttributes.ADD_ARMOR.create(stack, random, this.ADD_ARMOR);
				if(this.ADD_ARMOR_TOUGHNESS == generators.get(picked.get(i)))ModAttributes.ADD_ARMOR_TOUGHNESS.create(stack, random, this.ADD_ARMOR_TOUGHNESS);
				if(this.ADD_KNOCKBACK_RESISTANCE == generators.get(picked.get(i)))ModAttributes.ADD_KNOCKBACK_RESISTANCE.create(stack, random, this.ADD_KNOCKBACK_RESISTANCE);
				if(this.ADD_ATTACK_DAMAGE == generators.get(picked.get(i)))ModAttributes.ADD_ATTACK_DAMAGE.create(stack, random, this.ADD_ATTACK_DAMAGE);
				if(this.ADD_ATTACK_SPEED == generators.get(picked.get(i)))ModAttributes.ADD_ATTACK_SPEED.create(stack, random, this.ADD_ATTACK_SPEED);
				if(this.ADD_DURABILITY == generators.get(picked.get(i)))ModAttributes.ADD_DURABILITY.create(stack, random, this.ADD_DURABILITY);
				if(this.EXTRA_LEECH_RATIO == generators.get(picked.get(i)))ModAttributes.EXTRA_LEECH_RATIO.create(stack, random, this.EXTRA_LEECH_RATIO);
				if(this.EXTRA_PARRY_CHANCE == generators.get(picked.get(i)))ModAttributes.EXTRA_PARRY_CHANCE.create(stack, random, this.EXTRA_PARRY_CHANCE);
				if(this.EXTRA_HEALTH == generators.get(picked.get(i)))ModAttributes.EXTRA_HEALTH.create(stack, random, EXTRA_HEALTH);
				if(this.EXTRA_EFFECTS == generators.get(picked.get(i)))ModAttributes.EXTRA_EFFECTS.create(stack, random, this.EXTRA_EFFECTS);
			}

			ModAttributes.GEAR_MODIFIERS_TO_ROLL.create(stack, rolls - added);
		}
	}

	public static class Scrappy extends VaultGearConfig {
		@Override
		public String getName() {
			return "vault_gear_" + VaultGear.Rarity.SCRAPPY.name().toLowerCase();
		}

		@Override
		protected void reset() {
			super.reset();

			this.BASE_ATTRIBUTES.forEach((key, value) -> {
				if(ModItems.ETCHING.getRegistryName().toString().equals(key))return;
				value.GEAR_MODIFIERS_TO_ROLL = (IntegerAttribute.Generator)IntegerAttribute.generator()
						.add(0, PooledAttribute.Rolls.ofEmpty(), pool -> {})
						.collect(IntegerAttribute.of(NumberAttribute.Type.SET));
			});
		}
	}

	public static class Common extends VaultGearConfig {
		@Override
		public String getName() {
			return "vault_gear_" + VaultGear.Rarity.COMMON.name().toLowerCase();
		}

		@Override
		protected void reset() {
			super.reset();

			this.BASE_ATTRIBUTES.forEach((key, value) -> {
				if(ModItems.ETCHING.getRegistryName().toString().equals(key))return;
				value.GEAR_MODIFIERS_TO_ROLL = (IntegerAttribute.Generator)IntegerAttribute.generator()
						.add(1, PooledAttribute.Rolls.ofEmpty(), pool -> {})
						.collect(IntegerAttribute.of(NumberAttribute.Type.SET));
			});
		}
	}

	public static class Rare extends VaultGearConfig {
		@Override
		public String getName() {
			return "vault_gear_" + VaultGear.Rarity.RARE.name().toLowerCase();
		}

		@Override
		protected void reset() {
			super.reset();

			this.BASE_ATTRIBUTES.forEach((key, value) -> {
				if(ModItems.ETCHING.getRegistryName().toString().equals(key))return;
				value.GEAR_MODIFIERS_TO_ROLL = (IntegerAttribute.Generator)IntegerAttribute.generator()
						.add(1, PooledAttribute.Rolls.ofEmpty(), pool -> {})
						.collect(IntegerAttribute.of(NumberAttribute.Type.SET));
			});
		}
	}

	public static class Epic extends VaultGearConfig {
		@Override
		public String getName() {
			return "vault_gear_" + VaultGear.Rarity.EPIC.name().toLowerCase();
		}

		@Override
		protected void reset() {
			super.reset();

			this.BASE_ATTRIBUTES.forEach((key, value) -> {
				if(ModItems.ETCHING.getRegistryName().toString().equals(key))return;
				value.GEAR_MODIFIERS_TO_ROLL = (IntegerAttribute.Generator)IntegerAttribute.generator()
						.add(2, PooledAttribute.Rolls.ofEmpty(), pool -> {})
						.collect(IntegerAttribute.of(NumberAttribute.Type.SET));
			});
		}
	}

	public static class Omega extends VaultGearConfig {
		@Override
		public String getName() {
			return "vault_gear_" + VaultGear.Rarity.OMEGA.name().toLowerCase();
		}

		@Override
		protected void reset() {
			super.reset();

			this.BASE_ATTRIBUTES.forEach((key, value) -> {
				if(ModItems.ETCHING.getRegistryName().toString().equals(key))return;
				value.GEAR_MODIFIERS_TO_ROLL = (IntegerAttribute.Generator)IntegerAttribute.generator()
						.add(2, PooledAttribute.Rolls.ofEmpty(), pool -> {})
						.collect(IntegerAttribute.of(NumberAttribute.Type.SET));
			});
		}
	}

	public static class General extends VaultGearConfig {
		@Expose public Map<String, WeightedList<VaultGear.Rarity>> ROLLS;

		@Override
		public String getName() {
			return "vault_gear";
		}

		@Override
		protected void reset() {
			this.ROLLS = new LinkedHashMap<>();
			this.ROLLS.put(VaultGear.RollType.SCRAPPY_ONLY.name(), new WeightedList<VaultGear.Rarity>()
					.add(VaultGear.Rarity.SCRAPPY, 1)
					.strip());

			this.ROLLS.put(VaultGear.RollType.TREASURE_ONLY.name(), new WeightedList<VaultGear.Rarity>()
					.add(VaultGear.Rarity.COMMON, 1)
					.add(VaultGear.Rarity.RARE, 1)
					.add(VaultGear.Rarity.EPIC, 1)
					.add(VaultGear.Rarity.OMEGA, 1)
					.strip());

			this.ROLLS.put(VaultGear.RollType.ALL.name(), new WeightedList<VaultGear.Rarity>()
					.add(VaultGear.Rarity.SCRAPPY, 1)
					.add(VaultGear.Rarity.COMMON, 1)
					.add(VaultGear.Rarity.RARE, 1)
					.add(VaultGear.Rarity.EPIC, 1)
					.add(VaultGear.Rarity.OMEGA, 1)
					.strip());
		}
	}

}
