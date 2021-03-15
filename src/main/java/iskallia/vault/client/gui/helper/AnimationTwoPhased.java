package iskallia.vault.client.gui.helper;

public class AnimationTwoPhased {

    protected Easing initEasing = Easing.LINEAR_IN;
    protected Easing endEasing = Easing.LINEAR_OUT;

    protected boolean paused;

    protected float value;
    protected float initValue, midValue, endValue;

    protected int elapsedTime;
    protected int animationTime;

    public AnimationTwoPhased(float initValue, float midValue, float endValue, int animationTime) {
        this.initValue = initValue;
        this.midValue = midValue;
        this.endValue = endValue;
        this.elapsedTime = 0;
        this.animationTime = animationTime;
        this.value = initValue;
        this.paused = true;
    }

    public AnimationTwoPhased withEasing(Easing initEasing, Easing endEasing) {
        this.initEasing = initEasing;
        this.endEasing = endEasing;
        return this;
    }

    public float getValue() {
        return value;
    }

    public void tick(int deltaTime) {
        if (paused) return;

        elapsedTime = Math.min(elapsedTime + deltaTime, animationTime);

        float elapsedPercent = getElapsedPercentage();

        if (elapsedTime < 0.5f * animationTime) {
            float value = initEasing.calc(2f * elapsedPercent);
            this.value = value * (midValue - initValue) + initValue;

        } else {
            float value = initEasing.calc(2f * elapsedPercent - 1f);
            this.value = value * (endValue - midValue) + midValue;
        }

        if (elapsedTime >= animationTime) {
            pause();
        }
    }

    public void changeValues(float initValue, float midValue, float endValue) {
        this.initValue = initValue;
        this.midValue = midValue;
        this.endValue = endValue;
        float elapsedPercent = getElapsedPercentage();
        if (elapsedTime < 0.5f * animationTime) {
            float value = initEasing.calc(2f * elapsedPercent);
            this.value = value * (midValue - initValue) + initValue;
        } else {
            float value = initEasing.calc(2f * elapsedPercent - 1f);
            this.value = value * (endValue - midValue) + midValue;
        }
    }

    public float getElapsedPercentage() {
        return ((float) elapsedTime) / animationTime;
    }

    public void pause() {
        this.paused = true;
    }

    public void play() {
        this.paused = false;
    }

    public void reset() {
        this.value = initValue;
        this.elapsedTime = 0;
    }

}
