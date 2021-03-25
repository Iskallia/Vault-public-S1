package iskallia.vault.world.raid.modifier;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public class VaultModifiers implements INBTSerializable<CompoundNBT> {

	public static VaultModifiers CLIENT;

	private VaultRaid raid;
	private List<VaultModifier> modifiers = new ArrayList<>();

	private VaultModifiers() {

	}

	public VaultModifiers(VaultRaid raid) {
		this.raid = raid;
	}

	public void generate(Random random, int level, boolean raffle) {
		this.modifiers.addAll(ModConfigs.VAULT_MODIFIERS.getRandom(VaultRarity.values()[raid.rarity], random, level, raffle));
	}

	public void apply() {
		this.modifiers.forEach(modifier -> modifier.apply(this.raid));
	}

	public void tick(ServerWorld world, PlayerEntity player) {
		this.modifiers.forEach(modifier -> modifier.tick(world, player));
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		ListNBT modifiersList = new ListNBT();
		this.modifiers.forEach(group -> modifiersList.add(StringNBT.valueOf(group.getName())));
		nbt.put("Modifiers", modifiersList);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.modifiers.clear();
		ListNBT modifiersList = nbt.getList("Modifiers", Constants.NBT.TAG_STRING);

		for(int i = 0; i < modifiersList.size(); i++) {
			this.modifiers.add(ModConfigs.VAULT_MODIFIERS.getByName(modifiersList.getString(i)));
		}
	}

	public void encode(PacketBuffer buffer) {
		buffer.writeInt(this.modifiers.size());
		this.modifiers.forEach(group -> buffer.writeUtf(group.getName()));
	}

	public static VaultModifiers decode(PacketBuffer buffer) {
		VaultModifiers res = new VaultModifiers();

		for(int i = 0, count = buffer.readInt(); i < count; i++) {
			res.modifiers.add(ModConfigs.VAULT_MODIFIERS.getByName(buffer.readUtf()));
		}

		return res;
	}

	public void forEach(BiConsumer<Integer, VaultModifier> consumer) {
		for(int i = 0; i < this.modifiers.size(); i++) {
			consumer.accept(i, this.modifiers.get(i));
		}
	}

	public int size() {
		return this.modifiers.size();
	}

	public void add(String name) {
		VaultModifier modifier = ModConfigs.VAULT_MODIFIERS.getByName(name);

		if(!this.modifiers.contains(modifier)) {
			this.modifiers.add(modifier);
		}
	}

	public void remove(String name) {
		this.modifiers.removeIf(modifier -> modifier.getName().equals(name));
	}

}
