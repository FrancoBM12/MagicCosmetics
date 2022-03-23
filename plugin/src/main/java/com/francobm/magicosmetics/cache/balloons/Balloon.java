package com.francobm.magicosmetics.cache.balloons;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.Cosmetic;
import com.francobm.magicosmetics.cache.CosmeticType;
import com.francobm.magicosmetics.cache.RotationType;
import com.francobm.magicosmetics.cache.nms.v1_18_R1.PlayerBalloonHandler;
import com.francobm.magicosmetics.nms.balloon.PlayerBalloon;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.time.LocalDate;
import java.util.Calendar;

public class Balloon extends Cosmetic {
    private ArmorStand armorStand;
    private PufferFish leashed;
    private PlayerBalloon playerBalloon;
    private final double space;
    private final boolean rotation;
    private final RotationType rotationType;
    private final BalloonEngine balloonEngine;
    protected boolean floatLoop = true;
    protected double y = 0;
    protected double height = 0;
    protected boolean heightLoop = true;
    protected double rotate = -0.008;
    protected double rot = 0;
    protected boolean rotateLoop = true;

    public Balloon(String id, String name, ItemStack itemStack, int modelData, boolean colored, double space, CosmeticType cosmeticType, Color color, boolean rotation, RotationType rotationType, BalloonEngine balloonEngine) {
        super(id, name, itemStack, modelData, colored, cosmeticType, color);
        this.space = space;
        this.rotation = rotation;
        this.rotationType = rotationType;
        //this.armorStand = null;
        //this.leashed = null;
        this.balloonEngine = balloonEngine;
    }

    //public ArmorStand getArmorStand() { return armorStand; }

    public double getSpace() {
        return space;
    }

    //public Entity getLeashed() { return leashed; }

    public boolean isRotation() {
        return rotation;
    }

    public RotationType getRotationType() {
        return rotationType;
    }

    public BalloonEngine getBalloonEngine() {
        return balloonEngine;
    }

    @Override
    public void active(Player player) {
        if(player == null) {
            return;
        }
        if(armorStand == null || armorStand.isDead() || !armorStand.isValid() || leashed == null || leashed.isDead() || !leashed.isValid()){
            if(player.isDead()) return;
            if(player.getGameMode() == GameMode.SPECTATOR) return;
            clear(player);
            leashed = player.getWorld().spawn(player.getLocation().clone().add(0, space + 1.0 + 1.3, 0).add(player.getLocation().clone().getDirection().normalize().multiply(-1)), PufferFish.class);
            armorStand = player.getWorld().spawn(leashed.getLocation().clone().subtract(0, 1.3, 0), ArmorStand.class);
            leashed.setCanPickupItems(false);
            armorStand.setRemoveWhenFarAway(false);
            leashed.setRemoveWhenFarAway(false);
            leashed.setAI(false);
            leashed.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false));
            leashed.setPuffState(-2);
            leashed.setGravity(false);
            leashed.setLeashHolder(null);
            leashed.setLeashHolder(player);
            MetadataValue metadataValue = new FixedMetadataValue(MagicCosmetics.getInstance(), "balloon");
            leashed.setMetadata("cosmetics", metadataValue);

