package iskallia.vault.item;

import iskallia.vault.world.raid.VaultRaid;
import iskallia.vault.world.raid.modifier.VaultModifiers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CrystalData implements INBTSerializable<CompoundNBT> {

	private final ItemStack delegate;
	protected List<Modifier> modifiers = new ArrayList<>();

	public CrystalData(ItemStack delegate) {
		this.delegate = delegate;

		if(this.delegate != null) {
			this.deserializeNBT(this.delegate.getOrCreateChildTag("CrystalData"));
		}
	}

	public ItemStack getDelegate() {
		return this.delegate;
	}

	public void updateDelegate() {
		if(this.delegate != null) {
			this.delegate.getOrCreateTag().put("CrystalData", this.serializeNBT());
		}
	}

	public boolean addModifier(String name, Modifier.Operation operation, float chance) {
		Iterator<Modifier> it = this.modifiers.iterator();

		while(it.hasNext()) {
			Modifier modifier = it.next();

			if(modifier.name.equals(name) && modifier.operation == operation) {
				float oldValue = modifier.chance;
				float newValue = MathHelper.clamp(oldValue + chance, 0.0F, 1.0F);
				if(oldValue == newValue)return false;
				chance = newValue;
				it.remove();
			}
		}

		this.modifiers.add(new Modifier(name, operation, MathHelper.clamp(chance, 0.0F, 1.0F)));
		this.updateDelegate();
		return true;
	}

	public void apply(VaultRaid raid, Random random) {
		this.modifiers.forEach(modifier -> modifier.apply(raid.modifiers, random));
	}

	public void addInformation(World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		for(Modifier modifier: this.modifiers) {
			tooltip.add(new StringTextComponent("- Has ")
					.append(new StringTextComponent(Math.round(modifier.chance * 100.0F) + "%").mergeStyle(modifier.operation.color))
					.append(new StringTextComponent(" chance to "))
					.append(new StringTextComponent(modifier.operation.title).mergeStyle(modifier.operation.color))
					.append(new StringTextComponent(" the "))
					.append(new StringTextComponent(modifier.name).mergeStyle(TextFormatting.AQUA))
					.append(new StringTextComponent(" modifier!")));
		}
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();

		ListNBT modifiersList = new ListNBT();
		this.modifiers.forEach(modifier -> modifiersList.add(modifier.toNBT()));
		nbt.put("Modifiers", modifiersList);

		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.modifiers.clear();
		ListNBT modifiersList = nbt.getList("Modifiers", Constants.NBT.TAG_COMPOUND);
		modifiersList.forEach(inbt -> this.modifiers.add(Modifier.fromNBT((CompoundNBT)inbt)));
	}

	public static class Modifier {
		public final String name;
		public final Operation operation;
		public final float chance;

		public Modifier(String name, Operation operation, float chance) {
			this.name = name;
			this.operation = operation;
			this.chance = chance;
		}

		public void apply(VaultModifiers modifiers, Random random) {
			if(this.operation == Modifier.Operation.ADD) {
				if(random.nextFloat() < this.chance) {
					modifiers.add(this.name);
				}
			} else if(this.operation == Operation.REMOVE) {
				if(random.nextFloat() < this.chance) {
					modifiers.remove(this.name);
				}
			}
		}

		public CompoundNBT toNBT() {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putString("Name", this.name);
			nbt.putInt("Operation", this.operation.ordinal());
			nbt.putFloat("Chance", this.chance);
			return nbt;
		}

		public static Modifier fromNBT(CompoundNBT nbt) {
			return new Modifier(nbt.getString("Name"), Operation.values()[nbt.getInt("Operation")], nbt.getFloat("Chance"));
		}

		public enum Operation {
			ADD("add", TextFormatting.GREEN),
			REMOVE("cancel", TextFormatting.RED);

			public final String title;
			private final TextFormatting color;

			Operation(String title, TextFormatting color) {
				this.title = title;
				this.color = color;
			}
		}
	}

}
