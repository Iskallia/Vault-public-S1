package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.client.gui.helper.SkillFrame;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;

import java.util.HashMap;

public class AbilitiesGUIConfig extends Config {

    @Expose private HashMap<String, SkillStyle> styles;

    @Override
    public String getName() {
        return "abilities_gui_styles";
    }

    public HashMap<String, SkillStyle> getStyles() {
        return styles;
    }

    @Override
    protected void reset() {
        SkillStyle style;
        this.styles = new HashMap<>();

        style = new SkillStyle(0, 0,
                16 * 4, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.ABILITIES.NIGHT_VISION.getParentName(), style);

        style = new SkillStyle(50, 0,
                16 * 6, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.ABILITIES.INVISIBILITY.getParentName(), style);

        style = new SkillStyle(50 * 2, 0,
                16, 0);
        style.frameType = SkillFrame.STAR;
        styles.put(ModConfigs.ABILITIES.VEIN_MINER.getParentName(), style);

        style = new SkillStyle(50 * 3, 0,
                16 * 5, 0);
        style.frameType = SkillFrame.STAR;
        styles.put(ModConfigs.ABILITIES.SELF_SUSTAIN.getParentName(), style);

        style = new SkillStyle(0, 50,
                0, 0);
        style.frameType = SkillFrame.STAR;
        styles.put(ModConfigs.ABILITIES.DASH.getParentName(), style);

        style = new SkillStyle(50, 50,
                16 * 3, 0);
        style.frameType = SkillFrame.STAR;
        styles.put(ModConfigs.ABILITIES.MEGA_JUMP.getParentName(), style);

        style = new SkillStyle(50 * 2, 50,
                16 * 7, 0);
        style.frameType = SkillFrame.STAR;
        styles.put(ModConfigs.ABILITIES.GHOST_WALK.getParentName(), style);

        style = new SkillStyle(50 * 3, 50,
                16 * 8, 0);
        style.frameType = SkillFrame.STAR;
        styles.put(ModConfigs.ABILITIES.RAMPAGE.getParentName(), style);

        style = new SkillStyle(0, 50 * 2,
                16 * 9, 0);
        style.frameType = SkillFrame.STAR;
        styles.put(ModConfigs.ABILITIES.CLEANSE.getParentName(), style);


        style = new SkillStyle(50, 50 * 2,
                16 * 10, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.ABILITIES.TANK.getParentName(), style);

        style = new SkillStyle(50 * 2, 50 * 2,
                16 * 11, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.ABILITIES.EXECUTE.getParentName(), style);

        style = new SkillStyle(50 * 3, 50 * 2,
                16 * 12, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put(ModConfigs.ABILITIES.SUMMON_ETERNAL.getParentName(), style);
    }

}
