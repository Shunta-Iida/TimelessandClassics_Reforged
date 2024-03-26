package com.tac.guns.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tac.guns.tileentity.WorkbenchTileEntity;
import com.tac.guns.util.VoxelShapeHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraftforge.network.NetworkHooks;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class WorkbenchBlock extends RotatedObjectBlock implements EntityBlock {
    private final Map<BlockState, VoxelShape> SHAPES = new HashMap<>();

    public WorkbenchBlock(final Block.Properties properties) {
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
    public VoxelShape getShape(final BlockState state, final BlockGetter reader, final BlockPos pos,
            final CollisionContext context) {
        return this.getShape(state);
    }

    @Override
    public VoxelShape getOcclusionShape(final BlockState state, final BlockGetter reader, final BlockPos pos) {
        return this.getShape(state);
    }

    @Override
    public InteractionResult use(final BlockState state, final Level world, final BlockPos pos, final Player playerEntity,
            final InteractionHand hand, final BlockHitResult result) {
        if (!world.isClientSide()) {
            final BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof MenuProvider) {
                NetworkHooks.openGui((ServerPlayer) playerEntity, (MenuProvider) tileEntity, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockPos p_153215_, final BlockState p_153216_) {
        return new WorkbenchTileEntity(p_153215_, p_153216_);
    }
}
