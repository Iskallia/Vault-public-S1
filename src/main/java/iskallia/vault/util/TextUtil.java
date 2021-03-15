package iskallia.vault.util;

import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class TextUtil {
    static final TextFormatting[] rainbow = new TextFormatting[]{
            TextFormatting.RED,
            TextFormatting.GOLD,
            TextFormatting.YELLOW,
            TextFormatting.GREEN,
            TextFormatting.BLUE,
            TextFormatting.LIGHT_PURPLE,
            TextFormatting.DARK_PURPLE
    };

    public static StringTextComponent applyRainbowTo(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            sb.append(getNextColor(i));
            sb.append(c);
        }
        return new StringTextComponent(sb.toString());
    }

    private static TextFormatting getNextColor(int index) {
        return rainbow[index % rainbow.length];
    }

}
