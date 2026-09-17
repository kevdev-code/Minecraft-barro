package io.github.kevdev_code.barro;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// Furniture that faces whoever places it. Shapes are authored facing north and turned for the other three.
public class FurnitureBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private final Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
    private final double seatHeight;

    public FurnitureBlock(VoxelShape northShape, double seatHeight, Properties properties) {
        super(properties);
        this.seatHeight = seatHeight;
        for (Direction facing : Direction.Plane.HORIZONTAL) shapes.put(facing, turn(northShape, facing));
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    // Right click with an empty hand to sit down. Sneak to get up, the way any vehicle works.
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (seatHeight <= 0 || player.isPassenger() || player.isShiftKeyDown()) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            if (!level.getEntitiesOfClass(AsientoEntity.class, new AABB(pos)).isEmpty())
                return InteractionResult.PASS;   // someone is already sitting here
            AsientoEntity seat = new AsientoEntity(BarroContent.ASIENTO.get(), level);
            seat.setPos(pos.getX() + 0.5, pos.getY() + seatHeight, pos.getZ() + 0.5);
            level.addFreshEntity(seat);
            player.startRiding(seat);
        }
        return InteractionResult.SUCCESS;
    }

    // Rotates a shape around the centre of the block, one quarter turn at a time.
    private static VoxelShape turn(VoxelShape shape, Direction facing) {
        VoxelShape turned = shape;
        for (int quarter = 0; quarter < (facing.get2DDataValue() - Direction.NORTH.get2DDataValue() + 4) % 4; quarter++) {
            VoxelShape previous = turned;
            VoxelShape[] built = {Shapes.empty()};
            previous.forAllBoxes((x1, y1, z1, x2, y2, z2) ->
                    built[0] = Shapes.or(built[0], Shapes.box(1 - z2, y1, x1, 1 - z1, y2, x2)));
            turned = built[0];
        }
        return turned;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes.get(state.getValue(FACING));
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
