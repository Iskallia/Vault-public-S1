package iskallia.vault.client.gui.helper;

import iskallia.vault.Vault;
import iskallia.vault.util.ResourceBoundary;

public enum SkillFrame {

	STAR(new ResourceBoundary(Vault.id("textures/gui/skill-widget.png"), 0, 31, 30, 30)),
	RECTANGULAR(new ResourceBoundary(Vault.id("textures/gui/skill-widget.png"), 30, 31, 30, 30));

	ResourceBoundary resourceBoundary;

	SkillFrame(ResourceBoundary resourceBoundary) {
		this.resourceBoundary = resourceBoundary;
	}

	public ResourceBoundary getResourceBoundary() {
		return resourceBoundary;
	}

}
