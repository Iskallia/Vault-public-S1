package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.world.gen.structure.ArenaStructure;
import iskallia.vault.world.gen.structure.VaultStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModStructures {

    public static Structure<VaultStructure.Config> VAULT;
    public static Structure<ArenaStructure.Config> ARENA;

    public static void register(RegistryEvent.Register<Structure<?>> event) {
        VAULT = register(event.getRegistry(), "vault", new VaultStructure(VaultStructure.Config.CODEC));
        ARENA = register(event.getRegistry(), "arena", new ArenaStructure(ArenaStructure.Config.CODEC));
    }

    private static <T extends Structure<?>> T register(IForgeRegistry<Structure<?>> registry, String name, T structure) {
        Structure.field_236365_a_.put(name, structure);
        structure.setRegistryName(Vault.id(name));
        registry.register(structure);
        return structure;
    }

}
