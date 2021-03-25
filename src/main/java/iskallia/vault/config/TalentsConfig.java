package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.skill.talent.type.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.ForgeMod;

import java.util.Arrays;
import java.util.List;

public class TalentsConfig extends Config {

    @Expose public TalentGroup<EffectTalent> HASTE;
    @Expose public TalentGroup<EffectTalent> REGENERATION;
    @Expose public TalentGroup<VampirismTalent> VAMPIRISM;
    @Expose public TalentGroup<EffectTalent> RESISTANCE;
    @Expose public TalentGroup<EffectTalent> STRENGTH;
    @Expose public TalentGroup<EffectTalent> FIRE_RESISTANCE;
    @Expose public TalentGroup<EffectTalent> SPEED;
    @Expose public TalentGroup<EffectTalent> WATER_BREATHING;
    @Expose public TalentGroup<AttributeTalent> WELL_FIT;
    @Expose public TalentGroup<AttributeTalent> REACH;
    @Expose public TalentGroup<TwerkerTalent> TWERKER;
    @Expose public TalentGroup<ElvishTalent> ELVISH;
    @Expose public TalentGroup<AngelTalent> ANGEL;
    @Expose public TalentGroup<ExperiencedTalent> EXPERIENCED;
    @Expose public TalentGroup<ParryTalent> PARRY;
    @Expose public TalentGroup<AttributeTalent> STONE_SKIN;
    @Expose public TalentGroup<UnbreakableTalent> UNBREAKABLE;
    @Expose public TalentGroup<CriticalStrikeTalent> CRITICAL_STRIKE;
    @Expose public TalentGroup<EffectTalent> LOOTER;
    @Expose public TalentGroup<CarapaceTalent> CARAPACE;
    @Expose public TalentGroup<AttributeTalent> CHUNKY;
    @Expose public TalentGroup<FrenzyTalent> FRENZY;
    @Expose public TalentGroup<StepTalent> STEP;
    @Expose public TalentGroup<ParryTalent> NINJA;

    @Override
    public String getName() {
        return "talents";
    }

    public List<TalentGroup<?>> getAll() {
        return Arrays.asList(HASTE, REGENERATION, VAMPIRISM, RESISTANCE, STRENGTH, FIRE_RESISTANCE, SPEED,
                WATER_BREATHING, WELL_FIT, TWERKER, ELVISH, ANGEL, REACH, EXPERIENCED, PARRY, STONE_SKIN, UNBREAKABLE,
                CRITICAL_STRIKE, LOOTER, CARAPACE, CHUNKY, FRENZY, STEP, NINJA);
    }

