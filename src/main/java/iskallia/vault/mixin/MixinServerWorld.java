package iskallia.vault.mixin;

import iskallia.vault.Vault;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ISpawnWorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld extends World {

	protected MixinServerWorld(ISpawnWorldInfo worldInfo, RegistryKey<World> dimension, DimensionType dimensionType, Supplier<IProfiler> profiler, boolean isRemote, boolean isDebug, long seed) {
		super(worldInfo, dimension, dimensionType, profiler, isRemote, isDebug, seed);
	}

	@Inject(method = "tickEnvironment", at = @At("HEAD"), cancellable = true)
	public void tickEnvironment(Chunk chunk, int randomTickSpeed, CallbackInfo ci) {
		if(this.getDimensionKey() == Vault.VAULT_KEY) {
			ci.cancel();
		}
	}

}
