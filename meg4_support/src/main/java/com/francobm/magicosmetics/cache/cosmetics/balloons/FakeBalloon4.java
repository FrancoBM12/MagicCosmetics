package com.francobm.magicosmetics.cache.cosmetics.balloons;

import com.francobm.magicosmetics.utils.Offset;
import com.francobm.magicosmetics.utils.OffsetModel;
import com.francobm.magicosmetics.utils.PositionModelType;
import com.ticxo.modelengine.api.entity.Dummy;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

public class FakeBalloon4 extends Dummy<LivingEntity> {
    private final Vector offset;

    private final double yaw;

    private final double pitch;

    private final PositionModelType positionModelType;

    public FakeBalloon4(LivingEntity entity, OffsetModel offsetModel, PositionModelType positionModelType) {
        super(entity);
        this.offset = offsetModel.getBukkitVector();
        this.yaw = Math.toRadians(offsetModel.getYaw());
        this.pitch = Math.toRadians(offsetModel.getPitch());
        this.positionModelType = positionModelType;
    }

    public void wrapBodyRotationControl() {
        this.bodyRotationController.setMaxHeadAngle(45.0F);
        this.bodyRotationController.setMaxBodyAngle(45.0F);
        this.bodyRotationController.setStableAngle(5.0F);
    }

    public Location getOriginalLocation(){
        return getOriginal().getLocation();
    }

    @Override
    public EntityHandler.InteractionResult interact(HumanEntity player, EquipmentSlot slot) {
        return EntityHandler.InteractionResult.SUCCESS;
    }

    public Location getLocation() {
        Vector offset;
        Location location = getOriginal().getLocation();
        if (this.positionModelType == PositionModelType.HEAD) {
            double pYaw = Math.toRadians(location.getYaw());
            double pPitch = Math.toRadians(location.getPitch());
            offset = Offset.rotateYaw(Offset.rotatePitch(this.offset.clone(), this.pitch + pPitch), this.yaw + pYaw);
        } else {
            double pYaw = Math.toRadians((this.bodyRotationController == null) ? getYBodyRot() : this.bodyRotationController.getYBodyRot());
            offset = Offset.rotateYaw(this.offset.clone(), this.yaw + pYaw);
        }
        return location.add(offset);
    }


}
