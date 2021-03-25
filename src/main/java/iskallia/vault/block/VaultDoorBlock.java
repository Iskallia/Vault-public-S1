package iskallia.vault.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
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

import net.minecraft.block.AbstractBlock.Properties;

public class VaultDoorBlock extends DoorBlock {

    public static final List<VaultDoorBlock> VAULT_DOORS = new ArrayList<>();

    protected Item keyItem;

    public VaultDoorBlock(Item keyItem) {
        super(Properties.of(Material.WOOD, MaterialColor.DIAMOND)
                .strength(-1.0F, 3600000.0F)
                .sound(SoundType.METAL)
                .noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, Boolean.FALSE)
                .setValue(HINGE, DoorHingeSide.LEFT)
                .setValue(POWERED, Boolean.FALSE)
                .setValue(HALF, DoubleBlockHalf.LOWER));
        this.keyItem = keyItem;
        VAULT_DOORS.add(this);
    }

    public Item getKeyItem() {
        return keyItem;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack heldStack = player.getItemInHand(hand);
        Boolean isOpen = state.getValue(OPEN);

        if (!isOpen && heldStack.getItem() == getKeyItem()) {
            heldStack.shrink(1);
            return super.use(state, world, pos, player, hand, hit);
        }

        return ActionResultType.PASS;
    }


}
