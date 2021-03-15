package iskallia.vault.network.message;

import iskallia.vault.container.SkillTreeContainer;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerResearchesData;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.function.Supplier;

// From Client to Server
public class OpenSkillTreeMessage {

    public OpenSkillTreeMessage() { }

    public static void encode(OpenSkillTreeMessage message, PacketBuffer buffer) { }

    public static OpenSkillTreeMessage decode(PacketBuffer buffer) {
        return new OpenSkillTreeMessage();
    }

    public static void handle(OpenSkillTreeMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity sender = context.getSender();

            if (sender == null) return;

            PlayerAbilitiesData playerAbilitiesData = PlayerAbilitiesData.get((ServerWorld) sender.world);
            AbilityTree abilityTree = playerAbilitiesData.getAbilities(sender);

            PlayerTalentsData playerTalentsData = PlayerTalentsData.get((ServerWorld) sender.world);
            TalentTree talentTree = playerTalentsData.getTalents(sender);

            PlayerResearchesData playerResearchesData = PlayerResearchesData.get((ServerWorld) sender.world);
            ResearchTree researchTree = playerResearchesData.getResearches(sender);

            NetworkHooks.openGui(
                    sender,
                    new INamedContainerProvider() {
                        @Override
                        public ITextComponent getDisplayName() {
                            return new TranslationTextComponent("container.vault.ability_tree");
                        }

                        @Nullable
                        @Override
                        public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                            return new SkillTreeContainer(i, abilityTree, talentTree, researchTree);
                        }
                    },
                    (buffer) -> {
                        buffer.writeCompoundTag(abilityTree.serializeNBT());
                        buffer.writeCompoundTag(talentTree.serializeNBT());
                        buffer.writeCompoundTag(researchTree.serializeNBT());
                    }
            );
        });
        context.setPacketHandled(true);
    }

}
