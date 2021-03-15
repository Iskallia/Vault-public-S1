package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.SkillGates;

public class SkillGatesConfig extends Config {

    @Expose private SkillGates SKILL_GATES;

    @Override
    public String getName() {
        return "skill_gates";
    }

    public SkillGates getGates() {
        return SKILL_GATES;
    }

    @Override
    protected void reset() {
        SKILL_GATES = new SkillGates();
        SkillGates.Entry gateEntry;

        // Talents
        gateEntry = new SkillGates.Entry();
        gateEntry.setLockedBy(ModConfigs.TALENTS.VAMPIRISM.getParentName());
        SKILL_GATES.addEntry(ModConfigs.TALENTS.REGENERATION.getParentName(), gateEntry);

        gateEntry = new SkillGates.Entry();
        gateEntry.setLockedBy(ModConfigs.TALENTS.REGENERATION.getParentName());
        SKILL_GATES.addEntry(ModConfigs.TALENTS.VAMPIRISM.getParentName(), gateEntry);

        // Researches
        gateEntry = new SkillGates.Entry();
        gateEntry.setDependsOn("Storage Noob");
        SKILL_GATES.addEntry("Storage Master", gateEntry);

        gateEntry = new SkillGates.Entry();
        gateEntry.setDependsOn("Storage Master");
        SKILL_GATES.addEntry("Storage Refined", gateEntry);

        gateEntry = new SkillGates.Entry();
        gateEntry.setDependsOn("Storage Refined");
        SKILL_GATES.addEntry("Storage Energistic", gateEntry);

        gateEntry = new SkillGates.Entry();
        gateEntry.setDependsOn("Storage Energistic");
        SKILL_GATES.addEntry("Storage Enthusiast", gateEntry);

        gateEntry = new SkillGates.Entry();
        gateEntry.setDependsOn("Decorator");
        SKILL_GATES.addEntry("Decorator Pro", gateEntry);

        gateEntry = new SkillGates.Entry();
        gateEntry.setDependsOn("Tech Freak");
        SKILL_GATES.addEntry("Nuclear Power", gateEntry);
    }

}
