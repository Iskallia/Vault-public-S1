package iskallia.vault.skill.talent;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.type.AttributeTalent;
import iskallia.vault.skill.talent.type.EffectTalent;
import iskallia.vault.skill.talent.type.PlayerTalent;
import iskallia.vault.util.RomanNumber;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.potion.Effect;

import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

public class TalentGroup<T extends PlayerTalent> {

    @Expose private final String name;
    @Expose private final T[] levels;

    private BiMap<String, T> registry;

    public TalentGroup(String name, T... levels) {
        this.name = name;
        this.levels = levels;
    }

    public int getMaxLevel() {
        return this.levels.length;
    }

    public String getParentName() {
        return this.name;
    }

    public String getName(int level) {
        if (level == 0) return name + " " + RomanNumber.toRoman(0);
        return this.getRegistry().inverse().get(this.getTalent(level));
    }

    public T getTalent(int level) {
        if (level < 0) return this.levels[0];
        if (level >= getMaxLevel()) return this.levels[getMaxLevel() - 1];
        return this.levels[level - 1];
    }

    public int learningCost() {
        return this.levels[0].getCost();
    }

    public int cost(int level) {
        if (level > getMaxLevel()) return -1;
        return this.levels[level - 1].getCost();
    }

    public BiMap<String, T> getRegistry() {
        if (this.registry == null) {
            this.registry = HashBiMap.create(this.getMaxLevel());

            if (this.getMaxLevel() == 1) {
                this.registry.put(this.getParentName(), this.levels[0]);

            } else if (this.getMaxLevel() > 1) {
                for (int i = 0; i < this.getMaxLevel(); i++) {
                    this.registry.put(this.getParentName() + " " + RomanNumber.toRoman(i + 1),
                            this.getTalent(i + 1));
                }
            }
        }

        return this.registry;
    }

    /* --------------------------------------- */

    public static TalentGroup<EffectTalent> ofEffect(String name, Effect effect, EffectTalent.Type type, int maxLevel,
                                                     IntUnaryOperator cost, EffectTalent.Operator operator) {
        EffectTalent[] talents = IntStream.range(0, maxLevel)
                .mapToObj(i -> new EffectTalent(cost.applyAsInt(i + 1), effect, i, type, operator))
                .toArray(EffectTalent[]::new);
        return new TalentGroup<>(name, talents);
    }

    public static TalentGroup<AttributeTalent> ofAttribute(String name, Attribute attribute, String modifierName,
                                                           int maxLevel, IntUnaryOperator cost, IntToDoubleFunction amount,
                                                           IntFunction<AttributeModifier.Operation> operation) {
        AttributeTalent[] talents = IntStream.range(0, maxLevel)
                .mapToObj(i -> new AttributeTalent(cost.applyAsInt(i + 1), attribute,
                        new AttributeTalent.Modifier(
                                modifierName + " " + RomanNumber.toRoman(i + 1),
                                amount.applyAsDouble(i + 1),
                                operation.apply(i + 1)
                        )))
                .toArray(AttributeTalent[]::new);
        return new TalentGroup<>(name, talents);
    }

    public static <T extends PlayerTalent> TalentGroup<T> of(String name, int maxLevel, IntFunction<T> supplier) {
        PlayerTalent[] talents = IntStream.range(0, maxLevel).mapToObj(supplier).toArray(PlayerTalent[]::new);
        return new TalentGroup<>(name, (T[])talents);
    }

}
