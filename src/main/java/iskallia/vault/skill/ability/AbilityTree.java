package iskallia.vault.skill.ability;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.AbilityActivityMessage;
import iskallia.vault.network.message.AbilityFocusMessage;
import iskallia.vault.network.message.AbilityKnownOnesMessage;
import iskallia.vault.skill.ability.type.PlayerAbility;
import iskallia.vault.util.NetcodeUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.*;
import java.util.stream.Collectors;

public class AbilityTree implements INBTSerializable<CompoundNBT> {

    private final UUID uuid;
    private List<AbilityNode<?>> nodes = new ArrayList<>();
    private HashMap<Integer, Integer> cooldowns = new HashMap<>();

    private int focusedAbilityIndex;
    private boolean active;

    private boolean swappingPerformed;
    private boolean swappingLocked;

    public AbilityTree(UUID uuid) {
        this.uuid = uuid;
        this.add(null, ModConfigs.ABILITIES.getAll().stream()
                .map(abilityGroup -> new AbilityNode<>(abilityGroup, 0))
                .toArray(AbilityNode[]::new));
    }

    public List<AbilityNode<?>> getNodes() {
        return nodes;
    }

    public List<AbilityNode<?>> learnedNodes() {
        return nodes.stream()
                .filter(AbilityNode::isLearned)
                .collect(Collectors.toList());
    }

    public AbilityNode<?> getFocusedAbility() {
        List<AbilityNode<?>> learnedNodes = learnedNodes();
        if (learnedNodes.size() == 0) return null;
        return learnedNodes.get(focusedAbilityIndex);
    }

    public AbilityNode<?> getNodeOf(AbilityGroup<?> abilityGroup) {
        return this.getNodeByName(abilityGroup.getParentName());
    }

    public AbilityNode<?> getNodeByName(String name) {
        Optional<AbilityNode<?>> abilityWrapped = this.nodes.stream().filter(node -> node.getGroup().getParentName().equals(name)).findFirst();
        if (!abilityWrapped.isPresent()) {
            AbilityNode<?> abilityNode = new AbilityNode<>(ModConfigs.ABILITIES.getByName(name), 0);
            this.nodes.add(abilityNode);
            return abilityNode;
        }
        return abilityWrapped.get();
    }

    public boolean isActive() {
        return active;
    }

    public void setSwappingLocked(boolean swappingLocked) {
        this.swappingLocked = swappingLocked;
    }

    /* ---------------------------------- */

    public AbilityTree scrollUp(MinecraftServer server) {
        List<AbilityNode<?>> learnedNodes = learnedNodes();

        if (swappingLocked) return this;

        if (learnedNodes.size() != 0) {
            boolean prevActive = this.active;
            this.active = false;

            AbilityNode<?> previouslyFocused = getFocusedAbility();
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                previouslyFocused.getAbility().onBlur(player);
                if (prevActive && previouslyFocused.getAbility().getBehavior() == PlayerAbility.Behavior.PRESS_TO_TOGGLE)
                    previouslyFocused.getAbility().onAction(player, this.active);

                if (prevActive && getFocusedAbility().getAbility().getBehavior() != PlayerAbility.Behavior.HOLD_TO_ACTIVATE)
                    putOnCooldown(server, focusedAbilityIndex, ModConfigs.ABILITIES.cooldownOf(getFocusedAbility(), player));
            });

            this.focusedAbilityIndex++;
            if (this.focusedAbilityIndex >= learnedNodes.size())
                this.focusedAbilityIndex -= learnedNodes.size();

