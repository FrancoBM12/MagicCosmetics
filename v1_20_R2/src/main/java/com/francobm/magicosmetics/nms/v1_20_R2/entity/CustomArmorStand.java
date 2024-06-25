package com.francobm.magicosmetics.nms.v1_20_R2.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.level.World;

public class CustomArmorStand extends EntityArmorStand {
    public CustomArmorStand(World world) {
        super(EntityTypes.d, world);
    }

    @Override
    public float dh() {
        return -0.951f;
    }

    @Override
    public float k(Entity entity) {
        return -0.951f;
    }
}
