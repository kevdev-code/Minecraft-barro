package io.github.kevdev_code.barro;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A strand of papel picado, like the ones strung across a street. FACING is the direction the paper looks at.
 * Placed against the side of a block it hangs flat on that wall (ATTACHED); anywhere else it hangs down the
 * middle of its block, so a row of them reads as one strand.
 */
public class PapelPicadoBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    // How far this segment hangs: the ends of a strand stay up and the middle drops, so a row sags like a real one.
    public static final IntegerProperty SAG = IntegerProperty.create("sag", 0, 3);

    private static final int MAX_SAG = 3;

    private static final VoxelShape CENTER_ALONG_X = Block.box(0, 2, 7, 16, 16, 9);
    private static final VoxelShape CENTER_ALONG_Z = Block.box(7, 2, 0, 9, 16, 16);
    private static final Map<Direction, VoxelShape> ON_WALL = Map.of(
            Direction.NORTH, Block.box(0, 2, 14, 16, 16, 16),
            Direction.SOUTH, Block.box(0, 2, 0, 16, 16, 2),
            Direction.WEST, Block.box(14, 2, 0, 16, 16, 16),
            Direction.EAST, Block.box(0, 2, 0, 2, 16, 16));

    public PapelPicadoBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ATTACHED, false).setValue(SAG, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ATTACHED, SAG);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        Direction face = context.getClickedFace();
        if (face.getAxis().isHorizontal()) {
            BlockPos wallPos = context.getClickedPos().relative(face.getOpposite());
            if (context.getLevel().getBlockState(wallPos).isFaceSturdy(context.getLevel(), wallPos, face))
                state = state.setValue(FACING, face).setValue(ATTACHED, true);
        }
        // Otherwise it hangs free, running left to right in front of whoever places it.
        return state.setValue(SAG, sagAt(context.getLevel(), context.getClickedPos(), state));
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        // Only a change along the strand can lengthen or shorten it.
        return direction.getAxis() == strandAxis(state) ? state.setValue(SAG, sagAt(level, pos, state)) : state;
    }

    // The strand runs across the face of the paper.
    private static Direction.Axis strandAxis(BlockState state) {
        return state.getValue(FACING).getAxis() == Direction.Axis.Z ? Direction.Axis.X : Direction.Axis.Z;
    }

    private static int sagAt(LevelReader level, BlockPos pos, BlockState state) {
        Direction.Axis axis = strandAxis(state);
        int toOneEnd = segmentsToward(level, pos, Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE), state);
        int toOtherEnd = segmentsToward(level, pos, Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE), state);
        return Math.min(MAX_SAG, Math.min(toOneEnd, toOtherEnd));
    }

    // Counts matching segments in one direction, stopping at MAX_SAG: further away cannot change this one's sag.
    private static int segmentsToward(LevelReader level, BlockPos pos, Direction direction, BlockState state) {
        for (int distance = 1; distance <= MAX_SAG; distance++) {
            BlockState other = level.getBlockState(pos.relative(direction, distance));
            if (other.getBlock() != state.getBlock()
                    || other.getValue(FACING) != state.getValue(FACING)
                    || other.getValue(ATTACHED) != state.getValue(ATTACHED))
                return distance - 1;
        }
        return MAX_SAG;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(ATTACHED)) return ON_WALL.get(state.getValue(FACING));
        return state.getValue(FACING).getAxis() == Direction.Axis.Z ? CENTER_ALONG_X : CENTER_ALONG_Z;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
