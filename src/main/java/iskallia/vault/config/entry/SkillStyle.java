package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import iskallia.vault.client.gui.helper.SkillFrame;

public class SkillStyle {

    @Expose public int x, y;
    @Expose public SkillFrame frameType;
    @Expose public int u, v;

    public SkillStyle() {}

    public SkillStyle(int x, int y, int u, int v) {
        this.x = x;
        this.y = y;
        this.u = u;
        this.v = v;
    }

}