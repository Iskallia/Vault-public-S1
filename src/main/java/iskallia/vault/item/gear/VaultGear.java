package iskallia.vault.item.gear;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import iskallia.vault.config.VaultGearConfig;
import iskallia.vault.init.*;
import iskallia.vault.item.gear.attribute.DoubleAttribute;
import iskallia.vault.item.gear.attribute.EnumAttribute;
import iskallia.vault.item.gear.attribute.FloatAttribute;
import iskallia.vault.item.gear.attribute.IntegerAttribute;
import iskallia.vault.skill.set.PlayerSet;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.MendingEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public interface VaultGear<T extends Item> extends net.minecraftforge.common.extensions.IForgeItem {

    UUID[] ARMOR_MODIFIERS = new UUID[]{
            UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
            UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
            UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
    };

    int ROLL_TIME = 20 * 6;
    int ENTRIES_PER_ROLL = 50;

    int getModelsFor(Rarity rarity);

    default boolean isDamageable(T item, ItemStack stack) {
        return ModAttributes.DURABILITY.exists(stack);
    }

    default int getMaxDamage(T item, ItemStack stack, int maxDamage) {
        return ModAttributes.DURABILITY.getOrDefault(stack, maxDamage).getValue(stack);
    }

    default ITextComponent getDisplayName(T item, ItemStack stack, ITextComponent name) {
        if (ModAttributes.GEAR_STATE.getOrDefault(stack, State.UNIDENTIFIED).getValue(stack) == State.IDENTIFIED) {
            if (item == ModItems.ETCHING) {
                return name;

            } else {
                Rarity rarity = ModAttributes.GEAR_RARITY.getOrDefault(stack, Rarity.COMMON).getValue(stack);
                return ((IFormattableTextComponent) name).withStyle(rarity.color);
            }
        }

        TextComponent prefix = new StringTextComponent("Unidentified ");
        return prefix.setStyle(name.getStyle()).append(name);
    }

    default boolean canApply(ItemStack stack, Enchantment enchantment) {
        return !(enchantment instanceof MendingEnchantment);
    }

    default ActionResult<ItemStack> onItemRightClick(T item, World world, PlayerEntity player, Hand hand, ActionResult<ItemStack> result) {
        ItemStack stack = player.getItemInHand(hand);

        if(world.isClientSide) {
            if(stack.getItem() == ModItems.DAGGER && hand == Hand.OFF_HAND) {
                ((VaultDaggerItem)stack.getItem()).attackOffHand();
                return ActionResult.success(stack);
            }

            return result;
        }

        Optional<EnumAttribute<State>> attribute = ModAttributes.GEAR_STATE.get(stack);

        if(attribute.isPresent() && attribute.get().getValue(stack) == State.UNIDENTIFIED) {
            attribute.get().setBaseValue(State.ROLLING);
            return ActionResult.fail(stack);
        }

        return result;
    }

    default void inventoryTick(T item, ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if(world.isClientSide)return;

        if(ModAttributes.GEAR_STATE.getOrCreate(stack, State.UNIDENTIFIED).getValue(stack) == State.ROLLING) {
            this.tickRoll(item, stack, world, entity, itemSlot, isSelected);
        }

        if(!ModAttributes.GEAR_ROLL_TYPE.exists(stack)) {
            ModAttributes.GEAR_ROLL_TYPE.create(stack, RollType.ALL);
        }

        update(stack, world.getRandom());
    }

    default void tickRoll(T item, ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        int rollTicks = stack.getOrCreateTag().getInt("RollTicks");
        int lastModelHit = stack.getOrCreateTag().getInt("LastModelHit");
        double displacement = this.getDisplacement(rollTicks);

        if (rollTicks >= ROLL_TIME) {
            initialize(stack, world.getRandom());
            ModAttributes.GEAR_STATE.create(stack, State.IDENTIFIED);
            stack.getOrCreateTag().remove("RollTicks");
            stack.getOrCreateTag().remove("LastModelHit");
            world.playSound(null, entity.blockPosition(), ModSounds.CONFETTI_SFX, SoundCategory.PLAYERS, 0.5F, 1.0F);
            return;
        }

        if ((int) displacement != lastModelHit) {
            Rarity rarity = ModAttributes.GEAR_ROLL_TYPE.getOrCreate(stack, RollType.ALL).getValue(stack).get(world.random);
            ModAttributes.GEAR_RARITY.create(stack, rarity);
            ModAttributes.GEAR_MODEL.create(stack, world.random.nextInt(this.getModelsFor(rarity)));
            ModAttributes.GEAR_COLOR.create(stack, randomBaseColor(world.getRandom()));
            if (item == ModItems.ETCHING) {
                Set set = Set.values()[world.random.nextInt(Set.values().length)];
                ModAttributes.GEAR_SET.create(stack, set);
            }

            stack.getOrCreateTag().putInt("LastModelHit", (int) displacement);
            world.playSound(null, entity.blockPosition(), ModSounds.RAFFLE_SFX, SoundCategory.PLAYERS, 1.2F, 1.0F);
        }

        stack.getOrCreateTag().putInt("RollTicks", rollTicks + 1);
    }

    default double getDisplacement(int tick) {
        double c = 0.5D * ROLL_TIME * ROLL_TIME;

        return (-tick * tick * tick / 6.0D + c * tick) * ENTRIES_PER_ROLL
                / (-ROLL_TIME * ROLL_TIME * ROLL_TIME / 6.0 + c * ROLL_TIME);
    }

    default void addInformation(T item, ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        ModAttributes.GEAR_STATE.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(state -> {
            if(state == State.IDENTIFIED)return;
            ModAttributes.GEAR_ROLL_TYPE.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(roll -> {
                tooltip.add(new StringTextComponent("Roll: ").append(new StringTextComponent(roll.name()).withStyle(TextFormatting.GREEN)));
            });
        });

        ModAttributes.GEAR_RARITY.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(rarity -> {
            if (item == ModItems.ETCHING) return;
            tooltip.add(new StringTextComponent("Rarity: ").append(new StringTextComponent(rarity.name()).withStyle(rarity.color)));
        });

        ModAttributes.GEAR_SET.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(value -> {
            tooltip.add(new StringTextComponent(""));
            tooltip.add(new StringTextComponent("Etching: ").append(new StringTextComponent(value.name()).withStyle(TextFormatting.RED)));

            if (item == ModItems.ETCHING) {
                tooltip.add(new StringTextComponent(""));
                for (TextComponent descriptionLine : value.getLore()) {
                    tooltip.add(descriptionLine.withStyle(TextFormatting.ITALIC, TextFormatting.GRAY));
                }
                tooltip.add(new StringTextComponent(""));
                for (TextComponent descriptionLine : value.getDescription()) {
                    tooltip.add(descriptionLine.withStyle(TextFormatting.GRAY));
                }
            }
        });

        ModAttributes.MAX_REPAIRS.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(value -> {
            int current = ModAttributes.CURRENT_REPAIRS.getOrDefault(stack, 0).getValue(stack);
            int unfilled = value - current;
            tooltip.add(new StringTextComponent("Repairs: ")
                    .append(tooltipDots(current, TextFormatting.YELLOW))
                    .append(tooltipDots(unfilled, TextFormatting.GRAY)));
        });
        ModAttributes.GEAR_MAX_LEVEL.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(value -> {
            int current = ModAttributes.GEAR_LEVEL.getOrDefault(stack, 0.0F).getValue(stack).intValue();
            int unfilled = value - current;
            tooltip.add(new StringTextComponent("Level: ")
                    .append(tooltipDots(current, TextFormatting.YELLOW))
                    .append(tooltipDots(unfilled, TextFormatting.GRAY)));
        });
        ModAttributes.ADD_ARMOR.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(value -> {
            tooltip.add(new StringTextComponent("+" + format(value, 5) + " Armor").withStyle(TextFormatting.DARK_GRAY));
        });
        ModAttributes.ADD_ARMOR_TOUGHNESS.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(value -> {
            tooltip.add(new StringTextComponent("+" + format(value, 5) + " Armor Toughness").withStyle(TextFormatting.DARK_GRAY));
        });
        ModAttributes.ADD_KNOCKBACK_RESISTANCE.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(value -> {
            tooltip.add(new StringTextComponent("+" + format(value, 5) + " Knockback Resistance").withStyle(TextFormatting.DARK_GRAY));
        });
        ModAttributes.ADD_ATTACK_DAMAGE.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(value -> {
            tooltip.add(new StringTextComponent("+" + format(value, 5) + " Attack Damage").withStyle(TextFormatting.DARK_GRAY));
        });
        ModAttributes.ADD_ATTACK_SPEED.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(value -> {
            tooltip.add(new StringTextComponent("+" + format(value, 5) + " Attack Speed").withStyle(TextFormatting.DARK_GRAY));
        });
        ModAttributes.ADD_DURABILITY.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(value -> {
            tooltip.add(new StringTextComponent("+" + value + " Durability").withStyle(TextFormatting.DARK_GRAY));
        });
        ModAttributes.EXTRA_LEECH_RATIO.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(value -> {
            tooltip.add(new StringTextComponent("+" + format(value * 100.0F, 5) + "% Leech").withStyle(TextFormatting.RED));
        });
        ModAttributes.EXTRA_PARRY_CHANCE.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(value -> {
            tooltip.add(new StringTextComponent("+" + format(value * 100.0F, 5) + "% Parry").withStyle(TextFormatting.RED));
        });
        ModAttributes.EXTRA_HEALTH.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(value -> {
            tooltip.add(new StringTextComponent("+" + format(value, 5) + " Health").withStyle(TextFormatting.RED));
        });
        ModAttributes.EXTRA_EFFECTS.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(value -> {
            value.forEach(effect -> {
                tooltip.add(new StringTextComponent("+" + effect.getAmplifier() + " ")
                        .append(new TranslationTextComponent(effect.getEffect().getDescriptionId())).withStyle(TextFormatting.GREEN));
            });
        });

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

        if(enchantments.size() > 0) {
            tooltip.add(new StringTextComponent(""));
        }

        ModAttributes.MIN_VAULT_LEVEL.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(value -> {
            tooltip.add(new StringTextComponent("Requires level: ").append(new StringTextComponent(value + "").withStyle(TextFormatting.YELLOW)));
        });
    }

    static String format(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    default boolean canElytraFly(T item, ItemStack stack, LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            return PlayerSet.isActive(Set.DRAGON, (PlayerEntity) entity);
        }

        return false;
    }

    default boolean elytraFlightTick(T item, ItemStack stack, LivingEntity entity, int flightTicks) {
        return this.canElytraFly(item, stack, entity);
    }

    default int getColor(T item, ItemStack stack) {
        EnumAttribute<State> stateAttribute = ModAttributes.GEAR_STATE.get(stack).orElse(null);
        if (stateAttribute == null || stateAttribute.getValue(stack) == State.UNIDENTIFIED) {
            return 0xFFFFFFFF;
        }

        Rarity rarity = ModAttributes.GEAR_RARITY.getOrDefault(stack, Rarity.SCRAPPY).getValue(stack);
        Integer dyeColor = getDyeColor(stack);

        if (rarity == Rarity.SCRAPPY && dyeColor == null) {
            return 0xFFFFFFFF;
        }

        IntegerAttribute colorAttribute = ModAttributes.GEAR_COLOR.get(stack).orElse(null);
        int baseColor = colorAttribute == null ? 0xFFFFFFFF : colorAttribute.getValue(stack);

        return dyeColor != null ? dyeColor : baseColor;
    }

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings({"unchecked"})
    default <A extends BipedModel<?>> A getArmorModel(T item, LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
        Integer modelId = ModAttributes.GEAR_MODEL.getOrDefault(itemStack, -1).getValue(itemStack);
        Rarity rarity = ModAttributes.GEAR_RARITY.getOrDefault(itemStack, Rarity.SCRAPPY).getValue(itemStack);

        if (rarity == Rarity.SCRAPPY) {
            return null;
        }

        ModModels.GearModel gearModel = ModModels.GearModel.REGISTRY.get(modelId);

        if (gearModel == null) {
            return null;
        }

        return (A) gearModel.forSlotType(armorSlot);
    }

    @OnlyIn(Dist.CLIENT)
    default String getArmorTexture(T item, ItemStack itemStack, Entity entity, EquipmentSlotType slot, String type) {
        Integer modelId = ModAttributes.GEAR_MODEL.getOrDefault(itemStack, -1).getValue(itemStack);
        Rarity rarity = ModAttributes.GEAR_RARITY.getOrDefault(itemStack, Rarity.SCRAPPY).getValue(itemStack);

        if (rarity == Rarity.SCRAPPY) {
            return ModModels.GearModel.SCRAPPY.getTextureName(slot, type);
        }

        ModModels.GearModel gearModel = ModModels.GearModel.REGISTRY.get(modelId);

        if (gearModel == null) {
            return null;
        }

        return gearModel.getTextureName(slot, type);
    }

    default Multimap<Attribute, AttributeModifier> getAttributeModifiers(T item, EquipmentSlotType slot, ItemStack stack, Multimap<Attribute, AttributeModifier> parent) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

        Optional<DoubleAttribute> attackDamage = ModAttributes.ATTACK_DAMAGE.get(stack);
        Optional<DoubleAttribute> attackSpeed = ModAttributes.ATTACK_SPEED.get(stack);
        Optional<DoubleAttribute> armor = ModAttributes.ARMOR.get(stack);
        Optional<DoubleAttribute> armorToughness = ModAttributes.ARMOR_TOUGHNESS.get(stack);
        Optional<DoubleAttribute> knockbackResistance = ModAttributes.KNOCKBACK_RESISTANCE.get(stack);
        Optional<FloatAttribute> extraHealth = ModAttributes.EXTRA_HEALTH.get(stack);

        parent.forEach((attribute, modifier) -> {
            if(attribute == Attributes.ATTACK_DAMAGE && attackDamage.isPresent()) {
                builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF"),
                        "Weapon modifier", attackDamage.get().getValue(stack), AttributeModifier.Operation.ADDITION));
            } else if(attribute == Attributes.ATTACK_SPEED && attackSpeed.isPresent()) {
                builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"),
                        "Weapon modifier", attackSpeed.get().getValue(stack), AttributeModifier.Operation.ADDITION));
            } else if(attribute == Attributes.ARMOR && armor.isPresent()) {
                builder.put(Attributes.ARMOR, new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()],
                        "Armor modifier", armor.get().getValue(stack), AttributeModifier.Operation.ADDITION));
            } else if(attribute == Attributes.ARMOR_TOUGHNESS && armorToughness.isPresent()) {
                builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()],
                        "Armor toughness", armorToughness.get().getValue(stack), AttributeModifier.Operation.ADDITION));
            } else if(attribute == Attributes.KNOCKBACK_RESISTANCE && knockbackResistance.isPresent()) {
                builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()],
                        "Armor knockback resistance", knockbackResistance.get().getValue(stack), AttributeModifier.Operation.ADDITION));
            } else {
                builder.put(attribute, modifier);
            }
        });

        if(((item == ModItems.SWORD || item == ModItems.AXE) && slot == EquipmentSlotType.MAINHAND)
            || (item == ModItems.DAGGER && (slot == EquipmentSlotType.MAINHAND || slot == EquipmentSlotType.OFFHAND))
            || (item instanceof VaultArmorItem && item.getEquipmentSlot(stack) == slot)) {
            extraHealth.ifPresent(floatAttribute -> builder.put(Attributes.MAX_HEALTH, new AttributeModifier(new UUID(1234L, item.getRegistryName().toString().hashCode()),
                    "Extra Health", floatAttribute.getValue(stack), AttributeModifier.Operation.ADDITION)));
        }

        return builder.build();
    }

    static void addLevel(ItemStack stack, float amount) {
        if(!(stack.getItem() instanceof VaultGear<?>))return;
        int maxLevel = ModAttributes.GEAR_MAX_LEVEL.getOrDefault(stack, 0).getValue(stack);
        float current = ModAttributes.GEAR_LEVEL.getOrDefault(stack, 0.0F).getValue(stack);
        if((int)current >= maxLevel)return;

        float newLevel = current + amount;
        int difference = (int)newLevel - (int)current;
        ModAttributes.GEAR_LEVEL.create(stack, newLevel);

        int toRoll = ModAttributes.GEAR_MODIFIERS_TO_ROLL.getOrDefault(stack, 0).getValue(stack) + difference;
        if(toRoll != 0)ModAttributes.GEAR_MODIFIERS_TO_ROLL.create(stack, toRoll);
    }

    static void initialize(ItemStack stack, Random random) {
        ModAttributes.GEAR_RARITY.get(stack).ifPresent(attribute -> {
            VaultGearConfig.get(attribute.getValue(stack)).initializeAttributes(stack, random);
        });
    }

    static void update(ItemStack stack, Random random) {
        ModAttributes.GEAR_RARITY.get(stack).ifPresent(attribute -> {
            VaultGearConfig.get(attribute.getValue(stack)).initializeModifiers(stack, random);
        });
    }

    static int randomBaseColor(Random rand) {
        return BASE_COLORS[rand.nextInt(BASE_COLORS.length)].getColorValue();
    }

    static Integer getDyeColor(ItemStack stack) {
        CompoundNBT compoundnbt = stack.getTagElement("display");
        if (compoundnbt != null && compoundnbt.contains("color", Constants.NBT.TAG_INT)) {
            return compoundnbt.getInt("color");
        }
        return null;
    }

    static ITextComponent tooltipDots(int amount, TextFormatting formatting) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            text.append("\u2b22 ");
        }
        return new StringTextComponent(text.toString())
                .withStyle(formatting);
    }

    DyeColor[] BASE_COLORS = {
            DyeColor.BLUE,
            DyeColor.BROWN,
            DyeColor.CYAN,
            DyeColor.GREEN,
            DyeColor.LIGHT_BLUE,
            DyeColor.LIME,
            DyeColor.MAGENTA,
            DyeColor.ORANGE,
            DyeColor.PINK,
            DyeColor.PURPLE,
            DyeColor.RED,
            DyeColor.WHITE,
            DyeColor.YELLOW,
    };

    enum Type {
        SWORD, AXE, ARMOR
    }

    enum State {
        UNIDENTIFIED, ROLLING, IDENTIFIED
    }

    class Material implements IArmorMaterial {
        public static final Material INSTANCE = new Material();

        private Material() {

        }

        @Override
        public int getDurabilityForSlot(EquipmentSlotType slot) {
            return 0;
        }

        @Override
        public int getDefenseForSlot(EquipmentSlotType slot) {
            return 0;
        }

        @Override
        public int getEnchantmentValue() {
            return ArmorMaterial.DIAMOND.getEnchantmentValue();
        }

        @Override
        public SoundEvent getEquipSound() {
            return ArmorMaterial.DIAMOND.getEquipSound();
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }

        @Override
        public String getName() {
            return "vault_dummy";
        }

        @Override
        public float getToughness() {
            return 0;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0001F;
        }
    }

    class Tier implements IItemTier {
        public static final Tier INSTANCE = new Tier();

        private Tier() {

        }

        @Override
        public int getUses() {
            return 0;
        }

        @Override
        public float getSpeed() {
            return 0.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0.0F;
        }

        @Override
        public int getLevel() {
            return ItemTier.DIAMOND.getLevel();
        }

        @Override
        public int getEnchantmentValue() {
            return ItemTier.DIAMOND.getEnchantmentValue();
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    }

    enum Set {
        NONE("", ""),
        PHOENIX("Reborn from the ashes!",
                "Next time you take a lethal damage in the Vaults, become invulnerable for 3 seconds and get fully healed. (Can be triggered only once per Vault instance)"
        ),
        GOBLIN("Hoard all the way!",
                "Grants better loot chance (+1 Luck)"
        ),
        GOLEM("Steady as rock!",
                "Grants +8% resistance"
        ),
        ASSASSIN("Fast as wind!",
                "Increases speed and grants +10% dodge chance"
        ),
        SLAYER("Slay them all!",
                "Grants +2 Strength"
        ),
        RIFT("Become one with the Vault Rifts!",
                "Reduce all ability cooldowns by 50%"
        ),
        DRAGON("Breath of the ender!",
                "Gain elytra and gliding powers without an elytra item"
        ),
        BRUTE("Angry as the Piglins!",
                "Grants +1 Strength"
        ),
        TITAN("Sturdy as a titan!",
                "Grants +14% resistance"
        ),
        DRYAD("Touch of the nature!",
                "Grants +2 Regeneration"
        ),
        VAMPIRE("Smell the blood!",
                "Grants 5% life leech"
        ),
        NINJA("Can't hit me!",
                "Grants +20% parry chance"
        ),
        TREASURE_HUNTER("Leave no chest behind!",
                "Grants better loot chance (+3 Luck)"
        );

        String lore;
        String description;

        Set(String lore, String description) {
            this.lore = lore;
            this.description = description;
        }

        public List<TextComponent> getDescription() {
            return getTooltip(description);
        }

        public List<TextComponent> getLore() {
            return getTooltip(lore);
        }

        private List<TextComponent> getTooltip(String text) {
            LinkedList<TextComponent> tooltip = new LinkedList<>();
            StringBuilder sb = new StringBuilder();
            for (String word : text.split("\\s+")) {
                sb.append(word + " ");
                if (sb.length() >= 30) {
                    tooltip.add(new StringTextComponent(sb.toString().trim()));
                    sb = new StringBuilder();
                }
            }
            if (sb.length() > 0) {
                tooltip.add(new StringTextComponent(sb.toString().trim()));
            }
            return tooltip;
        }
    }

    enum Rarity {
        COMMON(TextFormatting.AQUA),
        RARE(TextFormatting.YELLOW),
        EPIC(TextFormatting.LIGHT_PURPLE),
        OMEGA(TextFormatting.GREEN),
        SCRAPPY(TextFormatting.GRAY);

        public final TextFormatting color;

        Rarity(TextFormatting color) {
            this.color = color;
        }
    }

    enum RollType {
        SCRAPPY_ONLY, TREASURE_ONLY, ALL;

        public Rarity get(Random rand) {
            return ModConfigs.VAULT_GEAR.ROLLS.get(this.name()).getRandom(rand);
        }
    }

}