    public TalentGroup<?> getByName(String name) {
        return this.getAll().stream().filter(group -> group.getParentName().equals(name)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown talent with name " + name));
    }

    @Override
    protected void reset() {
        this.HASTE = TalentGroup.ofEffect("Haste", Effects.DIG_SPEED, EffectTalent.Type.ICON_ONLY, 6, i -> { if(i < 3)return 2; else if(i == 3)return 3; else return 4; }, EffectTalent.Operator.SET);
        this.REGENERATION = TalentGroup.ofEffect("Regeneration", Effects.REGENERATION, EffectTalent.Type.ICON_ONLY, 3, i -> i == 0 ? 10 : 5, EffectTalent.Operator.SET);
        this.VAMPIRISM = new TalentGroup<>("Vampirism", new VampirismTalent(2, 0.1F), new VampirismTalent(2, 0.2F), new VampirismTalent(2, 0.3F), new VampirismTalent(2, 0.4F), new VampirismTalent(2, 0.5F), new VampirismTalent(2, 0.6F));
        this.RESISTANCE = TalentGroup.ofEffect("Resistance", Effects.DAMAGE_RESISTANCE, EffectTalent.Type.ICON_ONLY, 2, i -> 3, EffectTalent.Operator.SET);
        this.STRENGTH = TalentGroup.ofEffect("Strength", Effects.DAMAGE_BOOST, EffectTalent.Type.ICON_ONLY, 2, i -> 3, EffectTalent.Operator.SET);
        this.FIRE_RESISTANCE = TalentGroup.ofEffect("Fire Resistance", Effects.FIRE_RESISTANCE, EffectTalent.Type.ICON_ONLY, 1, i -> 5, EffectTalent.Operator.SET);
        this.SPEED = TalentGroup.ofEffect("Speed", Effects.MOVEMENT_SPEED, EffectTalent.Type.ICON_ONLY, 5, i -> 2, EffectTalent.Operator.SET);
        this.WATER_BREATHING = TalentGroup.ofEffect("Water Breathing", Effects.WATER_BREATHING, EffectTalent.Type.ICON_ONLY, 1, i -> 5, EffectTalent.Operator.SET);
        this.WELL_FIT = TalentGroup.ofAttribute("Well Fit", Attributes.MAX_HEALTH, "Extra Health", 10, i -> 1, i -> i * 2.0D, i -> AttributeModifier.Operation.ADDITION);
        this.REACH = TalentGroup.ofAttribute("Reach", ForgeMod.REACH_DISTANCE.get(), "Maximum Reach", 10, i -> 1, i -> i * 1.0D, i -> AttributeModifier.Operation.ADDITION);
        this.TWERKER = new TalentGroup<>("Twerker", new TwerkerTalent(4));
        this.ELVISH = new TalentGroup<>("Elvish", new ElvishTalent(10));
        this.ANGEL = new TalentGroup<>("Angel", new AngelTalent(200));
        this.EXPERIENCED = new TalentGroup<>("Experienced", new ExperiencedTalent(2, 0.20f), new ExperiencedTalent(2, 0.40f), new ExperiencedTalent(2, 0.60f), new ExperiencedTalent(2, 0.80f), new ExperiencedTalent(2, 1.00f), new ExperiencedTalent(2, 1.20f), new ExperiencedTalent(2, 1.40f), new ExperiencedTalent(2, 1.60f), new ExperiencedTalent(2, 1.80f), new ExperiencedTalent(2, 2.00f));
        this.PARRY = new TalentGroup<>("Parry", new ParryTalent(2, 0.02f), new ParryTalent(2, 0.04f), new ParryTalent(2, 0.06f), new ParryTalent(2, 0.08f), new ParryTalent(2, 0.10f), new ParryTalent(2, 0.12f), new ParryTalent(2, 0.14f), new ParryTalent(2, 0.16f), new ParryTalent(2, 0.18f), new ParryTalent(2, 0.20f));
        this.STONE_SKIN = TalentGroup.ofAttribute("Stone Skin", Attributes.KNOCKBACK_RESISTANCE, "Extra Knockback Resistance", 10, i -> 2, i -> i * 0.1F, i -> AttributeModifier.Operation.ADDITION);
        this.UNBREAKABLE = TalentGroup.of("Unbreakable", 10, i -> new UnbreakableTalent(2, i + 1));
        this.CRITICAL_STRIKE = TalentGroup.of("Critical Strike", 5, i -> new CriticalStrikeTalent(3, (i + 1) * 0.2F));
        this.LOOTER = TalentGroup.ofEffect("Looter", Effects.LUCK, EffectTalent.Type.ICON_ONLY, 10, i -> 3, EffectTalent.Operator.SET);
        this.CARAPACE = TalentGroup.of("Carapace", 10, i -> new CarapaceTalent(1, (i + 1) * 0.01F));
        this.CHUNKY = TalentGroup.ofAttribute("Chunky", Attributes.MAX_HEALTH, "Extra Health 2", 10, i -> i < 5 ? 2 : 3, i -> i * 2.0D, i -> AttributeModifier.Operation.ADDITION);
        this.FRENZY = TalentGroup.of("Frenzy", 3, i -> new FrenzyTalent(i * 2 - 1, (i + 1) * 0.1F, 2.0F));
        this.STEP = TalentGroup.of("Step", 1, i -> new StepTalent(4, 1.0F));
        this.NINJA = TalentGroup.of("Ninja", 10, i -> new ParryTalent(1, i < 5 ? (i + 1) * 0.01F : 0.5F + (i + 1) * 0.02F));
    }

}
