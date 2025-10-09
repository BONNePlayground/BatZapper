//
// Created by BONNe
// Copyright - 2025
//


package lv.id.bonne.batzapper.mixin;


import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Set;

import lv.id.bonne.batzapper.BatZapper;
import lv.id.bonne.batzapper.registries.BatZapperBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;


@Mixin(Bat.class)
public abstract class BatMixin extends Mob
{
    protected BatMixin(EntityType<? extends Bat> entityType, Level level)
    {
        super(entityType, level);
    }


    @Shadow
    public abstract boolean isResting();


    @Shadow
    public abstract void setResting(boolean bl);


    @Shadow
    public abstract boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount);


    /**
     * Periodically searches for the nearest bat zapper block and sets it as a navigation target.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void batZapper$injectTargetFinder(CallbackInfo ci)
    {
        Bat bat = (Bat) (Object) this;

        if (bat.level().isClientSide())
        {
            return; // Only run server-side
        }

        if (bat.tickCount % 100 != 0)
        {
            return; // Run every 100 ticks for performance
        }

        // Skip if bat is already navigating
        PathNavigation nav = this.getNavigation();
        if (nav.isInProgress() && !nav.isStuck())
        {
            return;
        }

        // Find and assign nearest zapper
        this.batZapper$findNearestZapper(bat.level());
    }


    /**
     * Custom AI behavior: makes bats fly toward either nearby players holding zappers or the nearest zapper block.
     */
    @Inject(
        method = "customServerAiStep",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ambient/AmbientCreature;customServerAiStep(Lnet/minecraft/server/level/ServerLevel;)V",
            shift = At.Shift.AFTER),
        cancellable = true
    )
    private void batZapper$customServerAI(ServerLevel serverLevel, CallbackInfo ci)
    {
        Bat bat = (Bat) (Object) this;
        Level level = bat.level();

        // Find nearest player holding a zapper
        TargetingConditions conditions =
            TargetingConditions.forNonCombat().range(12).selector(
                (livingEntity, pLevel) -> {
                    if (!(livingEntity instanceof ServerPlayer player) || !player.hasLineOfSight(bat))
                    {
                        return false;
                    }

                    return player.getMainHandItem().is(BatZapperBlockRegistry.BAT_ZAPPER.get().asItem()) ||
                        player.getOffhandItem().is(BatZapperBlockRegistry.BAT_ZAPPER.get().asItem());
                });

        Player nearestPlayer = serverLevel.getNearestPlayer(conditions, bat);

        if (nearestPlayer != null)
        {
            // Stop navigation. Run to player
            this.getNavigation().stop();
        }

        if (bat.horizontalCollision && !bat.verticalCollision)
        {
            // stuck against wall... move up
            Vec3 vel = bat.getDeltaMovement().add(0, 0.15, 0);
            bat.setDeltaMovement(vel);
        }

        if (this.getNavigation().isInProgress())
        {
            Path path = this.getNavigation().getPath();

            if (path != null && !path.isDone())
            {
                Vec3 nextPos = path.getNextEntityPos(bat);

                if (nextPos != null)
                {
                    double dy = nextPos.y - bat.getY();

                    // Movement upwards, because the fall down
                    if (dy > 0)
                    {
                        bat.addDeltaMovement(new Vec3(0, dy + 0.15, 0));
                    }
                }
            }

            ci.cancel();
            return;
        }

        Vec3 target = (nearestPlayer != null) ? nearestPlayer.position() : this.batZapper$targetPosition;

        if (target == null)
        {
            return; // Nothing to do
        }

        // Wake up bat if it's resting
        if (this.isResting())
        {
            this.setResting(false);

            if (!bat.isSilent())
            {
                level.levelEvent(null, LevelEvent.SOUND_BAT_LIFTOFF, bat.blockPosition(), 0);
            }
        }

        // Smooth movement toward target
        Vec3 delta = target.subtract(bat.position());
        Vec3 movement = bat.getDeltaMovement();

        Vec3 newMovement = movement.add(
            (Math.signum(delta.x) * 0.5 - movement.x) * 0.1,
            (Math.signum(delta.y + 0.1) * 0.7 - movement.y) * 0.1,
            (Math.signum(delta.z) * 0.5 - movement.z) * 0.1
        );

        bat.setDeltaMovement(newMovement);

        // Rotate smoothly toward direction of motion
        float yaw = (float) (Mth.atan2(newMovement.z, newMovement.x) * 180F / Math.PI) - 90F;
        bat.setYRot(bat.getYRot() + Mth.wrapDegrees(yaw - bat.getYRot()));
        bat.zza = 0.5F;

        // Damage bat on contact
        boolean closeToPlayer = nearestPlayer != null && nearestPlayer.distanceTo(bat) < 1;
        boolean closeToTarget = this.batZapper$targetPosition != null &&
            this.batZapper$targetPosition.distanceTo(bat.position()) < 1;

        if (closeToPlayer || closeToTarget)
        {
            this.hurtServer(serverLevel, bat.damageSources().magic(), 3f);
        }

        ci.cancel(); // Cancel vanilla AI
    }


    /**
     * Searches for the closest bat zapper within configured range.
     */
    @Unique
    private void batZapper$findNearestZapper(Level level)
    {
        Bat bat = (Bat) (Object) this;
        BlockPos origin = bat.blockPosition();
        int range = BatZapper.config().getZapperOperationRange();

        BlockPos bestPos = null;
        double bestDist = Double.MAX_VALUE;

        for (BlockPos pos : BlockPos.betweenClosed(
            origin.offset(-range, -range, -range),
            origin.offset(range, range, range)))
        {
            if (level.isOutsideBuildHeight(pos))
            {
                continue;
            }

            BlockState state = level.getBlockState(pos);

            if (!state.is(BatZapperBlockRegistry.BAT_ZAPPER))
            {
                continue;
            }

            double distSq = origin.distSqr(pos);

            if (distSq < bestDist)
            {
                bestDist = distSq;
                bestPos = pos.immutable();
            }
        }

        if (bestPos == null)
        {
            this.batZapper$targetPosition = null;
            return;
        }

        double distance = bat.distanceToSqr(bestPos.getCenter());

        if (distance < 64)
        {
            range = 0;
        }
        else if (distance < 144)
        {
            range = 4;
        }
        else if (distance < 400)
        {
            range = 10;
        }

        PathNavigation nav = this.getNavigation();
        Path path = nav.createPath(Set.of(bestPos.west(), bestPos.east(), bestPos.north(), bestPos.south()), range);

//        if (path != null)
//        {
//            Minecraft.getInstance().debugRenderer.pathfindingRenderer.addPath(bat.getId(), path, nav.getMaxDistanceToWaypoint());
//        }

        if (path != null && path.canReach())
        {
            nav.moveTo(path, 0.7);
            this.batZapper$targetPosition = bestPos.getCenter();
        }
        else
        {
            nav.stop();
            this.batZapper$targetPosition = null;
        }
    }


    @Override
    @NotNull
    @Intrinsic(displace = false)
    protected PathNavigation createNavigation(@NotNull Level level)
    {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level)
        {

        };
        navigation.setCanFloat(true);
        navigation.canCutCorner(PathType.WALKABLE);
        navigation.setMaxVisitedNodesMultiplier(0.1f);
        return navigation;
    }


    /**
     * The target position for bat zapper.
     */
    @Unique
    private Vec3 batZapper$targetPosition = null;
}