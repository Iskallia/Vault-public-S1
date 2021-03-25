package iskallia.vault.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class TeleportGoal<T extends LivingEntity> extends GoalTask<T> {

	private final Predicate<T> startCondition;
	private final Function<T, Vector3d> targetSupplier;
	private final Consumer<T> postTeleport;

	protected TeleportGoal(T entity, Predicate<T> startCondition, Function<T, Vector3d> targetSupplier, Consumer<T> postTeleport) {
		super(entity);
		this.startCondition = startCondition;
		this.targetSupplier = targetSupplier;
		this.postTeleport = postTeleport;
	}

	public static <T extends LivingEntity> Builder<T> builder(T entity) {
		return new Builder<>(entity);
	}

	@Override
	public boolean canUse() {
		return this.startCondition.test(this.getEntity());
	}

	@Override
	public void start() {
		Vector3d target = this.targetSupplier.apply(this.getEntity());

		if(target != null) {
			boolean teleported = this.getEntity().randomTeleport(target.x(), target.y(), target.z(), true);

			if(teleported) {
				this.postTeleport.accept(this.getEntity());
			}
		}
	}

	public static class Builder<T extends LivingEntity> {
		private final T entity;
		private Predicate<T> startCondition = entity -> false;
		private Function<T, Vector3d> targetSupplier = entity -> null;
		private Consumer<T> postTeleport = entity -> {};

		private Builder(T entity) {
			this.entity = entity;
		}

		public Builder<T> start(Predicate<T> startCondition) {
			this.startCondition = startCondition;
			return this;
		}

		public Builder<T> to(Function<T, Vector3d> targetSupplier) {
			this.targetSupplier = targetSupplier;
			return this;
		}

		public Builder<T> then(Consumer<T> postTeleport) {
			this.postTeleport = postTeleport;
			return this;
		}

		public TeleportGoal<T> build() {
			return new TeleportGoal<>(this.entity, this.startCondition, this.targetSupplier, this.postTeleport);
		}
	}

}
