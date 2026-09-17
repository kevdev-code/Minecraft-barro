package io.github.kevdev_code.barro;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * An invisible seat. Minecraft has no way to sit on a block, so sitting means riding something: the chair
 * spawns one of these and mounts the player on it. It is never saved and disappears the moment it is empty,
 * so a reloaded world cannot end up full of leftover seats.
 */
public class AsientoEntity extends Entity {
    public AsientoEntity(EntityType<?> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    @Override
    public void tick() {
        if (level().isClientSide()) return;
        // Vanish once nobody is sitting, or if the furniture underneath is gone.
        if (getPassengers().isEmpty() || !(level().getBlockState(blockPosition()).getBlock() instanceof FurnitureBlock)) {
            ejectPassengers();
            discard();
        }
    }

    // The rider sits exactly where the seat was placed.
    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float scale) {
        return Vec3.ZERO;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
    }
}
