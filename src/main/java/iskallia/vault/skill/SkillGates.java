package iskallia.vault.skill;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.type.Research;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.skill.talent.TalentTree;

import java.util.*;

public class SkillGates {

    @Expose private Map<String, Entry> entries;

    public SkillGates() {
        this.entries = new HashMap<>();
    }

    public void addEntry(String skillName, Entry entry) {
        this.entries.put(skillName, entry);
    }

    public List<AbilityGroup<?>> getDependencyAbilities(String abilityName) {
        List<AbilityGroup<?>> abilities = new LinkedList<>();
        Entry entry = entries.get(abilityName);
        if (entry == null) return abilities;
        entry.dependsOn.forEach(dependencyName -> {
            AbilityGroup<?> dependency = ModConfigs.ABILITIES.getByName(dependencyName);
            abilities.add(dependency);
        });
        return abilities;
    }

    public List<AbilityGroup<?>> getLockedByAbilities(String abilityName) {
        List<AbilityGroup<?>> abilities = new LinkedList<>();
        Entry entry = entries.get(abilityName);
        if (entry == null) return abilities;
        entry.lockedBy.forEach(dependencyName -> {
            AbilityGroup<?> dependency = ModConfigs.ABILITIES.getByName(dependencyName);
            abilities.add(dependency);
        });
        return abilities;
    }

    public List<TalentGroup<?>> getDependencyTalents(String talentName) {
        List<TalentGroup<?>> talents = new LinkedList<>();
        Entry entry = entries.get(talentName);
        if (entry == null) return talents;
        entry.dependsOn.forEach(dependencyName -> {
            TalentGroup<?> dependency = ModConfigs.TALENTS.getByName(dependencyName);
            talents.add(dependency);
        });
        return talents;
    }

    public List<TalentGroup<?>> getLockedByTalents(String talentName) {
        List<TalentGroup<?>> talents = new LinkedList<>();
        Entry entry = entries.get(talentName);
        if (entry == null) return talents;
        entry.lockedBy.forEach(dependencyName -> {
            TalentGroup<?> dependency = ModConfigs.TALENTS.getByName(dependencyName);
            talents.add(dependency);
        });
        return talents;
    }

    public List<Research> getDependencyResearches(String researchName) {
        List<Research> researches = new LinkedList<>();
        Entry entry = entries.get(researchName);
        if (entry == null) return researches;
        entry.dependsOn.forEach(dependencyName -> {
            Research dependency = ModConfigs.RESEARCHES.getByName(dependencyName);
            researches.add(dependency);
        });
        return researches;
    }

    public List<Research> getLockedByResearches(String researchName) {
        List<Research> researches = new LinkedList<>();
        Entry entry = entries.get(researchName);
        if (entry == null) return researches;
        entry.lockedBy.forEach(dependencyName -> {
            Research dependency = ModConfigs.RESEARCHES.getByName(dependencyName);
            researches.add(dependency);
        });
        return researches;
    }

    public boolean isLocked(String researchName, ResearchTree researchTree) {
        SkillGates gates = ModConfigs.SKILL_GATES.getGates();

        List<String> researchesDone = researchTree.getResearchesDone();

        for (Research dependencyResearch : gates.getDependencyResearches(researchName)) {
            if (!researchesDone.contains(dependencyResearch.getName()))
                return true;
        }

        for (Research lockedByResearch : gates.getLockedByResearches(researchName)) {
            if (researchesDone.contains(lockedByResearch.getName()))
                return true;
        }

        return false;
    }

    public boolean isLocked(TalentGroup<?> talent, TalentTree talentTree) {
        SkillGates gates = ModConfigs.SKILL_GATES.getGates();

        for (TalentGroup<?> dependencyTalent : gates.getDependencyTalents(talent.getParentName())) {
            if (!talentTree.getNodeOf(dependencyTalent).isLearned())
                return true;
        }

        for (TalentGroup<?> lockedByTalent : gates.getLockedByTalents(talent.getParentName())) {
            if (talentTree.getNodeOf(lockedByTalent).isLearned())
                return true;
        }

        return false;
    }

    public static class Entry {
        @Expose private List<String> dependsOn;
        @Expose private List<String> lockedBy;

        public Entry() {
            this.dependsOn = new LinkedList<>();
            this.lockedBy = new LinkedList<>();
        }

        public void setDependsOn(String... skills) {
            dependsOn.addAll(Arrays.asList(skills));
        }

        public void setLockedBy(String... skills) {
            lockedBy.addAll(Arrays.asList(skills));
        }
    }

}
