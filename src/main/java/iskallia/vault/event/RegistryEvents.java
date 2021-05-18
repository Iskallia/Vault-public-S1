package iskallia.vault.event;

import iskallia.vault.init.*;
import iskallia.vault.util.RelicSet;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        ModBlocks.registerBlocks(event);
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        ModItems.registerItems(event);
        ModBlocks.registerBlockItems(event);
        RelicSet.register();
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        ModModels.setupRenderLayers();
        ModModels.ItemProperty.register();
        ModModels.GearModel.register();
    }

    @SubscribeEvent
    public static void onSoundRegister(RegistryEvent.Register<SoundEvent> event) {
        ModSounds.registerSounds(event);
        ModSounds.registerSoundTypes();
    }

    @SubscribeEvent
    public static void onStructureRegister(RegistryEvent.Register<Structure<?>> event) {
        ModStructures.register(event);
        ModFeatures.registerStructureFeatures();
    }

    @SubscribeEvent
    public static void onFeatureRegister(RegistryEvent.Register<Feature<?>> event) {
        ModFeatures.registerFeatures(event);
    }

    @SubscribeEvent
    public static void onContainerRegister(RegistryEvent.Register<ContainerType<?>> event) {
        ModContainers.register(event);
    }

    @SubscribeEvent
    public static void onEntityRegister(RegistryEvent.Register<EntityType<?>> event) {
        ModEntities.register(event);
    }

    @SubscribeEvent
    public static void onTileEntityRegister(RegistryEvent.Register<TileEntityType<?>> event) {
        ModBlocks.registerTileEntities(event);
    }

    @SubscribeEvent
    public static void onRecipeRegister(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        ModRecipes.registerRecipes(event);
        ModRecipes.Serializer.register(event);
    }

    @SubscribeEvent
    public static void onEffectRegister(RegistryEvent.Register<Effect> event) {
        ModEffects.register(event);
    }

    @SubscribeEvent
    public static void onAttributeRegister(RegistryEvent.Register<Attribute> event) {
        ModAttributes.register(event);
    }

}
