package iskallia.vault.client.gui.tab;

import iskallia.vault.client.gui.screen.SkillTreeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.ITextComponent;

import java.util.HashMap;
import java.util.Map;

public abstract class SkillTab extends Screen {

    protected SkillTreeScreen parentScreen;

    // HOW TO PATCH SEMI-PERSISTENCY HAXX 101
    protected static Map<Class<? extends SkillTab>, Vector2f> persistedTranslations = new HashMap<>();
    protected static Map<Class<? extends SkillTab>, Float> persistedScales = new HashMap<>();

    protected Vector2f viewportTranslation;
    protected float viewportScale;
    protected boolean dragging;
    protected Vector2f grabbedPos;

    protected SkillTab(SkillTreeScreen parentScreen, ITextComponent title) {
        super(title);
        this.parentScreen = parentScreen;
        this.viewportTranslation = persistedTranslations.computeIfAbsent(getClass(), clazz -> new Vector2f(0, 0));
        this.viewportScale = persistedScales.computeIfAbsent(getClass(), clazz -> 1f);
        this.dragging = false;
        this.grabbedPos = new Vector2f(0, 0);
    }

    public abstract void refresh();

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.dragging = true;
        this.grabbedPos = new Vector2f((float) mouseX, (float) mouseY);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (dragging) {
            float dx = (float) (mouseX - grabbedPos.x) / viewportScale;
            float dy = (float) (mouseY - grabbedPos.y) / viewportScale;
            this.viewportTranslation = new Vector2f(
                    viewportTranslation.x + dx,
                    viewportTranslation.y + dy);
            this.grabbedPos = new Vector2f((float) mouseX, (float) mouseY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        Vector2f midpoint = parentScreen.getContainerBounds().midpoint();
        boolean mouseScrolled = super.mouseScrolled(mouseX, mouseY, delta);

        double zoomingX = (mouseX - midpoint.x) / viewportScale + viewportTranslation.x;
        double zoomingY = (mouseY - midpoint.y) / viewportScale + viewportTranslation.y;

        int wheel = delta < 0 ? -1 : 1;

        double zoomTargetX = (zoomingX - viewportTranslation.x) / viewportScale;
        double zoomTargetY = (zoomingY - viewportTranslation.y) / viewportScale;

        viewportScale += 0.25 * wheel * viewportScale;
        viewportScale = (float) MathHelper.clamp(viewportScale, 0.5, 5);

        viewportTranslation = new Vector2f(
                (float) (-zoomTargetX * viewportScale + zoomingX),
                (float) (-zoomTargetY * viewportScale + zoomingY)
        );

        return mouseScrolled;
    }

    @Override
    public void removed() {
        System.out.println(getClass().getSimpleName() + " closed.");
        persistedTranslations.put(getClass(), viewportTranslation);
        persistedScales.put(getClass(), viewportScale);
    }

}
