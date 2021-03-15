package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.client.gui.helper.SkillFrame;
import iskallia.vault.config.entry.SkillStyle;

import java.util.HashMap;

public class ResearchesGUIConfig extends Config {

    @Expose private HashMap<String, SkillStyle> styles;

    @Override
    public String getName() {
        return "researches_gui_styles";
    }

    public HashMap<String, SkillStyle> getStyles() {
        return styles;
    }

    @Override
    protected void reset() {
        SkillStyle style;
        this.styles = new HashMap<>();

        style = new SkillStyle(0, 0,
                0, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Backpacks!", style);

        style = new SkillStyle(50, 0,
                16 * 2, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Waystones", style);

        style = new SkillStyle(50 * 2, 0,
                16 * 5, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Safety First", style);

        style = new SkillStyle(50 * 3, 0,
                16 * 6, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Organisation", style);

        style = new SkillStyle(50 * 4, 0,
                16 * 7, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Super Builder", style);

        style = new SkillStyle(0, 50,
                16 * 9, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Super Miner", style);

        style = new SkillStyle(50, 50,
                16 * 10, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Storage Noob", style);

        style = new SkillStyle(50 * 2, 50,
                16 * 11, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Storage Master", style);

        style = new SkillStyle(50 * 3, 50,
                16 * 12, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Storage Refined", style);

        style = new SkillStyle(50 * 4, 50,
                16 * 13, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Storage Energistic", style);

        style = new SkillStyle(0, 50 * 2,
                16 * 14, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Storage Enthusiast", style);

        style = new SkillStyle(50, 50 * 2,
                16 * 15, 0);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Decorator", style);

        style = new SkillStyle(50 * 2, 50 * 2,
                0, 16);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Decorator Pro", style);

        style = new SkillStyle(50 * 3, 50 * 2,
                16, 16);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Engineer", style);

        style = new SkillStyle(50 * 4, 50 * 2,
                16 * 2, 16);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Super Engineer", style);

        style = new SkillStyle(0, 50 * 3,
                16 * 3, 16);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("One with Ender", style);

        style = new SkillStyle(50, 50 * 3,
                16 * 5, 16);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("The Chef", style);

        style = new SkillStyle(50 * 2, 50 * 3,
                16 * 7, 16);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Traveller", style);

        style = new SkillStyle(50 * 3, 50 * 3,
                16 * 8, 16);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Adventurer", style);

        style = new SkillStyle(50 * 4, 50 * 3,
                16 * 9, 16);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Hacker", style);

        style = new SkillStyle(0, 50 * 4,
                16 * 10, 16);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Redstoner", style);

        style = new SkillStyle(50, 50 * 4,
                16 * 11, 16);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Natural Magical", style);

        style = new SkillStyle(50 * 2, 50 * 4,
                16 * 12, 16);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Tech Freak", style);

        style = new SkillStyle(50 * 3, 50 * 4,
                16 * 13, 16);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("The Emerald King", style);

        style = new SkillStyle(50 * 4, 50 * 4,
                16 * 14, 16);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Quarry", style);

        style = new SkillStyle(0, 50 * 5,
                16 * 15, 16);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Spaceman", style);

        style = new SkillStyle(50, 50 * 5,
                0, 16 * 2);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Total Control", style);

        style = new SkillStyle(50 * 2, 50 * 5,
                16, 16 * 2);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Let there be light!", style);

        style = new SkillStyle(50 * 3, 50 * 5,
                16 * 2, 16 * 2);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Energetic", style);

        style = new SkillStyle(50 * 4, 50 * 5,
                16 * 3, 16 * 2);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Thermal Technician", style);

        style = new SkillStyle(0, 50 * 6,
                16 * 4, 16 * 2);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Plastic Technician", style);

        style = new SkillStyle(50, 50 * 6,
                16 * 5, 16 * 2);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Extended Possibilities", style);

        style = new SkillStyle(50 * 2, 50 * 6,
                16 * 6, 16 * 2);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Power Overwhelming", style);

        style = new SkillStyle(50 * 3, 50 * 6,
                16 * 7, 16 * 2);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Nuclear Power", style);

        style = new SkillStyle(50 * 4, 50 * 6,
                16 * 9, 16 * 2);
        style.frameType = SkillFrame.STAR;
        styles.put("Automatic Genius", style);

        // ---- 1.2

        style = new SkillStyle(0, 50 * 7,
                16 * 11, 16 * 2);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Creator", style);

        style = new SkillStyle(50, 50 * 7,
                16 * 12, 16 * 2);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Piper", style);

        style = new SkillStyle(50 * 2, 50 * 7,
                16 * 13, 16 * 2);
        style.frameType = SkillFrame.RECTANGULAR;
        styles.put("Uber Sand", style);

        style = new SkillStyle(50 * 3, 50 * 7,
                16 * 10, 16 * 2);
        style.frameType = SkillFrame.STAR;
        styles.put("Power Manager", style);
    }

}
