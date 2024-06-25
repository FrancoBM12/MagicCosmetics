package com.francobm.magicosmetics.cache.cosmetics.backpacks;

import com.francobm.magicosmetics.utils.Offset;
import com.francobm.magicosmetics.utils.OffsetModel;
import com.francobm.magicosmetics.utils.PositionModelType;
import com.ticxo.modelengine.api.entity.BukkitEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class FakeBackpack4 extends BukkitEntity {
    private final Vector offset;

    private final double yaw;

    private final double pitch;

    private final PositionModelType positionModelType;

    private BodyRotationController controller;

    public FakeBackpack4(Entity entity, OffsetModel offsetModel, PositionModelType positionModelType) {
        super(entity);
        this.offset = offsetModel.getBukkitVector();
        this.yaw = Math.toRadians(offsetModel.getYaw());
        this.pitch = Math.toRadians(offsetModel.getPitch());
        this.positionModelType = positionModelType;
    }

    public void wrapBodyRotationControl() {
        this.controller = getBodyRotationController();
        this.controller.setMaxHeadAngle(45.0F);
        this.controller.setMaxBodyAngle(45.0F);
        this.controller.setStableAngle(5.0F);
    }

    public Location getLocation() {
        Vector offset;
        Location location = getOriginal().getLocation();
        if (this.positionModelType == PositionModelType.HEAD) {
            double pYaw = Math.toRadians(location.getYaw());
            double pPitch = Math.toRadians(location.getPitch());
            offset = Offset.rotateYaw(Offset.rotatePitch(this.offset.clone(), this.pitch + pPitch), this.yaw + pYaw);
        } else {
            double pYaw = Math.toRadians((this.controller == null) ? getYBodyRot() : this.controller.getYBodyRot());
            offset = Offset.rotateYaw(this.offset.clone(), this.yaw + pYaw);
        }
        return location.add(offset);
    }


}
