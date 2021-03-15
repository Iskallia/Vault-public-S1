package iskallia.vault.skill.talent;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.talent.type.PlayerTalent;
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

public class TalentTree implements INBTSerializable<CompoundNBT> {

    private final UUID uuid;
    private List<TalentNode<?>> nodes = new ArrayList<>();

    public TalentTree(UUID uuid) {
        this.uuid = uuid;
        this.add(null, ModConfigs.TALENTS.getAll().stream()
                .map(talentGroup -> new TalentNode<>(talentGroup, 0))
                .toArray(TalentNode<?>[]::new));
    }

    public List<TalentNode<?>> getNodes() {
        return this.nodes;
    }

    public TalentNode<?> getNodeOf(TalentGroup<?> talentGroup) {
        return getNodeByName(talentGroup.getParentName());
    }

    public TalentNode<?> getNodeByName(String name) {
        Optional<TalentNode<?>> talentWrapped = this.nodes.stream().filter(node -> node.getGroup().getParentName().equals(name)).findFirst();
        if (!talentWrapped.isPresent()) {
            TalentNode<?> talentNode = new TalentNode<>(ModConfigs.TALENTS.getByName(name), 0);
            this.nodes.add(talentNode);
            return talentNode;
        }
        return talentWrapped.get();
    }

    /* ------------------------------------ */

    public TalentTree upgradeTalent(MinecraftServer server, TalentNode<?> talentNode) {
        this.remove(server, talentNode);

        TalentGroup<?> talentGroup = ModConfigs.TALENTS.getByName(talentNode.getGroup().getParentName());
        TalentNode<?> upgradedTalentNode = new TalentNode<>(talentGroup, talentNode.getLevel() + 1);
        this.add(server, upgradedTalentNode);

        return this;
    }

    /* ------------------------------------ */

    public TalentTree add(MinecraftServer server, TalentNode<?>... nodes) {
        for (TalentNode<?> node : nodes) {
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                if (node.isLearned()) {
                    node.getTalent().onAdded(player);
                }
            });
            this.nodes.add(node);
        }

        return this;
    }

    public TalentTree tick(MinecraftServer server) {
        NetcodeUtils.runIfPresent(server, this.uuid, player -> {
            this.nodes.stream().filter(TalentNode::isLearned)
                    .forEach(node -> node.getTalent().tick(player));
        });
        return this;
    }

    public TalentTree remove(MinecraftServer server, TalentNode<?>... nodes) {
        for (TalentNode<?> node : nodes) {
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                if (node.isLearned()) {
                    node.getTalent().onRemoved(player);
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
        this.nodes.stream().map(TalentNode::serializeNBT).forEach(list::add);
        nbt.put("Nodes", list);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        ListNBT list = nbt.getList("Nodes", Constants.NBT.TAG_COMPOUND);
        this.nodes.clear();
        for (int i = 0; i < list.size(); i++) {
            this.add(null, TalentNode.fromNBT(list.getCompound(i), PlayerTalent.class));
        }
    }

}
