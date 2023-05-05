package com.francobm.magicosmetics.cache.cosmetics.balloons;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.cache.RotationType;
import com.francobm.magicosmetics.nms.Packets.v1_19_R3.VersionHandler;
import com.francobm.magicosmetics.nms.balloon.EntityBalloon;
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

public class Balloon extends Cosmetic {
    private ArmorStand armorStand;
    private PufferFish leashed;
    private PlayerBalloon playerBalloon;
    private EntityBalloon entityBalloon;
    private final double space;
    private final boolean rotation;
    private final RotationType rotationType;
    private final BalloonEngine balloonEngine;
    private final BalloonIA balloonIA;
    protected double distance;
    private final double SQUARED_WALKING;
    private final double SQUARED_DISTANCE;
    private int ticks;
    private Location lastPosition;
    private final boolean bigHead;
    private final boolean invisibleLeash;
    private final boolean instantFollow;

    public Balloon(String id, String name, ItemStack itemStack, int modelData, boolean colored, double space, CosmeticType cosmeticType, Color color, boolean rotation, RotationType rotationType, BalloonEngine balloonEngine, BalloonIA balloonIA, double distance, String permission, boolean texture, boolean bigHead, boolean hideMenu, boolean invisibleLeash, boolean useEmote, boolean instantFollow) {
        super(id, name, itemStack, modelData, colored, cosmeticType, color, permission, texture, hideMenu, useEmote);
        this.space = space;
        this.rotation = rotation;
        this.rotationType = rotationType;
        this.distance = distance;
        this.balloonEngine = balloonEngine;
        this.balloonIA = balloonIA;
        this.SQUARED_WALKING = 5.5 * space;
        this.SQUARED_DISTANCE = 10 * space;
        this.bigHead = bigHead;
        this.invisibleLeash = invisibleLeash;
        this.instantFollow = instantFollow;
    }

    public double getSpace() {
        return space;
    }

    public boolean isRotation() {
        return rotation;
    }

    public boolean isInstantFollow() {
        return instantFollow;
    }

    public RotationType getRotationType() {
        return rotationType;
    }

    public BalloonEngine getBalloonEngine() {
        return balloonEngine;
    }

