package iskallia.vault.util;

import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;

// Y E S
public class LazySoundType extends SoundType {

    private boolean initialized;
    protected float lazyVolume;
    protected float lazyPitch;
    protected SoundEvent lazyBreakSound;
    protected SoundEvent lazyStepSound;
    protected SoundEvent lazyPlaceSound;
    protected SoundEvent lazyHitSound;
    protected SoundEvent lazyFallSound;

    public LazySoundType(SoundType vanillaType) {
        super(1f, 1f, vanillaType.getBreakSound(),
                vanillaType.getStepSound(),
                vanillaType.getPlaceSound(),
                vanillaType.getHitSound(),
                vanillaType.getFallSound());
    }

    public LazySoundType() {
        this(SoundType.STONE);
    }

    public void initialize(float volumeIn, float pitchIn, SoundEvent breakSoundIn, SoundEvent stepSoundIn, SoundEvent placeSoundIn, SoundEvent hitSoundIn, SoundEvent fallSoundIn) {
        if (initialized) throw new InternalError("LazySoundTypes should be initialized only once!");
        this.lazyVolume = volumeIn;
        this.lazyPitch = pitchIn;
        this.lazyBreakSound = breakSoundIn;
        this.lazyStepSound = stepSoundIn;
        this.lazyPlaceSound = placeSoundIn;
        this.lazyHitSound = hitSoundIn;
        this.lazyFallSound = fallSoundIn;
        initialized = true;
    }

    @Override
    public float getVolume() {
        return lazyVolume;
    }

    @Override
    public float getPitch() {
        return lazyPitch;
    }

    @Override
    public SoundEvent getBreakSound() {
        return lazyBreakSound == null ? super.getBreakSound() : lazyBreakSound;
    }

    @Override
    public SoundEvent getStepSound() {
        return lazyStepSound == null ? super.getStepSound() : lazyStepSound;
    }

    @Override
    public SoundEvent getPlaceSound() {
        return lazyPlaceSound == null ? super.getPlaceSound() : lazyPlaceSound;
    }

    @Override
    public SoundEvent getHitSound() {
        return lazyHitSound == null ? super.getHitSound() : lazyHitSound;
    }

    @Override
    public SoundEvent getFallSound() {
        return lazyFallSound == null ? super.getFallSound() : lazyFallSound;
    }

}
