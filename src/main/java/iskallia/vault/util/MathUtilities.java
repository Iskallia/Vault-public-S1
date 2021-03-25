package iskallia.vault.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Random;

public class MathUtilities {

    public static float randomFloat(float min, float max) {
        return new Random().nextFloat() * (max - min) + min;
    }

    public static int getRandomInt(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    public static double map(double value, double x0, double y0, double x1, double y1) {
        return x1 + (y1 - x1) * ((value - x0) / (y0 - x0));
    }

    public static double length(Vector2f vec) {
        return Math.sqrt(vec.x * vec.x + vec.y * vec.y);
    }

    public static double extractYaw(Vector3d vec) {
        return Math.atan2(vec.z(), vec.x());
    }

    public static double extractPitch(Vector3d vec) {
        return Math.asin(vec.y() / vec.length());
    }

    public static Vector3d rotatePitch(Vector3d vec, float pitch) {
        float f = MathHelper.cos(pitch);
        float f1 = MathHelper.sin(pitch);
        double d0 = vec.x();
        double d1 = vec.y() * (double) f + vec.z() * (double) f1;
        double d2 = vec.z() * (double) f - vec.y() * (double) f1;
        return new Vector3d(d0, d1, d2);
    }

    public static Vector3d rotateYaw(Vector3d vec, float yaw) {
        float f = MathHelper.cos(yaw);
        float f1 = MathHelper.sin(yaw);
        double d0 = vec.x() * (double) f + vec.z() * (double) f1;
        double d1 = vec.y();
        double d2 = vec.z() * (double) f - vec.x() * (double) f1;
        return new Vector3d(d0, d1, d2);
    }

    public static Vector3d rotateRoll(Vector3d vec, float roll) {
        float f = MathHelper.cos(roll);
        float f1 = MathHelper.sin(roll);
        double d0 = vec.x() * (double) f + vec.y() * (double) f1;
        double d1 = vec.y() * (double) f - vec.x() * (double) f1;
        double d2 = vec.z();
        return new Vector3d(d0, d1, d2);
    }

}
