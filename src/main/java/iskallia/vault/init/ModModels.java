package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.item.gear.model.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class ModModels {

    public static void setupRenderLayers() {
        RenderTypeLookup.setRenderLayer(ModBlocks.VAULT_PORTAL, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.ALEXANDRITE_DOOR, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.BENITOITE_DOOR, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.LARIMAR_DOOR, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.BLACK_OPAL_DOOR, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.PAINITE_DOOR, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ISKALLIUM_DOOR, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.RENIUM_DOOR, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.GORGINITE_DOOR, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.SPARKLETINE_DOOR, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.WUTODIE_DOOR, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.VAULT_ALTAR, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_1, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_2, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_3, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_4, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_5, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_6, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_7, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_8, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_9, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_10, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_11, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_12, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_13, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_14, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_15, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ARTIFACT_16, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.MVP_CROWN, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.VENDING_MACHINE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ADVANCED_VENDING_MACHINE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.GLOBAL_TRADER, RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(ModBlocks.CRYO_CHAMBER, CustomRenderType.INSTANCE);
        RenderTypeLookup.setRenderLayer(ModBlocks.CRYO_CHAMBER, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.KEY_PRESS, RenderType.getCutout());
    }

    @SuppressWarnings({"unchecked"})
    public static void registerItemColors(ItemColors colors) {
        colors.register((stack, color) -> color > 0 ? -1 : ((IDyeableArmorItem) stack.getItem()).getColor(stack),
                ModItems.HELMET, ModItems.CHESTPLATE, ModItems.LEGGINGS, ModItems.BOOTS);

        colors.register((stack, color) -> color > 0 ? -1 : (((VaultGear<Item>) stack.getItem())).getColor(stack.getItem(), stack),
                ModItems.AXE, ModItems.SWORD, ModItems.DAGGER);
    }

    private static class CustomRenderType extends RenderType {
        // TODO: Do dis, so Cryo Chamber renders correctly :c
        private static final RenderType INSTANCE = makeType("cutout_ignoring_normals",
                DefaultVertexFormats.BLOCK, 7, 131072,
                true, false,
                RenderType.State.getBuilder()
                        .shadeModel(SHADE_ENABLED)
                        .lightmap(LIGHTMAP_ENABLED)
                        .texture(BLOCK_SHEET)
                        .alpha(HALF_ALPHA)
                        .build(true)
        );

        public CustomRenderType(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
            super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
        }
    }

    public static class ItemProperty {
        public static IItemPropertyGetter GEAR_TEXTURE = (stack, world, entity) -> {
            return (float) ModAttributes.GEAR_MODEL.getOrDefault(stack, -1).getValue(stack);
        };

        public static IItemPropertyGetter GEAR_RARITY = (stack, world, entity) -> {
            return (float) ModAttributes.GEAR_RARITY.get(stack).map(attribute -> attribute.getValue(stack)).map(Enum::ordinal).orElse(-1);
        };

        public static IItemPropertyGetter ETCHING = (stack, world, entity) -> {
            return (float) ModAttributes.GEAR_SET.get(stack).map(attribute -> attribute.getValue(stack)).map(Enum::ordinal).orElse(-1);
        };

        public static IItemPropertyGetter PUZZLE_COLOR = (stack, world, entity) -> {
            return (float) ModAttributes.PUZZLE_COLOR.get(stack).map(attribute -> attribute.getValue(stack)).map(Enum::ordinal).orElse(-1);
        };

        public static void register() {
            registerItemProperty(ModItems.SWORD, "texture", GEAR_TEXTURE);
            registerItemProperty(ModItems.AXE, "texture", GEAR_TEXTURE);
            registerItemProperty(ModItems.DAGGER, "texture", GEAR_TEXTURE);
            registerItemProperty(ModItems.HELMET, "texture", GEAR_TEXTURE);
            registerItemProperty(ModItems.CHESTPLATE, "texture", GEAR_TEXTURE);
            registerItemProperty(ModItems.LEGGINGS, "texture", GEAR_TEXTURE);
            registerItemProperty(ModItems.BOOTS, "texture", GEAR_TEXTURE);
            registerItemProperty(ModItems.ETCHING, "texture", GEAR_TEXTURE);

            registerItemProperty(ModItems.SWORD, "vault_rarity", GEAR_RARITY);
            registerItemProperty(ModItems.AXE, "vault_rarity", GEAR_RARITY);
            registerItemProperty(ModItems.DAGGER, "vault_rarity", GEAR_RARITY);
            registerItemProperty(ModItems.HELMET, "vault_rarity", GEAR_RARITY);
            registerItemProperty(ModItems.CHESTPLATE, "vault_rarity", GEAR_RARITY);
            registerItemProperty(ModItems.LEGGINGS, "vault_rarity", GEAR_RARITY);
            registerItemProperty(ModItems.BOOTS, "vault_rarity", GEAR_RARITY);
            registerItemProperty(ModItems.ETCHING, "vault_rarity", GEAR_RARITY);

            registerItemProperty(ModItems.ETCHING, "vault_set", ETCHING);

            registerItemProperty(ModItems.PUZZLE_RUNE, "puzzle_color", PUZZLE_COLOR);
            registerItemProperty(ModBlocks.PUZZLE_RUNE_BLOCK_ITEM, "puzzle_color", PUZZLE_COLOR);
        }

        public static void registerItemProperty(Item item, String name, IItemPropertyGetter property) {
            ItemModelsProperties.registerProperty(item, Vault.id(name), property);
        }

    }

    public static class GearModel {
        public static Map<Integer, GearModel> REGISTRY;
        public static GearModel SCRAPPY;
        public static GearModel SAMURAI;
        public static GearModel KNIGHT;
        public static GearModel GUARD;
        public static GearModel DRAGON;
        public static GearModel PLATED_1;
        public static GearModel PLATED_1_DARK;
        public static GearModel PLATED_2;
        public static GearModel PLATED_2_DARK;
        public static GearModel PLATED_3;
        public static GearModel PLATED_3_DARK;
        public static GearModel PLATED_4;
        public static GearModel PLATED_4_DARK;

        public static void register() {
            REGISTRY = new HashMap<>();
            SCRAPPY = register("Scrappy", null);                       //0
            SAMURAI = register("Samurai", SamuraiArmorModel.class);             //1
            KNIGHT = register("Knight", KnightArmorModel.class);                //2
            GUARD = register("Guard", GuardArmorModel.class);                   //3
            DRAGON = register("Dragon", DragonArmorModel.class);                //4
            PLATED_1 = register("Plated 1", Plated1ArmorModel.class);           //5
            PLATED_1_DARK = register("Plated 1 Dark", Plated1ArmorModel.class); //6
            PLATED_2 = register("Plated 2", Plated2ArmorModel.class);           //7
            PLATED_2_DARK = register("Plated 2 Dark", Plated2ArmorModel.class); //8
            PLATED_3 = register("Plated 3", Plated3ArmorModel.class);           //9
            PLATED_3_DARK = register("Plated 3 Dark", Plated3ArmorModel.class); //10
            PLATED_4 = register("Plated 4", Plated4ArmorModel.class);           //11
            PLATED_4_DARK = register("Plated 4 Dark", Plated4ArmorModel.class); //12
        }

        String displayName;
        VaultGearModel<? extends LivingEntity> helmetModel;
        VaultGearModel<? extends LivingEntity> chestplateModel;
        VaultGearModel<? extends LivingEntity> leggingsModel;
        VaultGearModel<? extends LivingEntity> bootsModel;

        public VaultGearModel<? extends LivingEntity> forSlotType(EquipmentSlotType slotType) {
            switch (slotType) {
                case HEAD:
                    return this.helmetModel;
                case CHEST:
                    return this.chestplateModel;
                case LEGS:
                    return this.leggingsModel;
                case FEET:
                default:
                    return this.bootsModel;
            }
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getTextureName(EquipmentSlotType slotType, String type) {
            String base = Vault.sId("textures/models/armor/" + this.displayName.toLowerCase().replace(" ", "_") + "_armor")
                    + (slotType == EquipmentSlotType.LEGS ? "_layer2" : "_layer1");
            return (type == null ? base : base + "_" + type) + ".png";
        }

        private static <T extends VaultGearModel<?>> GearModel register(String textureName, Class<T> modelClass) {
            try {
                GearModel gearModel = new GearModel();
                gearModel.displayName = textureName;
                if (modelClass != null) { // Only used by Scrappy armor
                    Constructor<T> constructor = modelClass.getConstructor(float.class, EquipmentSlotType.class);
                    T helmetModel = constructor.newInstance((float) 1.0, EquipmentSlotType.HEAD);
                    T chestplateModel = constructor.newInstance((float) 1.0, EquipmentSlotType.CHEST);
                    T leggingsModel = constructor.newInstance((float) 1.0, EquipmentSlotType.LEGS);
                    T bootsModel = constructor.newInstance((float) 1.0, EquipmentSlotType.FEET);
                    gearModel.helmetModel = helmetModel;
                    gearModel.chestplateModel = chestplateModel;
                    gearModel.leggingsModel = leggingsModel;
                    gearModel.bootsModel = bootsModel;
                    REGISTRY.put(REGISTRY.size(), gearModel);
                }
                return gearModel;

            } catch (Exception e) {
                throw new InternalError("Error while registering Gear Model: " + modelClass.getSimpleName());
            }

        }
    }

}