            AbilityNode<?> newFocused = getFocusedAbility();
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                newFocused.getAbility().onFocus(player);
            });

            swappingPerformed = true;
            syncFocusedIndex(server);
            notifyActivity(server);
        }

        return this;
    }

    public AbilityTree scrollDown(MinecraftServer server) {
        List<AbilityNode<?>> learnedNodes = learnedNodes();

        if (swappingLocked) return this;

        if (learnedNodes.size() != 0) {
            boolean prevActive = this.active;
            this.active = false;

            AbilityNode<?> previouslyFocused = getFocusedAbility();
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                previouslyFocused.getAbility().onBlur(player);
                if (prevActive && previouslyFocused.getAbility().getBehavior() == PlayerAbility.Behavior.PRESS_TO_TOGGLE)
                    previouslyFocused.getAbility().onAction(player, this.active);

                if (prevActive && getFocusedAbility().getAbility().getBehavior() != PlayerAbility.Behavior.HOLD_TO_ACTIVATE)
                    putOnCooldown(server, focusedAbilityIndex, ModConfigs.ABILITIES.cooldownOf(getFocusedAbility(), player));
            });

            this.focusedAbilityIndex--;
            if (this.focusedAbilityIndex < 0)
                this.focusedAbilityIndex += learnedNodes.size();

            AbilityNode<?> newFocused = getFocusedAbility();
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                newFocused.getAbility().onFocus(player);
            });

            swappingPerformed = true;
            syncFocusedIndex(server);
            notifyActivity(server);
        }

        return this;
    }

    public void keyDown(MinecraftServer server) {
        AbilityNode<?> focusedAbility = getFocusedAbility();

        if (focusedAbility == null) return;

        PlayerAbility.Behavior behavior = focusedAbility.getAbility().getBehavior();

        if (behavior == PlayerAbility.Behavior.HOLD_TO_ACTIVATE) {
            active = true;
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                focusedAbility.getAbility().onAction(player, active);
            });
            notifyActivity(server, focusedAbilityIndex, 0, active);
        }
    }

    public void keyUp(MinecraftServer server) {
        AbilityNode<?> focusedAbility = getFocusedAbility();

        swappingLocked = false;

        if (focusedAbility == null) return;

        if (swappingPerformed) {
            swappingPerformed = false;
            return;
        }

        if (cooldowns.getOrDefault(focusedAbilityIndex, 0) > 0) return;

        PlayerAbility.Behavior behavior = focusedAbility.getAbility().getBehavior();

        if (behavior == PlayerAbility.Behavior.PRESS_TO_TOGGLE) {
            active = !active;
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                focusedAbility.getAbility().onAction(player, active);
                putOnCooldown(server, focusedAbilityIndex, ModConfigs.ABILITIES.cooldownOf(getFocusedAbility(), player));
            });
        } else if (behavior == PlayerAbility.Behavior.HOLD_TO_ACTIVATE) {
            active = false;
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                focusedAbility.getAbility().onAction(player, active);
            });
            notifyActivity(server);

        } else if (behavior == PlayerAbility.Behavior.RELEASE_TO_PERFORM) {
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                focusedAbility.getAbility().onAction(player, active);
                putOnCooldown(server, focusedAbilityIndex, ModConfigs.ABILITIES.cooldownOf(getFocusedAbility(), player));
            });
        }
    }

    public void quickSelectAbility(MinecraftServer server, int abilityIndex) {
        List<AbilityNode<?>> learnedNodes = learnedNodes();

        if (learnedNodes.size() != 0) {
            boolean prevActive = this.active;
            this.active = false;

            AbilityNode<?> previouslyFocused = getFocusedAbility();

            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                previouslyFocused.getAbility().onBlur(player);
                if (prevActive && previouslyFocused.getAbility().getBehavior() == PlayerAbility.Behavior.PRESS_TO_TOGGLE)
                    previouslyFocused.getAbility().onAction(player, this.active);

                if (prevActive && getFocusedAbility().getAbility().getBehavior() != PlayerAbility.Behavior.HOLD_TO_ACTIVATE)
                    putOnCooldown(server, focusedAbilityIndex, ModConfigs.ABILITIES.cooldownOf(getFocusedAbility(), player));
            });


            this.focusedAbilityIndex = abilityIndex;

            AbilityNode<?> newFocused = getFocusedAbility();
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                newFocused.getAbility().onFocus(player);
            });

            syncFocusedIndex(server);
        }
    }

    public void cancelKeyDown(MinecraftServer server) {
        AbilityNode<?> focusedAbility = getFocusedAbility();

        if (focusedAbility == null) return;

        PlayerAbility.Behavior behavior = focusedAbility.getAbility().getBehavior();

        if (behavior == PlayerAbility.Behavior.HOLD_TO_ACTIVATE) {
            active = false;
            swappingLocked = false;
            swappingPerformed = false;
        }

        notifyActivity(server);
    }

    public void putOnCooldown(MinecraftServer server, int abilityIndex, int cooldownTicks) {
        this.cooldowns.put(abilityIndex, cooldownTicks);
        notifyActivity(server, abilityIndex, cooldownTicks, 0);
    }

    public AbilityTree upgradeAbility(MinecraftServer server, AbilityNode<?> abilityNode) {
        this.remove(server, abilityNode);

        AbilityGroup<?> abilityGroup = ModConfigs.ABILITIES.getByName(abilityNode.getGroup().getParentName());
        AbilityNode<?> upgradedAbilityNode = new AbilityNode<>(abilityGroup, abilityNode.getLevel() + 1);
        this.add(server, upgradedAbilityNode);

        return this;
    }

    /* ---------------------------------- */

    public AbilityTree add(MinecraftServer server, AbilityNode<?>... nodes) {
        for (AbilityNode<?> node : nodes) {
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                if (node.isLearned()) {
                    node.getAbility().onAdded(player);
                }
            });
            this.nodes.add(node);
        }

        this.focusedAbilityIndex = MathHelper.clamp(this.focusedAbilityIndex,
                0, learnedNodes().size() - 1);

        return this;
    }

    public AbilityTree remove(MinecraftServer server, AbilityNode<?>... nodes) {
        List<AbilityNode<?>> learnedNodes = learnedNodes();
        for (int i = 0; i < learnedNodes.size(); i++) {
            putOnCooldown(server, i, 0);
        }

        for (AbilityNode<?> node : nodes) {
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                if (node.isLearned())
                    node.getAbility().onRemoved(player);
            });
            this.nodes.remove(node);
        }

        this.focusedAbilityIndex = MathHelper.clamp(this.focusedAbilityIndex,
                0, learnedNodes().size() - 1);

        return this;
    }

    /* ---------------------------------- */

    public void tick(TickEvent.PlayerTickEvent event) {
        AbilityNode<?> focusedAbility = getFocusedAbility();

        if (focusedAbility != null) {
            focusedAbility.getAbility().onTick(event.player, isActive());
        }

        for (Integer abilityIndex : cooldowns.keySet()) {
            cooldowns.computeIfPresent(abilityIndex, (index, cooldown) -> cooldown - 1);
            notifyCooldown(event.player.getServer(), abilityIndex, cooldowns.getOrDefault(abilityIndex, 0));
        }
        cooldowns.entrySet().removeIf(cooldown -> cooldown.getValue() <= 0);
    }

    public void sync(MinecraftServer server) {
        syncTree(server);
        syncFocusedIndex(server);
        notifyActivity(server);
    }

    public void syncTree(MinecraftServer server) {
        NetcodeUtils.runIfPresent(server, this.uuid, player -> {
            ModNetwork.CHANNEL.sendTo(
                    new AbilityKnownOnesMessage(this),
                    player.connection.netManager,
                    NetworkDirection.PLAY_TO_CLIENT
            );
        });
    }

    public void syncFocusedIndex(MinecraftServer server) {
        NetcodeUtils.runIfPresent(server, this.uuid, player -> {
            ModNetwork.CHANNEL.sendTo(
                    new AbilityFocusMessage(this.focusedAbilityIndex),
                    player.connection.netManager,
                    NetworkDirection.PLAY_TO_CLIENT
            );
        });
    }

    public void notifyActivity(MinecraftServer server) {
        notifyActivity(server,
                this.focusedAbilityIndex,
                this.cooldowns.getOrDefault(this.focusedAbilityIndex, 0),
                this.active);
    }

    public void notifyCooldown(MinecraftServer server, int abilityIndex, int cooldown) {
        notifyActivity(server, abilityIndex, cooldown, 0);
    }

    public void notifyActivity(MinecraftServer server, int abilityIndex, int cooldown, boolean active) {
        notifyActivity(server, abilityIndex, cooldown, active ? 2 : 1);
    }

    public void notifyActivity(MinecraftServer server, int abilityIndex, int cooldown, int activeFlag) {
        NetcodeUtils.runIfPresent(server, this.uuid, player -> {
            ModNetwork.CHANNEL.sendTo(
                    new AbilityActivityMessage(abilityIndex, cooldown, activeFlag),
                    player.connection.netManager,
                    NetworkDirection.PLAY_TO_CLIENT
            );
        });
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        ListNBT list = new ListNBT();
        this.nodes.stream().map(AbilityNode::serializeNBT).forEach(list::add);
        nbt.put("Nodes", list);
        nbt.putInt("FocusedIndex", focusedAbilityIndex);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        ListNBT list = nbt.getList("Nodes", Constants.NBT.TAG_COMPOUND);
        this.nodes.clear();
        for (int i = 0; i < list.size(); i++) {
            this.add(null, AbilityNode.fromNBT(list.getCompound(i), PlayerAbility.class));
        }
        this.focusedAbilityIndex = MathHelper.clamp(nbt.getInt("FocusedIndex"),
                0, learnedNodes().size() - 1);
    }

}
