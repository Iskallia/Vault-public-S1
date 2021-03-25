package iskallia.vault.init;

import iskallia.vault.container.*;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.talent.TalentTree;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.network.IContainerFactory;

import java.util.Optional;
import java.util.UUID;

public class ModContainers {

    public static ContainerType<SkillTreeContainer> SKILL_TREE_CONTAINER;
    public static ContainerType<VaultCrateContainer> VAULT_CRATE_CONTAINER;
    public static ContainerType<VendingMachineContainer> VENDING_MACHINE_CONTAINER;
    public static ContainerType<AdvancedVendingContainer> ADVANCED_VENDING_MACHINE_CONTAINER;
    public static ContainerType<RenamingContainer> RENAMING_CONTAINER;
    public static ContainerType<KeyPressContainer> KEY_PRESS_CONTAINER;
    public static ContainerType<GlobalTraderContainer> TRADER_CONTAINER;

    public static void register(RegistryEvent.Register<ContainerType<?>> event) {
        SKILL_TREE_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
            UUID uniqueID = inventory.player.getUUID();
            AbilityTree abilityTree = new AbilityTree(uniqueID);
            abilityTree.deserializeNBT(Optional.ofNullable(buffer.readNbt()).orElse(new CompoundNBT()));
            TalentTree talentTree = new TalentTree(uniqueID);
            talentTree.deserializeNBT(Optional.ofNullable(buffer.readNbt()).orElse(new CompoundNBT()));
            ResearchTree researchTree = new ResearchTree(uniqueID);
            researchTree.deserializeNBT(Optional.ofNullable(buffer.readNbt()).orElse(new CompoundNBT()));
            return new SkillTreeContainer(windowId, abilityTree, talentTree, researchTree);
        });

        VAULT_CRATE_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
            World world = inventory.player.getCommandSenderWorld();
            BlockPos pos = buffer.readBlockPos();
            return new VaultCrateContainer(windowId, world, pos, inventory, inventory.player);
        });

        VENDING_MACHINE_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
            World world = inventory.player.getCommandSenderWorld();
            BlockPos pos = buffer.readBlockPos();
            return new VendingMachineContainer(windowId, world, pos, inventory, inventory.player);
        });

        ADVANCED_VENDING_MACHINE_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
            World world = inventory.player.getCommandSenderWorld();
            BlockPos pos = buffer.readBlockPos();
            return new AdvancedVendingContainer(windowId, world, pos, inventory, inventory.player);
        });

        RENAMING_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
            CompoundNBT nbt = buffer.readNbt();
            return new RenamingContainer(windowId, nbt == null ? new CompoundNBT() : nbt);
        });

        KEY_PRESS_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
            PlayerEntity player = inventory.player;
            return new KeyPressContainer(windowId, player);
        });

        TRADER_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
            World world = inventory.player.getCommandSenderWorld();
            BlockPos pos = buffer.readBlockPos();
            CompoundNBT nbt = buffer.readNbt();
            ListNBT playerTrades = nbt == null ? null : nbt.getList("PlayerTradesList", Constants.NBT.TAG_COMPOUND);
            return new GlobalTraderContainer(windowId, world, pos, inventory, inventory.player, playerTrades);
        });

        event.getRegistry().registerAll(
                SKILL_TREE_CONTAINER.setRegistryName("ability_tree"),
                VAULT_CRATE_CONTAINER.setRegistryName("vault_crate"),
                VENDING_MACHINE_CONTAINER.setRegistryName("vending_machine"),
                ADVANCED_VENDING_MACHINE_CONTAINER.setRegistryName("advanced_vending_machine"),
                RENAMING_CONTAINER.setRegistryName("renaming_container"),
                KEY_PRESS_CONTAINER.setRegistryName("key_press_container"),
                TRADER_CONTAINER.setRegistryName("trader_container")
        );


    }

    private static <T extends Container> ContainerType<T> createContainerType(IContainerFactory<T> factory) {
        return new ContainerType<T>(factory);
    }

}