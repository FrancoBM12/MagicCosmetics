package com.francobm.magicosmetics.cache.cosmetics.balloons;

import com.francobm.magicosmetics.utils.OffsetModel;
import com.francobm.magicosmetics.utils.PositionModelType;
import com.ticxo.modelengine.api.entity.BukkitEntity;
import com.ticxo.modelengine.api.model.IModel;
import com.ticxo.modelengine.api.nms.entity.impl.EmptyLookController;
import com.ticxo.modelengine.api.nms.entity.impl.ManualRangeManager;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.nms.entity.wrapper.RangeManager;
import com.ticxo.modelengine.api.utils.math.Offset;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class FakeBalloon extends BukkitEntity {
    private RangeManager rangeManager;
    private final Vector offset;

    private final double yaw;

    private final double pitch;

    private final PositionModelType positionModelType;

    private BodyRotationController controller;

    public FakeBalloon(LivingEntity entity, OffsetModel offsetModel, PositionModelType positionModelType) {
        super(entity);
        this.offset = offsetModel.getBukkitVector();
        this.yaw = Math.toRadians(offsetModel.getYaw());
        this.pitch = Math.toRadians(offsetModel.getPitch());
        this.positionModelType = positionModelType;
    }

    public BodyRotationController wrapBodyRotationControl() {
        this.controller = super.wrapBodyRotationControl();
        /*this.controller.setMaxHeadAngle(45.0F);
        this.controller.setMaxBodyAngle(45.0F);
        this.controller.setStableAngle(5.0F);*/
        return this.controller;
    }

    @Override
    public RangeManager wrapRangeManager(IModel model) {
        this.rangeManager = new ManualRangeManager(this, model);
        return rangeManager;
    }

    public Location getOriginalLocation(){
        return getOriginal().getLocation();
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
