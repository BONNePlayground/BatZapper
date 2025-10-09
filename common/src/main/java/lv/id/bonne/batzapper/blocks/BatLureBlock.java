package lv.id.bonne.batzapper.blocks;


import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;

import lv.id.bonne.batzapper.BatZapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;


public class BatLureBlock extends Block
{
    public BatLureBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any());
    }


    @Override
    @NotNull
    protected MapCodec<? extends Block> codec()
    {
        return BatLureBlock.CODEC;
    }


    @Override
    protected boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos)
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
    protected void randomTick(BlockState blockState,
        ServerLevel serverLevel,
        BlockPos blockPos,
        RandomSource randomSource)
    {
        super.randomTick(blockState, serverLevel, blockPos, randomSource);

        Vec3 bottomCenter = blockPos.getCenter().add(0, 0.3, 0);

        for (int i = 20; i >= 0; i -= 5)
        {
            ShriekParticleOption shriekOption = new ShriekParticleOption(i);
            serverLevel.sendParticles(shriekOption,
                bottomCenter.x(),
                bottomCenter.y(),
                bottomCenter.z(),
                1,
                0,
                0,
                0,
                0.001);
        }

        if (randomSource.nextFloat() < BatZapper.config().getLureToSummonChance())
        {
            return;
        }

        int range = BatZapper.config().getLureOperationRange();

        // Find bats in 32 blocks on each side of lure
        int numberOfBats = serverLevel.getNearbyEntities(Bat.class,
                BATS_IN_RANGE,
                null,
                AABB.ofSize(blockPos.getCenter(), range * 2, range * 2, range * 2)).
            size();

        if (numberOfBats >= BatZapper.config().getLureBatLimit())
        {
            // I think 7 bats are enough. Isn't it? Well, now it is.
            return;
        }

        int summonedBats = BatZapper.config().getLureSummonsPerTry();

        // Try to find air block in 50 tries
        for (int tries = 0; tries < 50 * BatZapper.config().getLureSummonsPerTry() && summonedBats > 0; tries++)
        {
            int dx = randomSource.nextInt(range);
            int dy = randomSource.nextInt(range);
            int dz = randomSource.nextInt(range);

            BlockPos checkPos = blockPos.offset(dx, dy, dz);

            if (serverLevel.getBlockState(checkPos).isAir())
            {
                this.summonBat(checkPos, blockPos, serverLevel);
                summonedBats--;
            }
        }
    }


    private void summonBat(BlockPos batPos, BlockPos blockPos, ServerLevel serverLevel)
    {
        VibrationParticleOption vibration = new VibrationParticleOption(
            new BlockPositionSource(blockPos), 40);

        serverLevel.sendParticles(vibration,
            batPos.getCenter().x(),
            batPos.getCenter().y(),
            batPos.getCenter().z(),
            1,
            0,
            0,
            0,
            1);

        // Now create the nasty bat
        EntityType.BAT.create(serverLevel,
            serverLevel::addFreshEntity,
            batPos,
            MobSpawnType.MOB_SUMMONED,
            true,
            false);
    }


    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType)
    {
        return false;
    }


    public static final MapCodec<BatLureBlock> CODEC = simpleCodec(BatLureBlock::new);

    private final VoxelShape SHAPE = Shapes.or(
        Block.box(1, 0, 1, 3, 2, 3),
        Block.box(13, 0, 1, 15, 2, 3),
        Block.box(13, 0, 13, 15, 2, 15),
        Block.box(1, 0, 13, 3, 2, 15),
        Block.box(2, 2, 12, 4, 4, 14),
        Block.box(2, 2, 2, 4, 4, 4),
        Block.box(12, 2, 2, 14, 4, 4),
        Block.box(12, 2, 12, 14, 4, 14),
        Block.box(3, 4, 11, 5, 6, 13),
        Block.box(11, 4, 11, 13, 6, 13),
        Block.box(11, 4, 3, 13, 6, 5),
        Block.box(3, 4, 3, 5, 6, 5),
        Block.box(4, 6, 4, 12, 14, 12));

    private static final TargetingConditions BATS_IN_RANGE = TargetingConditions.forNonCombat();
}