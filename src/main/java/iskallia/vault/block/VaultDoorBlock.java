package iskallia.vault.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class VaultDoorBlock extends DoorBlock {

    public static final List<VaultDoorBlock> VAULT_DOORS = new ArrayList<>();

    protected Item keyItem;

    public VaultDoorBlock(Item keyItem) {
        super(Properties.create(Material.WOOD, MaterialColor.DIAMOND)
                .hardnessAndResistance(-1.0F, 3600000.0F)
                .sound(SoundType.METAL)
                .notSolid());
        this.setDefaultState(this.getStateContainer().getBaseState()
                .with(FACING, Direction.NORTH)
                .with(OPEN, Boolean.FALSE)
                .with(HINGE, DoorHingeSide.LEFT)
                .with(POWERED, Boolean.FALSE)
                .with(HALF, DoubleBlockHalf.LOWER));
        this.keyItem = keyItem;
        VAULT_DOORS.add(this);
    }

    public Item getKeyItem() {
        return keyItem;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack heldStack = player.getHeldItem(hand);
        Boolean isOpen = state.get(OPEN);

        if (!isOpen && heldStack.getItem() == getKeyItem()) {
            heldStack.shrink(1);
            return super.onBlockActivated(state, world, pos, player, hand, hit);
        }

        return ActionResultType.PASS;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        //Not reacting to neighbor changes, esp. not redstone
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.IGNORE;
    }
}
