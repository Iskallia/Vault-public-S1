package iskallia.vault.container;

import iskallia.vault.init.ModContainers;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.talent.TalentTree;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

// Wutax calls this iskall-proofing
// I call that my stupidity XD --iGoodie
public class SkillTreeContainer extends Container {

    private AbilityTree abilityTree;
    private TalentTree talentTree;
    private ResearchTree researchTree;

    public SkillTreeContainer(int windowId, AbilityTree abilityTree, TalentTree talentTree, ResearchTree researchTree) {
        super(ModContainers.SKILL_TREE_CONTAINER, windowId);
        this.abilityTree = abilityTree;
        this.talentTree = talentTree;
        this.researchTree = researchTree;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    public AbilityTree getAbilityTree() {
        return abilityTree;
    }

    public TalentTree getTalentTree() {
        return talentTree;
    }

    public ResearchTree getResearchTree() {
        return researchTree;
    }

}
