package dev.pizzawunsch.advancedhorses.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TroughBlock extends Block {
    public static final int MAX_LEVEL = 4;
    public static final float HUNGER_PER_LEVEL = 25.0F;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, MAX_LEVEL);

    private static final VoxelShape SHAPE_X = Block.box(0.0, 0.0, 3.0, 16.0, 7.0, 13.0);
    private static final VoxelShape SHAPE_Z = Block.box(3.0, 0.0, 0.0, 13.0, 7.0, 16.0);

    public TroughBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LEVEL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.Z ? SHAPE_X : SHAPE_Z;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                          InteractionHand hand, BlockHitResult hitResult) {
        boolean hay = stack.is(Items.HAY_BLOCK);
        if (!hay && !stack.is(Items.WHEAT)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        int fill = state.getValue(LEVEL);
        int needed = hay ? 1 : 3;
        if (fill >= MAX_LEVEL || stack.getCount() < needed && !player.hasInfiniteMaterials()) {
            return InteractionResult.PASS;
        }
        if (level instanceof ServerLevel) {
            level.setBlock(pos, state.setValue(LEVEL, Math.min(MAX_LEVEL, fill + (hay ? 2 : 1))), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 1.0F, 0.9F);
            stack.consume(needed, player);
        }
        return InteractionResult.SUCCESS;
    }

    public static boolean eatFrom(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof TroughBlock) || state.getValue(LEVEL) <= 0) {
            return false;
        }
        level.setBlock(pos, state.setValue(LEVEL, state.getValue(LEVEL) - 1), Block.UPDATE_ALL);
        return true;
    }

    public static boolean hasFood(BlockState state) {
        return state.getBlock() instanceof TroughBlock && state.getValue(LEVEL) > 0;
    }
}
