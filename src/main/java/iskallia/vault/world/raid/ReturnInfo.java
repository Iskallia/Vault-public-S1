package iskallia.vault.world.raid;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public class ReturnInfo implements INBTSerializable<CompoundNBT> {

	private Vector3d position;
	private float yaw;
	private float pitch;
	private GameType gamemode;
	private RegistryKey<World> dimension;

	public ReturnInfo() {
		this(Vector3d.ZERO, 0.0F, 0.0F, GameType.NOT_SET, ServerWorld.OVERWORLD);
	}

	public ReturnInfo(ServerPlayerEntity player) {
		this(player.getPositionVec(), player.rotationYaw, player.rotationPitch, player.interactionManager.getGameType(), player.world.getDimensionKey());
	}

	public ReturnInfo(Vector3d position, float yaw, float pitch, GameType gamemode, RegistryKey<World> dimension) {
		this.position = position;
		this.yaw = yaw;
		this.pitch = pitch;
		this.gamemode = gamemode;
		this.dimension = dimension;
	}

	public void apply(MinecraftServer server, ServerPlayerEntity player) {
		ServerWorld world = server.getWorld(this.dimension);
		player.teleport(world, this.position.x, this.position.y, this.position.z, this.yaw, this.pitch);
		player.setGameType(this.gamemode);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putDouble("PosX", this.position.x);
		nbt.putDouble("PosY", this.position.y);
		nbt.putDouble("PosZ", this.position.z);
		nbt.putFloat("Yaw", this.yaw);
		nbt.putFloat("Pitch", this.pitch);
		nbt.putInt("Gamemode", this.gamemode.ordinal());
		nbt.putString("Dimension", this.dimension.getLocation().toString()); //TODO: debug
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.position = new Vector3d(nbt.getDouble("PosX"), nbt.getDouble("PosY"), nbt.getDouble("PosZ"));
		this.yaw = nbt.getFloat("Yaw");
		this.pitch = nbt.getFloat("Pitch");
		this.gamemode = GameType.getByID(nbt.getInt("Gamemode"));
		this.dimension = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(nbt.getString("Dimension")));
	}

}
