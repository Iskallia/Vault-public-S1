package iskallia.vault.init;

import com.mojang.serialization.Codec;
import iskallia.vault.Vault;
import iskallia.vault.world.gen.structure.ArenaStructure;
import iskallia.vault.world.gen.structure.VaultStructure;
import iskallia.vault.world.gen.structure.pool.PalettedListPoolElement;
import iskallia.vault.world.gen.structure.pool.PalettedSinglePoolElement;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModStructures {

    public static Structure<VaultStructure.Config> VAULT;
    public static Structure<ArenaStructure.Config> ARENA;

    public static void register(RegistryEvent.Register<Structure<?>> event) {
        VAULT = register(event.getRegistry(), "vault", new VaultStructure(VaultStructure.Config.CODEC));
        ARENA = register(event.getRegistry(), "arena", new ArenaStructure(ArenaStructure.Config.CODEC));
        PoolElements.register(event);
    }

    private static <T extends Structure<?>> T register(IForgeRegistry<Structure<?>> registry, String name, T structure) {
        Structure.STRUCTURES_REGISTRY.put(name, structure);
        structure.setRegistryName(Vault.id(name));
        registry.register(structure);
        return structure;
    }

    public static class PoolElements {
        public static IJigsawDeserializer<PalettedSinglePoolElement> PALETTED_SINGLE_POOL_ELEMENT;
        public static IJigsawDeserializer<PalettedListPoolElement> PALETTED_LIST_POOL_ELEMENT;

        //No event for registering IJigsawDeserializer?
        public static void register(RegistryEvent.Register<Structure<?>> event) {
            PALETTED_SINGLE_POOL_ELEMENT = register("paletted_single_pool_element", PalettedSinglePoolElement.CODEC);
            PALETTED_LIST_POOL_ELEMENT  = register("paletted_list_pool_element", PalettedListPoolElement.CODEC);
        }

        static <P extends JigsawPiece> IJigsawDeserializer<P> register(String name, Codec<P> codec) {
            return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, Vault.id(name), () -> codec);
        }
    }

}
