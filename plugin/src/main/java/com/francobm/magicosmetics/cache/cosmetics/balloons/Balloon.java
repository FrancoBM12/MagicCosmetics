package com.francobm.magicosmetics.cache.cosmetics.balloons;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.cache.RotationType;
import com.francobm.magicosmetics.nms.balloon.EntityBalloon;
import com.francobm.magicosmetics.nms.balloon.PlayerBalloon;
import org.bukkit.Bukkit;
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
    private PufferFish pufferFish;
    private PlayerBalloon playerBalloon;
    private EntityBalloon entityBalloon;
    private final double space;
    private final boolean rotation;
    private final RotationType rotationType;
    private final BalloonEngine balloonEngine;
    private final BalloonIA balloonIA;
    protected boolean floatLoop = true;
    protected double y = 0;
    protected double height = 0;
    protected boolean heightLoop = true;
    protected double rotate = -0.008;
    protected double rot = 0;
    protected boolean rotateLoop = true;
    protected double distance;
    private final double SQUARED_WALKING;
    private final double SQUARED_DISTANCE;
    private int ticks;
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
                if(armorStand == null || armorStand.isDead() || !armorStand.isValid()) {
                    if(entity.isDead()) return;
                    clear(null);
                    armorStand = entity.getWorld().spawn(entity.getLocation().clone().add(0, space, 0).add(entity.getLocation().clone().getDirection().normalize().multiply(-1)), ArmorStand.class);
                    MetadataValue metadataValue = new FixedMetadataValue(MagicCosmetics.getInstance(), "balloon");
                    armorStand.setRemoveWhenFarAway(false);
                    armorStand.setMetadata("cosmetics", metadataValue);
                    armorStand.setArms(false);
                    armorStand.setMarker(true);
                    armorStand.setBasePlate(false);
                    setupModelEngine(armorStand);
                    balloonEngine.detectPlayers();
                    if (isColored()) {
                        balloonEngine.tintModel(getColor());
                    }
                }
                armorStand.setCollidable(false);
                armorStand.setVisible(false);
                armorStand.setInvisible(true);
                armorStand.setGravity(false);
                armorStand.setInvulnerable(true);
                update(entity);
                return;
            }
            if(armorStand == null || armorStand.isDead() || !armorStand.isValid() || leashed == null || leashed.isDead() || !leashed.isValid()){
                if(entity.isDead()) return;
                clear(null);
                leashed = entity.getWorld().spawn(entity.getLocation().clone().add(0, space, 0).add(entity.getLocation().clone().getDirection().multiply(-1)), PufferFish.class);
                armorStand = entity.getWorld().spawn(leashed.getLocation().clone().subtract(0, 1.3, 0), ArmorStand.class);
                leashed.setRemoveWhenFarAway(false);
                leashed.setAI(false);
                leashed.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false));
                leashed.setPuffState(-2);
                leashed.setGravity(false);
                leashed.setLeashHolder(entity);
                MetadataValue metadataValue = new FixedMetadataValue(MagicCosmetics.getInstance(), "balloon");
                leashed.setMetadata("cosmetics", metadataValue);

                armorStand.setRemoveWhenFarAway(false);
                armorStand.setMetadata("cosmetics", metadataValue);
                armorStand.setArms(false);
                armorStand.setMarker(true);
                armorStand.setBasePlate(false);

                setupModelEngine(armorStand);
                balloonEngine.detectPlayers();
                if (isColored()) {
                    balloonEngine.tintModel(getColor());
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
                if(armorStand == null || armorStand.isDead() || !armorStand.isValid()) {
                    if(player.isDead()) return;
                    if(player.getGameMode() == GameMode.SPECTATOR) return;
                    clear(player);
                    armorStand = player.getWorld().spawn(player.getLocation().clone().add(0, space, 0).add(player.getLocation().clone().getDirection().normalize().multiply(-1)), ArmorStand.class);
                    MetadataValue metadataValue = new FixedMetadataValue(MagicCosmetics.getInstance(), "balloon");
                    armorStand.setRemoveWhenFarAway(false);
                    armorStand.setMetadata("cosmetics", metadataValue);
                    armorStand.setArms(false);
                    armorStand.setMarker(true);
                    armorStand.setBasePlate(false);

                    setupModelEngine(armorStand);
                    balloonEngine.detectPlayers();
                    if (isColored()) {
                        balloonEngine.tintModel(getColor());
                    }
                }
                armorStand.setCollidable(false);
                armorStand.setVisible(false);
                armorStand.setInvisible(true);
                armorStand.setGravity(false);
                armorStand.setInvulnerable(true);
                update(player);
                return;
            }
            if( leashed == null || leashed.isDead() || !leashed.isValid()){
                if(player.isDead()) return;
                if(player.getGameMode() == GameMode.SPECTATOR) return;
                clear(player);
                leashed = player.getWorld().spawn(player.getLocation().clone().add(0, space, 0).add(player.getLocation().clone().getDirection().multiply(-1)), PufferFish.class);
                //pufferFish = player.getWorld().spawn(leashed.getLocation().clone(), PufferFish.class);
                leashed.setRemoveWhenFarAway(false);
                leashed.setAI(false);
                leashed.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false));
                leashed.setPuffState(-2);
                leashed.setGravity(false);
                leashed.setLeashHolder(player);
                MetadataValue metadataValue = new FixedMetadataValue(MagicCosmetics.getInstance(), "balloon");
                leashed.setMetadata("cosmetics", metadataValue);

                //pufferFish.setRemoveWhenFarAway(false);
                //pufferFish.setMetadata("cosmetics", metadataValue);
                //pufferFish.setAI(false);
                //pufferFish.setTarget(player);

                setupModelEngine(leashed);
                balloonEngine.detectPlayers();
                if (isColored()) {
                    balloonEngine.tintModel(getColor());
                }
                //balloonEngine.getModeledEntity().tick();
            }
            leashed.setCollidable(false);
            leashed.setInvisible(true);
            leashed.setInvulnerable(true);
            leashed.setSilent(true);
            //pufferFish.setCollidable(false);
            //pufferFish.setInvisible(true);
            //pufferFish.setGravity(false);
            //pufferFish.setInvulnerable(true);
            update(player);
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
            if(armorStand != null && leashed != null){
                for(Entity entity : armorStand.getWorld().getEntities()){
                    if(entity.isDead() || !entity.isValid()) continue;
                    if(entity.getUniqueId().equals(armorStand.getUniqueId()) || entity.getUniqueId().equals(leashed.getUniqueId()) || entity.getUniqueId().equals(balloonEngine.getEntity().getUniqueId())) {
                        entity.remove();
                    }
                }
            }
            balloonEngine.remove();
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
        if(armorStand != null){
            armorStand.remove();
            armorStand = null;
        }
        if(leashed != null){
            leashed.setLeashHolder(null);
            leashed.remove();
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
        if(balloonEngine != null){
            balloonEngine.remove();
            if(balloonEngine.getEntity() != null){
                balloonEngine.getEntity().remove();
                balloonEngine.setEntity(null);
            }
        }
        if(balloonIA != null){
            balloonIA.remove();
        }
    }

    private final double CATCH_UP_INCREMENTS = .27; //.25
    private double CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS; //.25

    public void update(Entity entity){
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

                Location normal = standTo.clone().setDirection(standTo.getDirection().multiply(-0.01));
                leashed.teleport(normal);
                balloonIA.getCustomEntity().teleport(standTo.clone().subtract(0, 1.3, 0));
            }else {
                if (!standDir.equals(new Vector())) {
                    standDir.normalize();
                }
                Location standToLoc = stand.clone().setDirection(standDir.setY(0));

                Location loc = standToLoc.clone().setDirection(standToLoc.getDirection().multiply(-0.01));
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
                LivingEntity livingEntity = balloonEngine.getEntity();
                if(livingEntity == null) return;
                Location playerLoc = entity.getLocation().clone().add(0, space, 0);
                Location stand = livingEntity.getLocation();
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
                    livingEntity.teleport(normal);
                }else {
                    if (!standDir.equals(new Vector())) {
                        standDir.normalize();
                    }
                    Location standToLoc = stand.clone().setDirection(standDir.setY(0));
                    Location normal = standToLoc.clone().setDirection(standToLoc.getDirection().multiply(0.01));
                    livingEntity.teleport(normal);
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
            LivingEntity livingEntity = balloonEngine.getEntity();
            if(livingEntity == null) return;
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
                Location normal = standTo.clone().setDirection(standTo.getDirection().multiply(-0.01));
                leashed.teleport(normal);
                //livingEntity.teleport(standTo.clone().subtract(0, 1.3, 0));
            }else {
                if (!standDir.equals(new Vector())) {
                    standDir.normalize();
                }
                Location standToLoc = stand.clone().setDirection(standDir.setY(0));

                Location loc = standToLoc.clone().setDirection(standToLoc.getDirection().multiply(-0.01));
                leashed.teleport(loc);
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
            return;
        }
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
            if(entity.getLocation().distanceSquared(stand) > SQUARED_WALKING){
                Vector lineBetween = playerLoc.clone().subtract(stand).toVector();
                if (!standDir.equals(new Vector())) {
                    standDir.normalize();
                }
                Vector distVec = lineBetween.clone().normalize().multiply(CATCH_UP_INCREMENTS_DISTANCE);
                Location standTo = stand.clone().setDirection(standDir.setY(0)).add(distVec.clone());
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
                armorStand.teleport(standToLoc.clone().subtract(0, 1.3, 0));
            }

            if(entity.getLocation().distanceSquared(stand) > SQUARED_WALKING){
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
            if(entity.getLocation().distanceSquared(stand) > SQUARED_DISTANCE){
                CATCH_UP_INCREMENTS_DISTANCE += 0.01;
            }else{
                CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS;
            }
            return;
        }
        if(armorStand == null) return;
        if(leashed == null) return;
        Location playerLoc = entity.getLocation().clone().add(0, space, 0);
        Location stand = leashed.getLocation();
        Vector standDir;
        if(entity instanceof Player){
            standDir = ((Player)entity).getEyeLocation().clone().subtract(stand).toVector();
        }else{
            standDir = entity.getLocation().clone().subtract(stand).toVector();
        }
        if(entity.getLocation().distanceSquared(stand) > SQUARED_WALKING){
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

        if(entity.getLocation().distanceSquared(stand) > SQUARED_WALKING){
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
        if(entity.getLocation().distanceSquared(stand) > SQUARED_DISTANCE){
            CATCH_UP_INCREMENTS_DISTANCE += 0.01;
        }else{
            CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS;
        }
    }

    public void setupModelEngine(LivingEntity livingEntity){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        balloonEngine.setActiveModel(plugin.getModelEngine().createActiveModel(balloonEngine.getModelId()));
        balloonEngine.setModeledEntity(plugin.getModelEngine().spawnModel(livingEntity, balloonEngine.getModelId(), balloonEngine.getActiveModel()));
        balloonEngine.setEntity(livingEntity);
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
