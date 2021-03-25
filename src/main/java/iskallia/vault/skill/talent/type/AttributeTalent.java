package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

public class AttributeTalent extends PlayerTalent {

	@Expose private final String attribute;
	@Expose private final Modifier modifier;

	public AttributeTalent(int cost, Attribute attribute, Modifier modifier) {
		this(cost, Registry.ATTRIBUTE.getKey(attribute).toString(), modifier);
	}

	public AttributeTalent(int cost, String attribute, Modifier modifier) {
		super(cost);
		this.attribute = attribute;
		this.modifier = modifier;
	}

	public Attribute getAttribute() {
		return Registry.ATTRIBUTE.get(new ResourceLocation(this.attribute));
	}

	public Modifier getModifier() {
		return this.modifier;
	}

	@Override
	public void onAdded(PlayerEntity player) {
		this.onRemoved(player); //Remove the old attribute modifier just in case.
		this.runIfPresent(player, attributeData -> attributeData.addTransientModifier(this.getModifier().toMCModifier()));
	}

	@Override
	public void tick(PlayerEntity player) {
		this.runIfPresent(player, attributeData -> {
			if(!attributeData.hasModifier(this.getModifier().toMCModifier())) {
				this.onAdded(player); //If the modifier goes away for some reason put it back.
			}
		});
	}

	@Override
	public void onRemoved(PlayerEntity player) {
		this.runIfPresent(player, attributeData -> attributeData.removeModifier(UUID.fromString(this.getModifier().id)));
	}

	public boolean runIfPresent(PlayerEntity player, Consumer<ModifiableAttributeInstance> action) {
		ModifiableAttributeInstance attributeData = player.getAttribute(this.getAttribute());
		if(attributeData == null)return false;
		action.accept(attributeData);
		return true;
	}

	public static class Modifier {
		@Expose public final String id;
		@Expose public final String name;
		@Expose public final double amount;
		@Expose public final int operation;

		public Modifier(String name, double amount, AttributeModifier.Operation operation) {
			this.id = MathHelper.createInsecureUUID(new Random(name.hashCode())).toString();
			this.name = name;
			this.amount = amount;
			this.operation = operation.toValue();
		}

		public AttributeModifier toMCModifier() {
			return new AttributeModifier(UUID.fromString(this.id), this.name,
					this.amount, AttributeModifier.Operation.fromValue(this.operation));
		}
	}

}
