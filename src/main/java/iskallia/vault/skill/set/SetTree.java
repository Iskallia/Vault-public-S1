package iskallia.vault.skill.set;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.util.NetcodeUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class SetTree implements INBTSerializable<CompoundNBT> {

    private final UUID uuid;
    private List<SetNode<?>> nodes = new ArrayList<>();

    public SetTree(UUID uuid) {
        this.uuid = uuid;
    }

    public List<SetNode<?>> getNodes() {
        return this.nodes;
    }

    public SetNode<?> getNodeOf(TalentGroup<?> talentGroup) {
        return getNodeByName(talentGroup.getParentName());
    }

    public SetNode<?> getNodeByName(String name) {
        Optional<SetNode<?>> talentWrapped = this.nodes.stream().filter(node -> node.getGroup().getParentName().equals(name)).findFirst();
        if (!talentWrapped.isPresent()) {
            SetNode<?> talentNode = new SetNode<>(ModConfigs.SETS.getByName(name), 0);
            this.nodes.add(talentNode);
            return talentNode;
        }
        return talentWrapped.get();
    }

    /* ------------------------------------ */

    public SetTree add(MinecraftServer server, SetNode<?>... nodes) {
        for (SetNode<?> node : nodes) {
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                if (node.isActive()) {
                    node.getSet().onAdded(player);
                }
            });
            this.nodes.add(node);
        }

        return this;
    }

    public SetTree tick(MinecraftServer server) {
        NetcodeUtils.runIfPresent(server, this.uuid, player -> {
            this.nodes.removeIf(node -> node.getLevel() == 0);

            List<SetNode<?>> toRemove = this.nodes.stream()
                    .filter(SetNode::isActive)
                    .filter(setNode -> !setNode.getSet().shouldBeActive(player))
                    .collect(Collectors.toList());

            toRemove.forEach(setNode -> this.remove(server, setNode));

            ModConfigs.SETS.getAll().stream()
                    .filter(setGroup -> this.nodes.stream()
                            .map(setNode -> setNode.getGroup().getName())
                            .noneMatch(s -> s.equals(setGroup.getName())))
                    .forEach(setGroup -> {
                        for(int i = setGroup.getMaxLevel(); i > 0; i--) {
                            PlayerSet set = setGroup.getSet(i);

                            if(set.shouldBeActive(player)) {
                                this.add(server, new SetNode<>(setGroup, i));
                                break;
                            }
                        }
                    });

            this.nodes.forEach(setNode -> setNode.getSet().onTick(player));
         });

        return this;
    }

    public SetTree remove(MinecraftServer server, SetNode<?>... nodes) {
        for (SetNode<?> node : nodes) {
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                if (node.isActive()) {
                    node.getSet().onRemoved(player);
                }
            });
            this.nodes.remove(node);
        }

        return this;
    }

    /* ------------------------------------ */

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        ListNBT list = new ListNBT();
        this.nodes.stream().map(SetNode::serializeNBT).forEach(list::add);
        nbt.put("Nodes", list);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        ListNBT list = nbt.getList("Nodes", Constants.NBT.TAG_COMPOUND);
        this.nodes.clear();
        for (int i = 0; i < list.size(); i++) {
            this.add(null, SetNode.fromNBT(list.getCompound(i), PlayerSet.class));
        }
    }

}
