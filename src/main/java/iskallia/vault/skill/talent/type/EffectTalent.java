package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.skill.set.EffectSet;
import iskallia.vault.skill.set.SetNode;
import iskallia.vault.skill.set.SetTree;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerSetsData;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EffectTalent extends PlayerTalent {

    @Expose private final String effect;
    @Expose private final int amplifier;
    @Expose private final String type;
    @Expose private final String operator;

    public EffectTalent(int cost, Effect effect, int amplifier, Type type, Operator operator) {
        this(cost, Registry.MOB_EFFECT.getKey(effect).toString(), amplifier, type.toString(), operator.toString());
    }

    public EffectTalent(int cost, String effect, int amplifier, String type, String operator) {
        super(cost);
        this.effect = effect;
        this.amplifier = amplifier;
        this.type = type;
        this.operator = operator;
    }

    public Effect getEffect() {
        return Registry.MOB_EFFECT.get(new ResourceLocation(this.effect));
    }

    public int getAmplifier() {
        return this.amplifier;
    }

    public Type getType() {
        return Type.fromString(this.type);
    }

    public Operator getOperator() {
        return Operator.fromString(this.operator);
    }

    @Override
    public void tick(PlayerEntity player) {
        Tuple<Integer, EffectTalent> data = getData(player, (ServerWorld) player.level, this.getEffect());
        EffectInstance activeEffect = player.getEffect(this.getEffect());

        if (data.getA() >= 0) {
            EffectInstance newEffect = new EffectInstance(this.getEffect(), 100, data.getA(),
                    false, data.getB().getType().showParticles, data.getB().getType().showIcon);

            if (activeEffect == null || activeEffect.getAmplifier() < newEffect.getAmplifier()) {
                player.addEffect(newEffect);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level.isClientSide) return;
        ForgeRegistries.POTIONS.forEach(effect -> {
            Tuple<Integer, EffectTalent> data = getData(event.player, (ServerWorld) event.player.level, effect);

            EffectInstance activeEffect = event.player.getEffect(effect);

            if (data.getA() >= 0) {
                EffectInstance newEffect = new EffectInstance(effect, 339, data.getA(),
                        false, data.getB().getType().showParticles, data.getB().getType().showIcon);

                if (activeEffect == null || activeEffect.getAmplifier() < newEffect.getAmplifier()) {
                    event.player.addEffect(newEffect);
                } else {
                    if (activeEffect.getDuration() <= 259) {
                        event.player.addEffect(newEffect);
                    }
                }
            }
        });
    }

    public static Tuple<Integer, EffectTalent> getData(PlayerEntity player, ServerWorld world, Effect effect) {
        TalentTree abilities = PlayerTalentsData.get(world).getTalents(player);
        SetTree sets = PlayerSetsData.get(world).getSets(player);

        List<EffectTalent> overrides = new ArrayList<>();
        List<EffectTalent> addends = new ArrayList<>();

        for (TalentNode<?> node : abilities.getNodes()) {
            if (!(node.getTalent() instanceof EffectTalent)) continue;
            EffectTalent talent = (EffectTalent) node.getTalent();
            if (talent.getEffect() != effect) continue;

            if (talent.getOperator() == Operator.SET) {
                overrides.add(talent);
            } else if (talent.getOperator() == Operator.ADD) {
                addends.add(talent);
            }
        }

        for (SetNode<?> node : sets.getNodes()) {
            if (!(node.getSet() instanceof EffectSet)) continue;
            EffectSet set = (EffectSet) node.getSet();
            if (set.getChild().getEffect() != effect) continue;

            if (set.getChild().getOperator() == Operator.SET) {
                overrides.add(set.getChild());
            } else if (set.getChild().getOperator() == Operator.ADD) {
                addends.add(set.getChild());
            }
        }

        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            List<EffectTalent> effects = ModAttributes.EXTRA_EFFECTS.getOrDefault(stack, new ArrayList<>()).getValue(stack);

            for (EffectTalent effect1 : effects) {
                if (effect1.getEffect() != effect) continue;

                if (effect1.getOperator() == Operator.SET) {
                    overrides.add(effect1);
                } else if (effect1.getOperator() == Operator.ADD) {
                    addends.add(effect1);
                }
            }
        }

        if (overrides.isEmpty() && addends.isEmpty()) {
            return new Tuple<>(-1, null);
        }

        int newAmplifier = overrides.isEmpty() ? -1 : overrides.stream().mapToInt(EffectTalent::getAmplifier).max().getAsInt();
        newAmplifier += addends.stream().mapToInt(EffectTalent::getAmplifier).sum();

        EffectTalent priority = overrides.isEmpty()
                ? addends.stream().max(Comparator.comparingInt(EffectTalent::getAmplifier)).get()
                : overrides.stream().max(Comparator.comparingInt(EffectTalent::getAmplifier)).get();

        return new Tuple<>(newAmplifier, priority);
    }

    @Override
    public void onRemoved(PlayerEntity player) {
        player.removeEffect(this.getEffect());
    }

    public enum Type {
        HIDDEN("hidden", false, false),
        PARTICLES_ONLY("particles_only", true, false),
        ICON_ONLY("icon_only", false, true),
        ALL("all", true, true);

        private static Map<String, Type> STRING_TO_TYPE = Arrays.stream(values())
                .collect(Collectors.toMap(Type::toString, o -> o));

        public final String name;
        public final boolean showParticles;
        public final boolean showIcon;

        Type(String name, boolean showParticles, boolean showIcon) {
            this.name = name;
            this.showParticles = showParticles;
            this.showIcon = showIcon;
        }

        public static Type fromString(String type) {
            return STRING_TO_TYPE.get(type);
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum Operator {
        SET("set"), ADD("add");

        private static Map<String, Operator> STRING_TO_TYPE = Arrays.stream(values())
                .collect(Collectors.toMap(Operator::toString, o -> o));

        public final String name;

        Operator(String name) {
            this.name = name;
        }

        public static Operator fromString(String type) {
            return STRING_TO_TYPE.get(type);
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

}
