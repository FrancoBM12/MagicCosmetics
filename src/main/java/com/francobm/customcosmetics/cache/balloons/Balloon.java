package com.francobm.customcosmetics.cache.balloons;

import com.francobm.customcosmetics.CustomCosmetics;
import com.francobm.customcosmetics.cache.Cosmetic;
import com.francobm.customcosmetics.cache.CosmeticType;
import com.francobm.customcosmetics.cache.RotationType;
import com.francobm.customcosmetics.cache.nms.PlayerBalloon;
import com.francobm.customcosmetics.utils.MathUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class Balloon extends Cosmetic {
    private ArmorStand armorStand;
    private PufferFish leashed;
    private final double space;
    private final boolean rotation;
    private final RotationType rotationType;
    private BalloonEngine balloonEngine;
    private PlayerBalloon playerBalloon;

    public Balloon(String id, String name, ItemStack itemStack, int modelData, boolean colored, double space, CosmeticType cosmeticType, Color color, boolean rotation, RotationType rotationType, String modelId) {
        super(id, name, itemStack, modelData, colored, cosmeticType, color);
        this.space = space;
        this.rotation = rotation;
        this.rotationType = rotationType;
        this.armorStand = null;
        this.leashed = null;
        if(modelId.isEmpty()) return;
        if(CustomCosmetics.getInstance().isModelEngine()){
            balloonEngine = new BalloonEngine(modelId);
        }
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public double getSpace() {
        return space;
    }

    public PufferFish getLeashed() {
        return leashed;
    }

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
        if(playerBalloon == null){
            if(player.isDead()) return;
            clear(player);

            playerBalloon = PlayerBalloon.createBalloon(player, updatePosition(player));
            if(playerBalloon == null){
                CustomCosmetics.getInstance().getLogger().severe("Plugin not support this version!!");
                return;
            }
            playerBalloon.spawnBalloon(true);
        }
        playerBalloon.setItemOnHelmet(getItemColor(), true);
        float vec = (float) (player.getVelocity().getX() + player.getVelocity().getY() + player.getVelocity().getZ());
        playerBalloon.lookEntity(0, (float) (-vec/2.5), true);
        playerBalloon.teleport(updatePosition(player).add(0, vec/2, 0), true);

        /*if(armorStand == null || armorStand.isDead() || !armorStand.isValid() || leashed == null || leashed.isDead() || !leashed.isValid()){
            if(player.isDead()) return;
            clear(player);

            leashed = player.getWorld().spawn(updatePosition(player), PufferFish.class);
            armorStand = player.getWorld().spawn(leashed.getLocation().clone().subtract(0, 1.2, 0), ArmorStand.class);
            leashed.setCanPickupItems(false);
            leashed.setRemoveWhenFarAway(false);
            leashed.setAI(false);
            leashed.setPuffState(-1);
            leashed.setGravity(false);
            leashed.setLeashHolder(null);
            leashed.setLeashHolder(player);
            MetadataValue metadataValue = new FixedMetadataValue(CustomCosmetics.getInstance(), "balloon");
            leashed.setMetadata("cosmetics", metadataValue);

            leashed.addPassenger(armorStand);
            armorStand.setMetadata("cosmetics", metadataValue);
            if(balloonEngine != null){
                setupModelEngine();
                balloonEngine.getModeledEntity().detectPlayers();
                balloonEngine.getModeledEntity().setInvisible(true);
            }else {
                armorStand.setArms(false);
                armorStand.setMarker(true);
                armorStand.setBasePlate(false);
                armorStand.getEquipment().setHelmet(getItemColor());
                armorStand.setSwimming(true);
            }
        }

        try{
            Entity entity = leashed.getLeashHolder();
            if(entity instanceof Player){
                Player p = (Player) entity;
                if(!player.getUniqueId().equals(p.getUniqueId())){
                    clear(player);
                    return;
                }
            }
        }catch (IllegalStateException exception){
            clear(player);
            //CustomCosmetics.getInstance().getLogger().info("Entity is not leashed waiting to player...");
            return;
        }

        //
        leashed.setCollidable(false);
        leashed.setInvisible(true);
        leashed.setInvulnerable(true);
        leashed.setSilent(true);
        if(balloonEngine == null) {
            armorStand.setCollidable(false);
            armorStand.setVisible(false);
            armorStand.setInvisible(true);
            armorStand.setGravity(false);
            armorStand.setInvulnerable(true);
        }
        //
        float vec = (float) (player.getVelocity().getX() + player.getVelocity().getY() + player.getVelocity().getZ());
        armorStand.setHeadPose(new EulerAngle(-vec/2.5, 0, 0));

        leashed.teleport(updatePosition(player).add(0, vec/2, 0));
        if(rotation){
            int rot = CustomCosmetics.getInstance().getConfig().getInt("balloons-rotation");
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
        armorStand.teleport(leashed.getLocation().clone().subtract(0, 1.2, 0));
        if(balloonEngine != null){
            balloonEngine.getModeledEntity().tick();
        }*/
        //armorStand.teleport(behind);
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
        if(balloonEngine != null){
            balloonEngine.setActiveModel(null);
            balloonEngine.setModeledEntity(null);
        }
        if(playerBalloon != null){
            playerBalloon.remove(true);
        }
        playerBalloon = null;
        leashed = null;
        armorStand = null;
    }

    private boolean status = true;
    public Location updatePosition(Entity player){
        Vector direction = player.getLocation().getDirection().multiply(-1).setY(0);
        Location currentLoc = player.getLocation().clone().add(direction).add(0, space, 0);
        currentLoc.setPitch(-50f - MathUtils.random(0f, 5f));

        currentLoc.setYaw(player.getLocation().getYaw());
        //currentLoc.add(currentLoc.getDirection().multiply(-1.3D).getX(), 2.3D + (status ? 0.1D : 0D), currentLoc.getDirection().multiply(-1.8D).getZ());
        if(balloonEngine == null) {
            currentLoc.add(0, 2.3D + (status ? 0.1D : -0.1D), 0.0);
        }else{
            currentLoc.add(0, 2.0D, 0);
        }
        status = !status;
        return currentLoc;
    }

    public void setupModelEngine(){
        CustomCosmetics plugin = CustomCosmetics.getInstance();
        balloonEngine.setActiveModel(plugin.getModelEngine().createActiveModel(balloonEngine.getModelId()));
        balloonEngine.setModeledEntity(plugin.getModelEngine().createModeledEntity(armorStand));
        balloonEngine.getModeledEntity().addActiveModel(balloonEngine.getActiveModel());
    }
}
