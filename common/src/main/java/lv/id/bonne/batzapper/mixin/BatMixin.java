//
// Created by BONNe
// Copyright - 2025
//


package lv.id.bonne.batzapper.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import lv.id.bonne.batzapper.registries.BatZapperBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;


@Mixin(Bat.class)
public abstract class BatMixin
{
    @Shadow
    public abstract boolean isResting();


    @Shadow
    public abstract void setResting(boolean bl);


    @Shadow
    public abstract boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float f);


    /**
     * This code injects bat zapper block finder in the world.
     * @param ci
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void injectTargetFinder(CallbackInfo ci)
    {
        Bat bat = (Bat) (Object) this;

        if (bat.level().isClientSide) return;

        // Only process every few ticks for performance
        if (bat.tickCount % 10 != 0) return;

        if (this.batZapper$targetPosition != null &&
            bat.level().getBlockState(this.batZapper$targetPosition).
                is(BatZapperBlockRegistry.BAT_ZAPPER))
        {
            // If bat already found block, fly to it. Do not search blocks again.
            return;
        }

        // Find the closest block cage.
        this.batZapper$targetPosition = this.batZapper$findNearestCage(bat);
    }


    /**
     * This method injects custom AI logic for bats that tries to fly towards bat zapper.
     */
    @Inject(method = "customServerAiStep", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/entity/ambient/AmbientCreature;customServerAiStep(Lnet/minecraft/server/level/ServerLevel;)V",
        shift = At.Shift.AFTER),
        cancellable = true)
    private void injectCustomAIStep(ServerLevel serverLevel, CallbackInfo ci)
    {
        Bat bat = (Bat) (Object) this;

        Player nearestPlayer = serverLevel.getNearestPlayer(BAT_ZAPPER_TARGETING, bat);
        BlockPos targetPosition = this.batZapper$targetPosition;

        if (nearestPlayer != null)
        {
            // fly to player
            targetPosition = nearestPlayer.blockPosition();
        }

        if (targetPosition == null)
        {
            // Position is not found. Continue with vanilla behaviour.
            return;
        }

        if (this.isResting())
        {
            // Remove from resting
            this.setResting(false);

            if (!bat.isSilent())
            {
                // Trigger sound.
                serverLevel.levelEvent(null, 1025, bat.blockPosition(), 0);
            }
        }

        // Now calculate where to fly.

        double deltaX = targetPosition.getX() + 0.5 - bat.getX();
        double deltaY = targetPosition.getY() + 0.1 - bat.getY();
        double deltaZ = targetPosition.getZ() + 0.5 - bat.getZ();
        Vec3 initialMovement = bat.getDeltaMovement();

        Vec3 newMovement = initialMovement.add((Math.signum(deltaX) * (double)0.5F - initialMovement.x) * (double)0.1F,
            (Math.signum(deltaY) * (double)0.7F - initialMovement.y) * (double)0.1F,
            (Math.signum(deltaZ) * (double)0.5F - initialMovement.z) * (double)0.1F);

        bat.setDeltaMovement(newMovement);

        float rotation = (float)(Mth.atan2(newMovement.z, newMovement.x) * (double)(180F / (float)Math.PI)) - 90.0F;
        float bodyRotation = Mth.wrapDegrees(rotation - bat.getYRot());
        bat.zza = 0.5F;
        bat.setYRot(bat.getYRot() + bodyRotation);

        if (nearestPlayer != null && nearestPlayer.distanceTo(bat) < 1)
        {
            this.hurtServer(serverLevel, bat.damageSources().magic(), 3f);
        }

        // Prevent to execute vanilla code.
        ci.cancel();
    }


    @Unique
    private BlockPos batZapper$findNearestCage(Bat bat)
    {
        BlockPos batPos = bat.blockPosition();
        int range = 12;

        for (int r = 1; r <= range; r++)
        {
            for (int x = -r; x <= r; x++)
            {
                for (int y = -r; y <= r; y++)
                {
                    for (int z = -r; z <= r; z++)
                    {
                        if (Math.max(Math.abs(x), Math.max(Math.abs(y), Math.abs(z))) != r) continue;

                        BlockPos checkPos = batPos.offset(x, y, z);

                        if (bat.level().isOutsideBuildHeight(checkPos)) continue;

                        BlockState blockState = bat.level().getBlockState(checkPos);

                        if (blockState.is(BatZapperBlockRegistry.BAT_ZAPPER))
                        {
                            return checkPos;
                        }
                    }
                }
            }
        }

        // no zappers in range.
        return null;
    }


    /**
     * The target position for bat zapper.
     */
    @Unique
    private BlockPos batZapper$targetPosition = null;

    /**
     * The targeting logic for player with zapper.
     */
    @Unique
    private final static TargetingConditions BAT_ZAPPER_TARGETING = TargetingConditions.forNonCombat().range(12).selector(
        (livingEntity, serverLevel) -> {
            if (!(livingEntity instanceof ServerPlayer player))
            {
                return false;
            }

            return player.getMainHandItem().is(BatZapperBlockRegistry.BAT_ZAPPER.get().asItem()) ||
                player.getOffhandItem().is(BatZapperBlockRegistry.BAT_ZAPPER.get().asItem());
        });
}