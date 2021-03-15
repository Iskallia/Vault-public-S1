package iskallia.vault.client.gui.helper;

import net.minecraft.util.math.vector.Vector2f;

// ~~The moment you know you missed Typescript :c~~
// Not anymore lol XD
public class Rectangle {

    public int x0, y0;
    public int x1, y1;

    public Rectangle() {}

    public Rectangle(Rectangle other) {
        this.x0 = other.x0;
        this.y0 = other.y0;
        this.x1 = other.x1;
        this.y1 = other.y1;
    }

    public int getWidth() {
        return x1 - x0;
    }

    public int getHeight() {
        return y1 - y0;
    }

    public void setWidth(int width) {
        this.x1 = this.x0 + width;
    }

    public void setHeight(int height) {
        this.y1 = this.y0 + height;
    }

    public boolean contains(int x, int y) {
        return x0 <= x && x <= x1
                && y0 <= y && y <= y1;
    }

    public Vector2f midpoint() {
        return new Vector2f(
                (x1 + x0) / 2f,
                (y1 + y0) / 2f
        );
    }

}
