package iskallia.vault.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class VectorHelper {

	public static Vector3d getDirectionNormalized(Vector3d destination, Vector3d origin) {
		return new Vector3d(destination.x - origin.x, destination.y - origin.y, destination.z - origin.z).normalize();
	}

	public static Vector3d getVectorFromPos(BlockPos pos) {
		return new Vector3d(pos.getX(), pos.getY(), pos.getZ());
	}

	public static Vector3d add(Vector3d a, Vector3d b) {
		return new Vector3d(a.x + b.x, a.y + b.y, a.z + b.z);
	}

	public static Vector3d subtract(Vector3d a, Vector3d b) {
		return new Vector3d(a.x - b.x, a.y - b.y, a.z - b.z);
	}

	public static Vector3d multiply(Vector3d velocity, float speed) {

		return new Vector3d(velocity.x * speed, velocity.y * speed, velocity.z * speed);
	}

	public static Vector3d getMovementVelocity(Vector3d current, Vector3d target, float speed) {

		Vector3d velocity = VectorHelper.multiply(getDirectionNormalized(target, current), speed);

		return velocity;
	}

}