    public void active(Entity entity){
        if(entity == null) {
            clear(null);
            return;
        }
        if(balloonIA != null) {
            if(invisibleLeash){
                if(balloonIA.getCustomEntity() == null) {
                    if(entity.isDead()) return;
                    clear(null);
                    armorStand = entity.getWorld().spawn(entity.getLocation().clone().add(0, space, 0).add(entity.getLocation().clone().getDirection().normalize().multiply(-1)), ArmorStand.class);
                    MetadataValue metadataValue = new FixedMetadataValue(MagicCosmetics.getInstance(), "balloon");
                    armorStand.setRemoveWhenFarAway(false);
                    armorStand.setMetadata("cosmetics", metadataValue);
                    armorStand.setArms(false);
                    armorStand.setMarker(true);
                    armorStand.setBasePlate(false);
                    balloonIA.spawn(armorStand.getLocation());
                }
                armorStand.setCollidable(false);
                armorStand.setVisible(false);
                armorStand.setInvisible(true);
                armorStand.setGravity(false);
                armorStand.setInvulnerable(true);
                update(entity);
                return;
            }
            if(balloonIA.getCustomEntity() == null || leashed == null || !leashed.isValid() || leashed.isDead()) {
                if(entity.isDead()) return;
                clear(null);
                leashed = entity.getWorld().spawn(entity.getLocation().clone().add(0, space, 0).add(entity.getLocation().clone().getDirection().multiply(-1)), PufferFish.class);
                balloonIA.spawn(leashed.getLocation().clone().subtract(0, 1.3, 0));
                leashed.setRemoveWhenFarAway(false);
                leashed.setAI(false);
                leashed.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false));
                leashed.setPuffState(-2);
                leashed.setGravity(false);
                leashed.setLeashHolder(entity);
                MetadataValue metadataValue = new FixedMetadataValue(MagicCosmetics.getInstance(), "balloon");
                leashed.setMetadata("cosmetics", metadataValue);
            }
            leashed.setCollidable(false);
            leashed.setInvisible(true);
            leashed.setInvulnerable(true);
            leashed.setSilent(true);
            update(entity);
            return;
        }
        if(balloonEngine != null){
            if(invisibleLeash) {
                if(balloonEngine.getModeledEntity().getBase() == null) {
                    if(entity.isDead()) return;
                    clear(null);
                    balloonEngine.spawnModel(entity.getLocation().clone().add(0, space, 0).add(entity.getLocation().clone().getDirection().normalize().multiply(-1)));
                    balloonEngine.detectPlayers(leashed, entity);
                    if (isColored()) {
                        balloonEngine.tintModel(getColor());
                    }
                }
                update(entity);
                return;
            }
            if(balloonEngine.getModeledEntity().getBase() == null || leashed == null){
                if(entity.isDead()) return;
                clear(null);
                leashed = ((VersionHandler)MagicCosmetics.getInstance().getVersion()).spawnFakePuffer(entity.getLocation().clone().add(0, space, 0).add(entity.getLocation().clone().getDirection().multiply(-1)));
                //((VersionHandler)MagicCosmetics.getInstance().getVersion()).spawnFakeEntity(entity, leashed);
                balloonEngine.spawnModel(leashed.getLocation().clone().subtract(0, 1.3, 0));
                balloonEngine.detectPlayers(leashed, entity);
                if (isColored()) {
                    balloonEngine.tintModel(getColor());
                }
            }
            update(entity);
            return;
        }
        if(entityBalloon == null){
            if(entity.isDead()) return;

            clear(null);
            entityBalloon = MagicCosmetics.getInstance().getVersion().createEntityBalloon(entity, space, distance, bigHead, invisibleLeash);
            entityBalloon.spawn(false);
        }
        if(entity instanceof Player){
            entityBalloon.setItem(getItemColor((Player) entity));
        }else {
            entityBalloon.setItem(getItemColor());
        }
        entityBalloon.rotate(rotation, rotationType, (float) MagicCosmetics.getInstance().balloonRotation);
        entityBalloon.update();
        entityBalloon.spawn(true);
    }

    @Override
    public void active(Player player) {
        if(isHideCosmetic()) {
            clear(player);
            return;
        }
        if(player == null) {
            return;
        }
        if(balloonIA != null) {
            if(invisibleLeash) {
                if(balloonIA.getCustomEntity() == null || armorStand == null) {
                    if(player.isDead()) return;
                    if(player.getGameMode() == GameMode.SPECTATOR) return;
                    clear(player);
                    armorStand = player.getWorld().spawn(player.getLocation().clone().add(0, space, 0).add(player.getLocation().clone().getDirection().normalize().multiply(-1)), ArmorStand.class);
                    balloonIA.spawn(armorStand.getLocation());
                    if (isColored()) {
                        balloonIA.paintBalloon(getColor());
                    }
                    MetadataValue metadataValue = new FixedMetadataValue(MagicCosmetics.getInstance(), "balloon");
                    armorStand.setRemoveWhenFarAway(false);
                    armorStand.setMetadata("cosmetics", metadataValue);
                    armorStand.setArms(false);
                    armorStand.setMarker(true);
                    armorStand.setBasePlate(false);
                }
                armorStand.setCollidable(false);
                armorStand.setVisible(false);
                armorStand.setInvisible(true);
                armorStand.setGravity(false);
                armorStand.setInvulnerable(true);
                update(player);
                return;
            }
            if(leashed == null || leashed.isDead() || !leashed.isValid()){
                if(player.isDead() || !player.isValid()) return;
                if(player.getGameMode() == GameMode.SPECTATOR) return;
                clear(player);
                leashed = player.getWorld().spawn(player.getLocation().clone().add(0, space, 0).add(player.getLocation().clone().getDirection().normalize().multiply(-1)), PufferFish.class);
                balloonIA.spawn(leashed.getLocation().clone().subtract(0, 1.3, 0));
                if (isColored()) {
                    balloonIA.paintBalloon(getColor());
                }
                leashed.setRemoveWhenFarAway(false);
                leashed.setAI(false);
                leashed.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false));
                leashed.setPuffState(-2);
                leashed.setGravity(false);
                leashed.setLeashHolder(player);
                MetadataValue metadataValue = new FixedMetadataValue(MagicCosmetics.getInstance(), "balloon");
                leashed.setMetadata("cosmetics", metadataValue);
            }
            leashed.setCollidable(false);
            leashed.setInvisible(true);
            leashed.setInvulnerable(true);
            leashed.setSilent(true);
            update(player);
            return;
        }
        if(balloonEngine != null){
            if(invisibleLeash) {
                if(balloonEngine.getModeledEntity() == null) {
                    if(player.isDead()) return;
                    clear(null);

                    balloonEngine.spawnModel(player.getLocation().clone().add(0, space, 0).add(player.getLocation().clone().getDirection().normalize().multiply(-1)));
                    balloonEngine.detectPlayers(leashed, player);
                    if (isColored()) {
                        balloonEngine.tintModel(getColor());
                    }
                }
                balloonEngine.detectPlayers(leashed, player);
                update(player);
                return;
            }
            if(balloonEngine.getModeledEntity() == null || leashed == null){
                if(player.isDead()) return;
                clear(null);
                leashed = balloonEngine.spawnLeash(player.getLocation().clone().add(0, space, 0).add(player.getLocation().clone().getDirection().multiply(-1)));
                balloonEngine.attachPufferFish(player, leashed);
                balloonEngine.spawnModel(leashed.getLocation().clone().subtract(0, 1.3, 0));
                balloonEngine.detectPlayers(leashed, player);
                if (isColored()) {
                    balloonEngine.tintModel(getColor());
                }
            }
            update(player);
            balloonEngine.detectPlayers(leashed, player);
            return;
        }
        if(playerBalloon == null){
            if(player.isDead()) return;
            if(player.getGameMode() == GameMode.SPECTATOR) return;

            clear(player);
            playerBalloon = MagicCosmetics.getInstance().getVersion().createPlayerBalloon(player, space, distance, bigHead, invisibleLeash);
            playerBalloon.spawn(false);
        }
        playerBalloon.setItem(getItemColor(player));
        playerBalloon.rotate(rotation, rotationType, (float) MagicCosmetics.getInstance().balloonRotation);
        playerBalloon.update(instantFollow);
        playerBalloon.spawn(true);
    }

    public void clearClosed(){
        if(balloonIA != null){
            balloonIA.remove();
            return;
        }
        if(balloonEngine != null){
            balloonEngine.remove(leashed);
            leashed = null;
            armorStand = null;
        }
        if(playerBalloon != null){
            playerBalloon.remove();
            playerBalloon = null;
        }
    }

    @Override
    public void clear(Player player) {
        if(balloonEngine != null){
            balloonEngine.remove(leashed);
        }
        if(balloonIA != null){
            balloonIA.remove();
        }
        if(armorStand != null){
            armorStand = null;
        }
        if(leashed != null){
            leashed = null;
        }
        if(playerBalloon != null){
            playerBalloon.remove();
            playerBalloon = null;
        }
        if(entityBalloon != null){
            entityBalloon.remove();
            entityBalloon = null;
        }
    }

    private final double CATCH_UP_INCREMENTS = .27; //.25
    private double CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS; //.25

    public void instantUpdate(Entity entity) {
        if(balloonIA != null){
            if(invisibleLeash) {
                if(armorStand == null) return;
                Location playerLoc = entity.getLocation().clone().add(0, space, 0);
                Location stand = armorStand.getLocation();
                Vector standDir;
                if(entity instanceof Player){
                    standDir = ((Player)entity).getEyeLocation().clone().subtract(stand).toVector();
                }else{
                    standDir = entity.getLocation().clone().subtract(stand).toVector();
                }
                Location distance2 = stand.clone();
                Location distance1 = entity.getLocation().clone();
                if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
                    Vector lineBetween = playerLoc.clone().subtract(stand).toVector();
                    if (!standDir.equals(new Vector())) {
                        standDir.normalize();
                    }
                    Vector distVec = lineBetween.clone().normalize().multiply(CATCH_UP_INCREMENTS_DISTANCE);
                    Location standTo = stand.clone().setDirection(standDir.setY(0)).add(distVec.clone());
                    Location normal = standTo.clone().setDirection(standTo.getDirection().multiply(0.01));
                    armorStand.teleport(normal);
                    balloonIA.getCustomEntity().teleport(normal.clone().subtract(0, 1.3, 0));
                    //balloonIA.getCustomEntity().teleport(standTo.clone().subtract(0, 1.3, 0));
                }else {
                    if (!standDir.equals(new Vector())) {
                        standDir.normalize();
                    }
                    Location standToLoc = stand.clone().setDirection(standDir.setY(0));
                    Location normal = standToLoc.clone().setDirection(standToLoc.getDirection().multiply(0.01));
                    armorStand.teleport(normal);
                    balloonIA.getCustomEntity().teleport(normal.clone().subtract(0, 1.3, 0));
                    //balloonIA.getCustomEntity().teleport(standToLoc.clone().subtract(0, 1.3, 0));
                }
                if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
                    balloonIA.setState(1);
                    ticks = 0;
                }else{
                    if(ticks >= 20) {
                        balloonIA.setState(0);
                        ticks = 0;
                    }
                    ticks++;
                }
                if(distance1.distanceSquared(distance2) > SQUARED_DISTANCE){
                    CATCH_UP_INCREMENTS_DISTANCE += 0.01;
                }else{
                    CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS;
                }
                return;
            }
            if(leashed == null) return;
            Location playerLoc = entity.getLocation().clone().add(0, space, 0);
            Location stand = leashed.getLocation();
            Vector standDir;
            if(entity instanceof Player){
                standDir = ((Player)entity).getEyeLocation().clone().subtract(stand).toVector();
            }else{
                standDir = entity.getLocation().clone().subtract(stand).toVector();
            }
            Location distance2 = stand.clone();
            Location distance1 = entity.getLocation().clone();
            if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
                Vector lineBetween = playerLoc.clone().subtract(stand).toVector();
                if (!standDir.equals(new Vector())) {
                    standDir.normalize();
                }
                Vector distVec = lineBetween.clone().normalize().multiply(CATCH_UP_INCREMENTS_DISTANCE);
                Location standTo = stand.clone().setDirection(standDir.setY(0)).add(distVec.clone());

                Location normal = standTo.clone().setDirection(standTo.getDirection().multiply(0.01));
                leashed.teleport(normal);
                balloonIA.getCustomEntity().teleport(standTo.clone().subtract(0, 1.3, 0));
            }else {
                if (!standDir.equals(new Vector())) {
                    standDir.normalize();
                }
                Location standToLoc = stand.clone().setDirection(standDir.setY(0));

                Location loc = standToLoc.clone().setDirection(standToLoc.getDirection().multiply(0.01));
                leashed.teleport(loc);
                balloonIA.getCustomEntity().teleport(standToLoc.clone().subtract(0, 1.3, 0));
            }
            if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
                balloonIA.setState(1);
                ticks = 0;
            }else{
                if(ticks >= 20) {
                    balloonIA.setState(0);
                    ticks = 0;
                }
                ticks++;
            }
            if(distance1.distanceSquared(distance2) > SQUARED_DISTANCE){
                CATCH_UP_INCREMENTS_DISTANCE += 0.01;
            }else{
                CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS;
            }
            return;
        }
        if(balloonEngine != null){
            if(invisibleLeash) {
                Location playerLoc = entity.getLocation().clone().add(0, space, 0);
                Location stand = balloonEngine.getMovementModel();
                Vector standDir;
                if(entity instanceof Player){
                    standDir = ((Player)entity).getEyeLocation().clone().subtract(stand).toVector();
                }else{
                    standDir = entity.getLocation().clone().subtract(stand).toVector();
                }

                if (!standDir.equals(new Vector())) {
                    standDir.normalize();
                }
                Location standToLoc = playerLoc.setDirection(standDir.setY(0));
                standToLoc = standToLoc.setDirection(standToLoc.getDirection().multiply(0.01));
                balloonEngine.movementModel(standToLoc);

                if(lastPosition == null)
                    lastPosition = entity.getLocation();

                if(!lastPosition.equals(entity.getLocation())){
                    balloonEngine.setState(1);
                    ticks = 0;
                }else{
                    if(ticks >= 20) {
                        balloonEngine.setState(0);
                        ticks = 0;
                        lastPosition = entity.getLocation();
                    }
                    ticks++;
                }
                return;
            }
            if(leashed == null) return;
            MagicCosmetics plugin = MagicCosmetics.getInstance();
            Location playerLoc = entity.getLocation().clone().add(0, space, 0);
            Location stand = balloonEngine.getMovementModel();
            Vector standDir;
            if(entity instanceof Player){
                standDir = ((Player)entity).getEyeLocation().clone().subtract(stand).toVector();
            }else{
                standDir = entity.getLocation().clone().subtract(stand).toVector();
            }
            if (!standDir.equals(new Vector())) {
                standDir.normalize();
            }
            Location standToLoc = playerLoc.setDirection(standDir.setY(0));
            standToLoc = standToLoc.setDirection(standToLoc.getDirection().multiply(-1));
            balloonEngine.updatePositionLeash(leashed, standToLoc);
            balloonEngine.teleportLeash(leashed);
            balloonEngine.movementModel(standToLoc);
            if(lastPosition == null)
                lastPosition = entity.getLocation();
            if(!lastPosition.equals(entity.getLocation())){
                balloonEngine.setState(1);
                ticks = 0;
            }else{
                if(ticks >= 20) {
                    balloonEngine.setState(0);
                    ticks = 0;
                    lastPosition = entity.getLocation();
                }
                ticks++;
            }
        }
    }

    public void update(Entity entity){
        if(instantFollow){
            instantUpdate(entity);
            return;
        }
        if(balloonIA != null){
            if(invisibleLeash) {
                if(armorStand == null) return;
                Location playerLoc = entity.getLocation().clone().add(0, space, 0);
                Location stand = armorStand.getLocation();
                Vector standDir;
                if(entity instanceof Player){
                    standDir = ((Player)entity).getEyeLocation().clone().subtract(stand).toVector();
                }else{
                    standDir = entity.getLocation().clone().subtract(stand).toVector();
                }
                Location distance2 = stand.clone();
                Location distance1 = entity.getLocation().clone();
                if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
                    Vector lineBetween = playerLoc.clone().subtract(stand).toVector();
                    if (!standDir.equals(new Vector())) {
                        standDir.normalize();
                    }
                    Vector distVec = lineBetween.clone().normalize().multiply(CATCH_UP_INCREMENTS_DISTANCE);
                    Location standTo = stand.clone().setDirection(standDir.setY(0)).add(distVec.clone());
                    Location normal = standTo.clone().setDirection(standTo.getDirection().multiply(0.01));
                    armorStand.teleport(normal);
                    balloonIA.getCustomEntity().teleport(normal.clone().subtract(0, 1.3, 0));
                    //balloonIA.getCustomEntity().teleport(standTo.clone().subtract(0, 1.3, 0));
                }else {
                    if (!standDir.equals(new Vector())) {
                        standDir.normalize();
                    }
                    Location standToLoc = stand.clone().setDirection(standDir.setY(0));
                    Location normal = standToLoc.clone().setDirection(standToLoc.getDirection().multiply(0.01));
                    armorStand.teleport(normal);
                    balloonIA.getCustomEntity().teleport(normal.clone().subtract(0, 1.3, 0));
                    //balloonIA.getCustomEntity().teleport(standToLoc.clone().subtract(0, 1.3, 0));
                }
                if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
                    balloonIA.setState(1);
                    ticks = 0;
                }else{
                    if(ticks >= 20) {
                        balloonIA.setState(0);
                        ticks = 0;
                    }
                    ticks++;
                }
                if(distance1.distanceSquared(distance2) > SQUARED_DISTANCE){
                    CATCH_UP_INCREMENTS_DISTANCE += 0.01;
                }else{
                    CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS;
                }
                return;
            }
            if(leashed == null) return;
            Location playerLoc = entity.getLocation().clone().add(0, space, 0);
            Location stand = leashed.getLocation();
            Vector standDir;
            if(entity instanceof Player){
                standDir = ((Player)entity).getEyeLocation().clone().subtract(stand).toVector();
            }else{
                standDir = entity.getLocation().clone().subtract(stand).toVector();
            }
            Location distance2 = stand.clone();
            Location distance1 = entity.getLocation().clone();
            if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
                Vector lineBetween = playerLoc.clone().subtract(stand).toVector();
                if (!standDir.equals(new Vector())) {
                    standDir.normalize();
                }
                Vector distVec = lineBetween.clone().normalize().multiply(CATCH_UP_INCREMENTS_DISTANCE);
                Location standTo = stand.clone().setDirection(standDir.setY(0)).add(distVec.clone());

                Location normal = standTo.clone().setDirection(standTo.getDirection().multiply(0.01));
                leashed.teleport(normal);
                balloonIA.getCustomEntity().teleport(standTo.clone().subtract(0, 1.3, 0));
            }else {
                if (!standDir.equals(new Vector())) {
                    standDir.normalize();
                }
                Location standToLoc = stand.clone().setDirection(standDir.setY(0));

                Location loc = standToLoc.clone().setDirection(standToLoc.getDirection().multiply(0.01));
                leashed.teleport(loc);
                balloonIA.getCustomEntity().teleport(standToLoc.clone().subtract(0, 1.3, 0));
            }
            if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
                balloonIA.setState(1);
                ticks = 0;
            }else{
                if(ticks >= 20) {
                    balloonIA.setState(0);
                    ticks = 0;
                }
                ticks++;
            }
            if(distance1.distanceSquared(distance2) > SQUARED_DISTANCE){
                CATCH_UP_INCREMENTS_DISTANCE += 0.01;
            }else{
                CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS;
            }
            return;
        }
        if(balloonEngine != null){
            MagicCosmetics plugin = MagicCosmetics.getInstance();
            if(invisibleLeash) {
                Location playerLoc = entity.getLocation().clone().add(0, space, 0);
                Location stand = balloonEngine.getMovementModel();
                Vector standDir;
                if(entity instanceof Player){
                    standDir = ((Player)entity).getEyeLocation().clone().subtract(stand).toVector();
                }else{
                    standDir = entity.getLocation().clone().subtract(stand).toVector();
                }
                Location distance2 = stand.clone();
                Location distance1 = entity.getLocation().clone();
                if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
                    Vector lineBetween = playerLoc.clone().subtract(stand).toVector();
                    if (!standDir.equals(new Vector())) {
                        standDir.normalize();
                    }
                    Vector distVec = lineBetween.clone().normalize().multiply(CATCH_UP_INCREMENTS_DISTANCE);
                    Location standTo = stand.clone().setDirection(standDir.setY(0)).add(distVec.clone());
                    Location normal = standTo.clone().setDirection(standTo.getDirection().multiply(0.01));
                    balloonEngine.movementModel(normal);
                }else {
                    if (!standDir.equals(new Vector())) {
                        standDir.normalize();
                    }
                    Location standToLoc = stand.clone().setDirection(standDir.setY(0));
                    Location normal = standToLoc.clone().setDirection(standToLoc.getDirection().multiply(0.01));
                    balloonEngine.movementModel(normal);
                }

                if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
                    balloonEngine.setState(1);
                    ticks = 0;
                }else{
                    if(ticks >= 20) {

                        balloonEngine.setState(0);
                        ticks = 0;
                    }
                    ticks++;
                }
                if(distance1.distanceSquared(distance2) > SQUARED_DISTANCE){
                    CATCH_UP_INCREMENTS_DISTANCE += 0.01;
                }else{
                    CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS;
                }
                return;
            }
            if(leashed == null) return;
            Location playerLoc = entity.getLocation().clone().add(0, space, 0);
            Location stand = leashed.getLocation();
            Vector standDir;
            if(entity instanceof Player){
                standDir = ((Player)entity).getEyeLocation().clone().subtract(stand).toVector();
            }else{
                standDir = entity.getLocation().clone().subtract(stand).toVector();
            }
            Location distance2 = stand.clone();
            Location distance1 = entity.getLocation().clone();
            if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
                Vector lineBetween = playerLoc.clone().subtract(stand).toVector();
                if (!standDir.equals(new Vector())) {
                    standDir.normalize();
                }
                //balloonEngine.setLook(playerLoc.getDirection().getX(), playerLoc.getDirection().getY(), playerLoc.getDirection().getZ());
                Vector distVec = lineBetween.clone().normalize().multiply(CATCH_UP_INCREMENTS_DISTANCE);
                Location standTo = stand.clone().setDirection(standDir.setY(0)).add(distVec.clone());
                Location normal = standTo.clone().setDirection(standTo.getDirection().multiply(0.01));

                leashed.teleport(normal);
                balloonEngine.updatePositionLeash(leashed, normal);
                balloonEngine.teleportLeash(leashed);
                balloonEngine.movementModel(normal);
                //livingEntity.teleport(standTo.clone().subtract(0, 1.3, 0));
            }else {
                if (!standDir.equals(new Vector())) {
                    standDir.normalize();
                }
                Location standToLoc = stand.clone().setDirection(standDir.setY(0));

                Location loc = standToLoc.clone().setDirection(standToLoc.getDirection().multiply(0.01));
                balloonEngine.updatePositionLeash(leashed, loc);
                balloonEngine.teleportLeash(leashed);
                balloonEngine.movementModel(loc);
                //livingEntity.teleport(standToLoc.clone().subtract(0, 1.3, 0));
            }

            if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
                balloonEngine.setState(1);
                ticks = 0;
            }else{
                if(ticks >= 20) {
                    balloonEngine.setState(0);
                    ticks = 0;
                }
                ticks++;
            }
            if(distance1.distanceSquared(distance2) > SQUARED_DISTANCE){
                CATCH_UP_INCREMENTS_DISTANCE += 0.01;
            }else{
                CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS;
            }
        }
    }

    public double getDistance() {
        return distance;
    }

    public BalloonIA getBalloonIA() {
        return balloonIA;
    }

    public boolean isBigHead() {
        return bigHead;
    }

    public boolean isInvisibleLeash() {
        return invisibleLeash;
    }

    public void setLeashedHolder(Entity entity) {
        if(leashed == null || !leashed.isValid() || leashed.isDead()) return;
        leashed.setLeashHolder(entity);
    }
}
