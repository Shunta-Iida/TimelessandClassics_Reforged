package com.tac.guns.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.tac.guns.network.PacketHandler;
import com.tac.guns.network.message.MessageSaveItemUpgradeBench;
import com.tac.guns.tileentity.UpgradeBenchTileEntity;
import com.tac.guns.tileentity.WorkbenchTileEntity;
import com.tac.guns.util.VoxelShapeHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class UpgradeBenchBlock extends RotatedObjectBlock implements EntityBlock {
    private final Map<BlockState, VoxelShape> SHAPES = new HashMap<>();

    public UpgradeBenchBlock(final Properties properties) {
        super(properties);
    }

    private VoxelShape getShape(final BlockState state) {
        if (this.SHAPES.containsKey(state)) {
            return this.SHAPES.get(state);
        }
        final Direction direction = state.getValue(HorizontalDirectionalBlock.FACING);
        final List<VoxelShape> shapes = new ArrayList<>();
        shapes.add(Block.box(0.5, 0, 0.5, 15.5, 13, 15.5));
        shapes.add(Block.box(0, 13, 0, 16, 15, 16));
        shapes.add(VoxelShapeHelper.getRotatedShapes(
                VoxelShapeHelper.rotate(Block.box(0, 15, 0, 16, 16, 2), Direction.SOUTH))[direction
                        .get2DDataValue()]);
        final VoxelShape shape = VoxelShapeHelper.combineAll(shapes);
        this.SHAPES.put(state, shape);
        return shape;
    }

    @Override
    public @NotNull VoxelShape getShape(final BlockState state, final BlockGetter reader,
            final BlockPos pos, final CollisionContext context) {
        return this.getShape(state);
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(final BlockState state, final BlockGetter reader,
            final BlockPos pos) {
        return this.getShape(state);
    }

    @Override
    public @NotNull InteractionResult use(final BlockState state, final Level world,
            final BlockPos pos, final Player playerEntity, final InteractionHand hand,
            final BlockHitResult result) {
        if (!world.isClientSide()) {
            final BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof MenuProvider) {
                PacketHandler.getPlayChannel().sendToServer(new MessageSaveItemUpgradeBench(pos));
                tileEntity.setChanged();
            }
        }
        return InteractionResult.SUCCESS;

    }

    @Override
    public void onRemove(final BlockState state, final Level worldIn, final BlockPos pos,
            final BlockState newState, final boolean isMoving) {
        if (state.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
            Block.popResource(worldIn, pos,
                    ((UpgradeBenchTileEntity) worldIn.getBlockEntity(pos)).getInventory().get(0));
            Block.popResource(worldIn, pos,
                    ((UpgradeBenchTileEntity) worldIn.getBlockEntity(pos)).getInventory().get(1));
            worldIn.removeBlockEntity(pos);
        }
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new WorkbenchTileEntity(pos, state);
    }
}
