package iskallia.vault.client.gui.helper;

import java.util.function.Function;

public enum Easing {

    LINEAR_IN(x -> x),

    LINEAR_OUT(x -> 1f - x),

    EXPO_OUT(x -> x == 1 ? 1 : 1 - (float) Math.pow(2, -10 * x)),

    EASE_OUT_BOUNCE(x -> {
        float n1 = 7.5625f;
        float d1 = 2.75f;

        if (x < 1 / d1) {
            return n1 * x * x;
        } else if (x < 2 / d1) {
            return n1 * (x -= 1.5f / d1) * x + 0.75f;
        } else if (x < 2.5 / d1) {
            return n1 * (x -= 2.25f / d1) * x + 0.9375f;
        } else {
            return n1 * (x -= 2.625f / d1) * x + 0.984375f;
        }
    });

    // ([0,1]):time -> [0,1]:percentage
    Function<Float, Float> function;

    Easing(Function<Float, Float> function) {
        this.function = function;
    }

    public Function<Float, Float> getFunction() {
        return function;
    }

    public float calc(float time) {
        return function.apply(time);
    }

}
