package lv.id.bonne.batzapper.blocks;


import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;


public class BatZapperBlock extends Block
{
    public BatZapperBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any());
    }


    @Override
    @NotNull
    protected MapCodec<? extends Block> codec()
    {
        return BatZapperBlock.CODEC;
    }


    @Override
    protected boolean propagatesSkylightDown(BlockState blockState)
    {
        return true;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 1.0F;
    }


    /**
     * This method returns the shape of current table.
     *
     * @param state The block state.
     * @param level The level where block is located.
     * @param pos The position of the block.
     * @param context The collision content.
     * @return The VoxelShape of current table.
     */
    @Override
    @NotNull
    public VoxelShape getShape(@NotNull BlockState state,
        @NotNull BlockGetter level,
        @NotNull BlockPos pos,
        @NotNull CollisionContext context)
    {
        return SHAPE;
    }


    @Override
    protected void entityInside(BlockState blockState,
        Level level,
        BlockPos blockPos,
        Entity entity,
        InsideBlockEffectApplier insideBlockEffectApplier)
    {
        super.entityInside(blockState, level, blockPos, entity, insideBlockEffectApplier);

        if (level instanceof ServerLevel serverLevel && entity instanceof Bat)
        {
            entity.hurtServer(serverLevel, serverLevel.damageSources().magic(), 3f);
        }
    }


    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType)
    {
        return false;
    }


    public static final MapCodec<BatZapperBlock> CODEC = simpleCodec(BatZapperBlock::new);

    private final VoxelShape SHAPE = Shapes.or(
        Block.box(4,0,6,5,13,10),
        Block.box(5,0,5,6,13,11),
        Block.box(6,0,4,10,13,12),
        Block.box(10,0,5,11,13,11),
        Block.box(11,0,6,12,13,10),
        Block.box(5,13,6,11,14,10),
        Block.box(6,13,5,10,14,6),
        Block.box(6,13,10,10,14,11));
}