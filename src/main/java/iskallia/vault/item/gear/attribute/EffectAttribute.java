package iskallia.vault.item.gear.attribute;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.type.EffectTalent;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class EffectAttribute extends PooledAttribute<List<EffectTalent>> {

	public EffectAttribute() {

	}

	public EffectAttribute(ItemAttribute.Modifier<List<EffectTalent>> modifier) {
		super(modifier);
	}

	@Override
	public void write(CompoundNBT nbt) {
		if(this.getBaseValue() == null)return;
		CompoundNBT tag = new CompoundNBT();
		ListNBT effectsList = new ListNBT();

		this.getBaseValue().forEach(effect -> {
			CompoundNBT effectTag = new CompoundNBT();
			tag.putString("Id", effect.getEffect().getRegistryName().toString());
			tag.putInt("Amplifier", effect.getAmplifier());
			tag.putString("Type", effect.getType().name);
			tag.putString("Operator", effect.getOperator().name);
			effectsList.add(effectTag);
		});

		tag.put("Effects", effectsList);
		nbt.put("BaseValue", tag);
	}

	@Override
	public void read(CompoundNBT nbt) {
		if(!nbt.contains("BaseValue", Constants.NBT.TAG_COMPOUND)) {
			this.setBaseValue(new ArrayList<>());
			return;
		}

		CompoundNBT tag = nbt.getCompound("BaseValue");
		ListNBT effectsList = tag.getList("Effects", Constants.NBT.TAG_COMPOUND);

		this.setBaseValue(effectsList.stream()
				.map(inbt -> (CompoundNBT)inbt)
				.map(compoundNBT -> new EffectTalent(0, tag.getString("Id"), tag.getInt("Amplifier"),
						tag.getString("Type"), tag.getString("Operator")))
				.collect(Collectors.toList()));
	}

	public static EffectAttribute.Generator generator() {
		return new EffectAttribute.Generator();
	}

	public static EffectAttribute.Generator.Operator of(EffectAttribute.Type type) {
		return new EffectAttribute.Generator.Operator(type);
	}

	public static class Generator extends PooledAttribute.Generator<List<EffectTalent>, Generator.Operator> {
		@Override
		public List<EffectTalent> getDefaultValue(Random random) {
			return new ArrayList<>();
		}

		public static class Operator extends PooledAttribute.Generator.Operator<List<EffectTalent>> {
			@Expose protected String type;

			public Operator(Type type) {
				this.type = type.name();
			}

			public Type getType() {
				return Type.getByName(this.type).orElseThrow(() -> new IllegalStateException("Unknown type \"" + this.type + "\""));
			}

			@Override
			public List<EffectTalent> apply(List<EffectTalent> value, List<EffectTalent> modifier) {
				if(this.getType() == Type.SET) {
					return modifier;
				} else if(this.getType() == Type.MERGE) {
					List<EffectTalent> res = new ArrayList<>(value);
					res.addAll(modifier);
					return res;
				}

				return value;
			}
		}
	}

	public enum Type {
		SET, MERGE;

		public static Optional<Type> getByName(String name) {
			for(Type value : Type.values()) {
				if(value.name().equalsIgnoreCase(name)) {
					return Optional.of(value);
				}
			}

			return Optional.empty();
		}
	}

}
