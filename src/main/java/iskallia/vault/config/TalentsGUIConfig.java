package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.client.gui.helper.SkillFrame;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;

import java.util.HashMap;

public class TalentsGUIConfig extends Config {

    @Expose private HashMap<String, SkillStyle> styles;

    @Override
    public String getName() {
        return "talents_gui_styles";
    }

    public HashMap<String, SkillStyle> getStyles() {
        return styles;
    }

    @Override
    protected void reset() {
        SkillStyle style;
        this.styles = new HashMap<>();

        style = new SkillStyle(0, 0, 16 * 6, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.TALENTS.HASTE.getParentName(), style);

        style = new SkillStyle(70, 0, 16 * 3, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.TALENTS.REGENERATION.getParentName(), style);

        style = new SkillStyle(70 * 2, 0, 16 * 1, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.TALENTS.VAMPIRISM.getParentName(), style);

        style = new SkillStyle(70 * 3, 0, 16 * 7, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.TALENTS.RESISTANCE.getParentName(), style);

        style = new SkillStyle(70 * 4, 0, 16 * 8, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.TALENTS.STRENGTH.getParentName(), style);

        style = new SkillStyle(70 * 5, 0, 16 * 4, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.TALENTS.FIRE_RESISTANCE.getParentName(), style);

        style = new SkillStyle(0, 70, 16 * 9, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.TALENTS.SPEED.getParentName(), style);

        style = new SkillStyle(70, 70, 0, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.TALENTS.WATER_BREATHING.getParentName(), style);

        style = new SkillStyle(70 * 2, 70, 16 * 2, 0);
        style.frameType = SkillFrame.STAR;
        styles.put(ModConfigs.TALENTS.WELL_FIT.getParentName(), style);

        style = new SkillStyle(70 * 3, 70, 16 * 13, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.TALENTS.TWERKER.getParentName(), style);

        style = new SkillStyle(70 * 4, 70, 16 * 11, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.TALENTS.ELVISH.getParentName(), style);

        style = new SkillStyle(70 * 5, 70, 16 * 14, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.TALENTS.ANGEL.getParentName(), style);

        style = new SkillStyle(0, 70 * 2, 16 * 10, 0);
        style.frameType = SkillFrame.STAR;
        styles.put(ModConfigs.TALENTS.REACH.getParentName(), style);

        // ---- 1.2

        style = new SkillStyle(70, 70 * 2, 0, 16);
        style.frameType = SkillFrame.STAR;
        styles.put(ModConfigs.TALENTS.EXPERIENCED.getParentName(), style);

        style = new SkillStyle(70 * 2, 70 * 2, 16 * 15, 0);
        style.frameType = SkillFrame.STAR;
        styles.put(ModConfigs.TALENTS.PARRY.getParentName(), style);

        style = new SkillStyle(70 * 3, 70 * 2, 16, 16);
        style.frameType = SkillFrame.STAR;
        styles.put(ModConfigs.TALENTS.STONE_SKIN.getParentName(), style);

        style = new SkillStyle(70 * 4, 70 * 2, 16 * 2, 16);
        style.frameType = SkillFrame.STAR;
        styles.put(ModConfigs.TALENTS.UNBREAKABLE.getParentName(), style);

        style = new SkillStyle(70 * 5, 70 * 2, 16 * 3, 16);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.TALENTS.CRITICAL_STRIKE.getParentName(), style);

        style = new SkillStyle(0, 70 * 3, 16 * 4, 16);
        style.frameType = SkillFrame.STAR;
        styles.put(ModConfigs.TALENTS.LOOTER.getParentName(), style);

        style = new SkillStyle(70, 70 * 3, 16 * 5, 16);
        style.frameType = SkillFrame.STAR;
        styles.put(ModConfigs.TALENTS.CARAPACE.getParentName(), style);

        style = new SkillStyle(70 * 2, 70 * 3, 16 * 6, 16);
        style.frameType = SkillFrame.STAR;
        styles.put(ModConfigs.TALENTS.CHUNKY.getParentName(), style);

        style = new SkillStyle(70 * 3, 70 * 3, 16 * 7, 16);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.TALENTS.FRENZY.getParentName(), style);

        style = new SkillStyle(70 * 4, 70 * 3, 16 * 8, 16);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.TALENTS.STEP.getParentName(), style);

        style = new SkillStyle(70 * 5, 70 * 3, 16 * 9, 16);
        style.frameType = SkillFrame.STAR;
        styles.put(ModConfigs.TALENTS.NINJA.getParentName(), style);
    }

}