            //leashed.addPassenger(armorStand);
            armorStand.setMetadata("cosmetics", metadataValue);
            armorStand.setArms(false);
            armorStand.setMarker(true);
            armorStand.setBasePlate(false);
            if(balloonEngine != null){
                setupModelEngine();
                balloonEngine.getModeledEntity().detectPlayers();
                balloonEngine.getModeledEntity().setInvisible(true);
                balloonEngine.getActiveModel().setDamageTint(false);
                if(isColored()) {
                    for(String id : balloonEngine.getActiveModel().getBlueprint().getAllBoneIds()) {
                        if(balloonEngine.getColorableParts() != null && !balloonEngine.getColorableParts().isEmpty()) {
                            if (!balloonEngine.getColorableParts().contains(id)) continue;
                        }
                        balloonEngine.getActiveModel().setTint(getColor(), id, true);
                    }
                }
            }else {
                armorStand.getEquipment().setHelmet(getItemColor(player));
            }
        }
        leashed.setCollidable(false);
        leashed.setInvisible(true);
        leashed.setInvulnerable(true);
        leashed.setSilent(true);
        armorStand.setCollidable(false);
        armorStand.setVisible(false);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        //
        if(rotation){
            int rot = MagicCosmetics.getInstance().getConfig().getInt("balloons-rotation");
            switch (rotationType){
                case RIGHT:
                    armorStand.setHeadPose(armorStand.getHeadPose().add(0,rot,0));
                    break;
                case UP:
                    armorStand.setHeadPose(armorStand.getHeadPose().add(rot, 0,0));
                    break;
                case ALL:
                    armorStand.setHeadPose(armorStand.getHeadPose().add(rot,rot,0));
                    break;
            }
        }
        update(player);
        //armorStand.teleport(leashed.getLocation().clone().subtract(0, 1.2, 0));
        if(balloonEngine != null){
            balloonEngine.getModeledEntity().tick();
        }
        //armorStand.teleport(behind);
        /*if(playerBalloon == null){
            if(player.isDead()) return;
            if(player.getGameMode() == GameMode.SPECTATOR) return;

            clear(player);
            playerBalloon = MagicCosmetics.getInstance().getVersion().createPlayerBalloon(player, space);
            playerBalloon.spawnBag(true, true);
        }
        playerBalloon.setItemOnHelmet(getItemColor(), true);
        playerBalloon.update(true);*/
    }

    @Override
    public void clear(Player player) {
        if(armorStand != null){
            armorStand.remove();
        }
        if(leashed != null){
            leashed.setLeashHolder(null);
            leashed.remove();
        }
        /*if(playerBalloon != null){
            playerBalloon.remove(true);
            playerBalloon = null;
        }*/
        if(balloonEngine != null){
            balloonEngine.remove();
        }
        leashed = null;
        armorStand = null;
    }

    private final double THRESHOLD_DISTANCE = 1;
    private final double SQUARED_WALKING = 12.5;
    private final double SQUARED_RUN = 12.4;
    private final double SQUARED_DISTANCE = 20;

    private final double CATCH_UP_INCREMENTS = .27; //.25
    private double CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS; //.25
    private final double CLOSEST_DIST_TO_PLAYER = 1.5;

    public void update(Player player){
        if(armorStand == null) return;
        if(leashed == null) return;
        Location playerLoc = player.getLocation().clone().add(0, space + 1.0 + 1.3, 0);
        Location stand = leashed.getLocation();
        Vector standDir = player.getEyeLocation().clone().subtract(stand).toVector();
        if(player.getLocation().distanceSquared(stand) > SQUARED_WALKING){
            Vector lineBetween = playerLoc.clone().subtract(stand).toVector();
            if (!standDir.equals(new Vector())) {
                standDir.normalize();
            }
            Vector distVec = lineBetween.clone().normalize().multiply(CATCH_UP_INCREMENTS_DISTANCE);
            Location standTo = stand.clone().setDirection(standDir.setY(0)).add(distVec.clone());

            Location normal = standTo.clone().setDirection(standTo.getDirection().multiply(-0.01));
            leashed.teleport(normal);
            armorStand.teleport(standTo.clone().subtract(0, 1.3, 0));
        }else {
            if (!standDir.equals(new Vector())) {
                standDir.normalize();
            }
            Location standToLoc = stand.clone().setDirection(standDir.setY(0));
            if (!floatLoop) {
                y += 0.01;
                standToLoc.add(0, 0.01, 0);
                //standToLoc.setYaw(standToLoc.getYaw() - 3F);
                if (y > 0.10) {
                    floatLoop = true;
                }
            } else {
                y -= 0.01;
                standToLoc.subtract(0, 0.01, 0);
                //standToLoc.setYaw(standToLoc.getYaw() + 3F);
                if (y < (-0.11 + 0)) {
                    floatLoop = false;
                    rotate *= -1;
                }
            }

            if (!rotateLoop) {
                rot += 0.01;
                armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0, rotate).subtract(0.008, 0, 0));
                if (rot > 0.20) {
                    rotateLoop = true;
                }
            } else {
                rot -= 0.01;
                armorStand.setHeadPose(armorStand.getHeadPose().add(0.008, 0, rotate));//.subtract(0.006, 0, 0));
                if (rot < -0.20) {
                    rotateLoop = false;
                }
            }
            Location loc = standToLoc.clone().setDirection(standToLoc.getDirection().multiply(-0.01));
            leashed.teleport(loc);
            armorStand.teleport(standToLoc.clone().subtract(0, 1.3, 0));
        }

        if(player.getLocation().distanceSquared(stand) > SQUARED_RUN){
            if(!heightLoop){
                height += 0.01;
                armorStand.setHeadPose(armorStand.getHeadPose().subtract(0.022, 0, 0));
                if(height > 0.10) heightLoop = true;
            }
        }else{
            if (heightLoop) {
                height -= 0.01;
                armorStand.setHeadPose(armorStand.getHeadPose().add(0.022, 0, 0));
                if (height < (-0.10 + 0)) heightLoop = false;
                return;
            }

        }
        if(player.getLocation().distanceSquared(stand) > SQUARED_DISTANCE){
            CATCH_UP_INCREMENTS_DISTANCE += 0.01;
        }else{
            CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS;
        }
        //MagicCosmetics.getInstance().getLogger().info("Distance squared: " + player.getLocation().distanceSquared(stand));

        /*
        Location leashedLoc = leashed.getLocation();
        Location p = player.getLocation().getBlock().getLocation().clone();
        Location as = armorStand.getLocation().getBlock().getLocation().clone();
        p.setY(0);
        as.setY(0);
        ////location.add(player.getLocation().clone().add(0, 1, 0));
        //location.setX(player.getLocation().getX());
        //location.setZ(player.getLocation().getZ());
        leashedLoc.setX(player.getLocation().getX());
        leashedLoc.setZ(player.getLocation().getZ());
        //location.setDirection(player.getLocation().getDirection());
        if(balloonEngine != null){
            leashedLoc.setDirection(player.getLocation().getDirection());
            //location.setY(player.getLocation().getY() + space + 1.0);
            leashedLoc.setY(player.getLocation().getY() + space + 1.0 + 1.3);
            balloonEngine.setIddle();
            leashed.teleport(leashedLoc);
            armorStand.teleport(leashed.getLocation().clone().subtract(0, 1.2, 0));
            return;
        }
        if(!p.equals(as)) {
            leashedLoc.setDirection(player.getLocation().getDirection());
            //location.setY(player.getLocation().getY() + space + 1.0);
            leashedLoc.setY(player.getLocation().getY() + space + 1.0 + 1.3);
            if(!heightLoop){
                height += 0.01;
                armorStand.setHeadPose(armorStand.getHeadPose().subtract(0.01, 0, 0));
                if(height > 0.10) heightLoop = true;
            }
        }else {
            if (heightLoop) {
                height -= 0.01;
                armorStand.setHeadPose(armorStand.getHeadPose().add(0.01, 0, 0));
                if (height < (-0.10 + 0)) heightLoop = false;
            }
            if (!floatLoop) {
                y += 0.01;
                leashedLoc.add(0, 0.01, 0);
                //location.add(0, 0.01, 0);
                //location.setYaw(location.getYaw() + 7.5f);
                //leashedLoc.setYaw(leashedLoc.getYaw() - 3f);
                //armorStand.teleport(location);
                if (y > 0.10) floatLoop = true;
            } else {
                y -= 0.01;
                leashedLoc.subtract(0, 0.01, 0);
                //leashedLoc.setYaw(leashedLoc.getYaw() + 3f);
                //location.subtract(0, 0.01, 0);
                //location.setYaw(location.getYaw() - 7.5f);
                //armorStand.teleport(location);
                if (y < (-0.10 + 0)) floatLoop = false;
            }
            rotate *= -1;
        }
        leashed.teleport(leashedLoc);
        armorStand.teleport(leashed.getLocation().clone().subtract(0, 1.2, 0));

         */
    }

    public void setupModelEngine(){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        balloonEngine.setActiveModel(plugin.getModelEngine().createActiveModel(balloonEngine.getModelId()));
        balloonEngine.setModeledEntity(plugin.getModelEngine().createModeledEntity(armorStand));
        balloonEngine.getModeledEntity().addActiveModel(balloonEngine.getActiveModel());
    }
}
