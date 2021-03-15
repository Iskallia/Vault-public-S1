package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.type.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;

import java.util.Arrays;
import java.util.List;

public class AbilitiesConfig extends Config {

    @Expose public AbilityGroup<EffectAbility> NIGHT_VISION;
    @Expose public AbilityGroup<EffectAbility> INVISIBILITY;
    @Expose public AbilityGroup<GhostWalkAbility> GHOST_WALK;
    @Expose public AbilityGroup<RampageAbility> RAMPAGE;
    @Expose public AbilityGroup<VeinMinerAbility> VEIN_MINER;
    @Expose public AbilityGroup<SelfSustainAbility> SELF_SUSTAIN;
    @Expose public AbilityGroup<DashAbility> DASH;
    @Expose public AbilityGroup<MegaJumpAbility> MEGA_JUMP;
    @Expose public AbilityGroup<CleanseAbility> CLEANSE;
    @Expose public AbilityGroup<TankAbility> TANK;
    @Expose public AbilityGroup<ExecuteAbility> EXECUTE;
    @Expose public AbilityGroup<SummonEternalAbility> SUMMON_ETERNAL;

    @Override
    public String getName() {
        return "abilities";
    }

    public List<AbilityGroup<?>> getAll() {
        return Arrays.asList(NIGHT_VISION, INVISIBILITY, VEIN_MINER,
                SELF_SUSTAIN, DASH, MEGA_JUMP, GHOST_WALK, RAMPAGE,
                CLEANSE, TANK, EXECUTE, SUMMON_ETERNAL);
    }

    public AbilityGroup<?> getByName(String name) {
        return this.getAll().stream().filter(group -> group.getParentName().equals(name)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown ability with name " + name));
    }

    public int cooldownOf(AbilityNode<?> abilityNode, PlayerEntity player) {
        AbilityGroup<?> abilityGroup = getByName(abilityNode.getGroup().getParentName());
        return abilityGroup.getAbility(abilityNode.getLevel()).getCooldown(player);
    }

    @Override
    protected void reset() {
        this.NIGHT_VISION = AbilityGroup.ofEffect("Night Vision", Effects.NIGHT_VISION, EffectAbility.Type.ICON_ONLY, 1, i -> 1);

        this.INVISIBILITY = AbilityGroup.ofEffect("Invisibility", Effects.INVISIBILITY, EffectAbility.Type.ICON_ONLY, 1, i -> 1);

        this.GHOST_WALK = AbilityGroup.ofGhostWalkEffect("Ghost Walk", ModEffects.GHOST_WALK, EffectAbility.Type.ICON_ONLY, 6, i -> 1);

        this.RAMPAGE = AbilityGroup.ofRampage("Rampage", ModEffects.RAMPAGE, EffectAbility.Type.ICON_ONLY, 9, i -> 1);

        this.VEIN_MINER = new AbilityGroup<>("Vein Miner",
                new VeinMinerAbility(1, 4),
                new VeinMinerAbility(1, 8),
                new VeinMinerAbility(1, 16),
                new VeinMinerAbility(2, 32),
                new VeinMinerAbility(2, 64)
        );

        this.SELF_SUSTAIN = new AbilityGroup<>("Self Sustain",
                new SelfSustainAbility(1, 1),
                new SelfSustainAbility(1, 2),
                new SelfSustainAbility(1, 4)
        );

        this.DASH = new AbilityGroup<>("Dash",
                new DashAbility(2, 1),
                new DashAbility(1, 2),
                new DashAbility(1, 3),
                new DashAbility(1, 4),
                new DashAbility(1, 5),
                new DashAbility(1, 6),
                new DashAbility(1, 7),
                new DashAbility(1, 8),
                new DashAbility(1, 9),
                new DashAbility(1, 10)
        );

        this.MEGA_JUMP = new AbilityGroup<>("Mega Jump",
                new MegaJumpAbility(1, 0),
                new MegaJumpAbility(1, 2),
                new MegaJumpAbility(1, 3)
        );

        this.CLEANSE = new AbilityGroup<>("Cleanse",
                new CleanseAbility(1, 30 * 20),
                new CleanseAbility(1, 27 * 20),
                new CleanseAbility(1, 25 * 20),
                new CleanseAbility(1, 23 * 20),
                new CleanseAbility(1, 20 * 20),
                new CleanseAbility(1, 18 * 20),
                new CleanseAbility(1, 16 * 20),
                new CleanseAbility(1, 14 * 20),
                new CleanseAbility(1, 12 * 20),
                new CleanseAbility(1, 10 * 20)
        );

        this.TANK = AbilityGroup.ofTank("Tank", ModEffects.TANK, EffectAbility.Type.ICON_ONLY, 5, i -> 3);

        this.EXECUTE = AbilityGroup.ofExecute("Execute", ModEffects.EXECUTE, EffectAbility.Type.ICON_ONLY, 5, i -> 1);

        this.SUMMON_ETERNAL = new AbilityGroup<>("Summon Eternal",
                new SummonEternalAbility(1, 10 * 60 * 20, 10 * 60 * 20, true, 1),
                new SummonEternalAbility(1, 9 * 60 * 20, 9 * 60 * 20, true, 1),
                new SummonEternalAbility(1, 8 * 60 * 20, 8 * 60 * 20, false,  2),
                new SummonEternalAbility(1, 7 * 60 * 20, 7 * 60 * 20, false, 2),
                new SummonEternalAbility(1, 6 * 60 * 20, 6 * 60 * 20, false, 3));
    }

}
