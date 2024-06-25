package com.francobm.magicosmetics.cache.cosmetics.balloons;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.cache.RotationType;
import com.francobm.magicosmetics.nms.balloon.EntityBalloon;
import com.francobm.magicosmetics.nms.balloon.PlayerBalloon;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Balloon extends Cosmetic {
    private ArmorStand armorStand;
    private PufferFish leashed;
    private PlayerBalloon playerBalloon;
    private EntityBalloon entityBalloon;
    private double space;
    private boolean rotation;
    private RotationType rotationType;
    private BalloonEngine balloonEngine;
    private BalloonIA balloonIA;
    protected double distance;
    private double SQUARED_WALKING;
    private double SQUARED_DISTANCE;
    private boolean bigHead;
    private boolean invisibleLeash;
    private boolean instantFollow;

    public Balloon(String id, String name, ItemStack itemStack, int modelData, boolean colored, double space, CosmeticType cosmeticType, Color color, boolean rotation, RotationType rotationType, BalloonEngine balloonEngine, BalloonIA balloonIA, double distance, String permission, boolean texture, boolean bigHead, boolean hideMenu, boolean invisibleLeash, boolean useEmote, boolean instantFollow, NamespacedKey namespacedKey) {
        super(id, name, itemStack, modelData, colored, cosmeticType, color, permission, texture, hideMenu, useEmote, namespacedKey);
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

    @Override
    protected void updateCosmetic(Cosmetic cosmetic) {
        super.updateCosmetic(cosmetic);
        Balloon balloon = (Balloon) cosmetic;
        this.space = balloon.space;
        this.rotation = balloon.rotation;
        this.rotationType = balloon.rotationType;
        this.distance = balloon.distance;
        this.balloonEngine = balloon.balloonEngine;
        this.balloonIA = balloon.balloonIA;
        this.SQUARED_WALKING = 5.5 * space;
        this.SQUARED_DISTANCE = 10 * space;
        this.bigHead = balloon.bigHead;
        this.invisibleLeash = balloon.invisibleLeash;
        this.instantFollow = balloon.instantFollow;
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
            clear();
            return;
        }
        if(balloonIA != null) {
            if(invisibleLeash){
                if(balloonIA.getCustomEntity() == null) {
                    if(entity.isDead()) return;
                    clear();
                    balloonIA.spawn(entity.getLocation().clone().add(0, space, 0).add(entity.getLocation().clone().getDirection().normalize().multiply(-1)));
                    balloonIA.detectPlayers(leashed, entity);
                    if (isColored()) {
                        balloonIA.paintBalloon(getColor());
                    }
                }
                balloonIA.detectPlayers(leashed, entity);
                update(entity);
                return;
            }
            if(balloonIA.getCustomEntity() == null) {
                if(entity.isDead()) return;
                clear();
                balloonIA.spawn(entity.getLocation().clone().add(0, space, 0).add(entity.getLocation().clone().getDirection().normalize().multiply(-1)));
                leashed = balloonIA.spawnLeash(balloonIA.getLeashBone().getLocation());
                balloonIA.detectPlayers(leashed, entity);
                if (isColored()) {
                    balloonIA.paintBalloon(getColor());
                }
            }
            balloonIA.detectPlayers(leashed, entity);
            update(entity);
            return;
        }
        if(balloonEngine != null){
            if(invisibleLeash) {
                if(balloonEngine.getBalloonUniqueId() == null){
                    if(entity.isDead()) return;
                    clear();
                    armorStand = balloonEngine.spawnModel(entity.getLocation().clone().add(0, space, 0).add(entity.getLocation().clone().getDirection().normalize().multiply(-1)));
                    if (isColored()) {
                        balloonEngine.tintModel(getColor());
                    }
                }
                update(entity);
                return;
            }
            if(balloonEngine.getBalloonUniqueId() == null){
                if(entity.isDead()) return;
                clear();
                armorStand = balloonEngine.spawnModel(entity.getLocation().clone().add(0, space, 0).add(entity.getLocation().clone().getDirection().normalize().multiply(-1)));
                balloonEngine.spawnLeash(entity);
                if (isColored()) {
                    balloonEngine.tintModel(getColor());
                }
            }
            update(entity);
            return;
        }
        if(entityBalloon == null){
            if(entity.isDead()) return;

            clear();
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
    public void lendToEntity() {
        if(balloonIA != null) {
            if(invisibleLeash) {
                if(balloonIA.getCustomEntity() == null) {
                    if(lendEntity.isDead()) return;
                    clear();
                    balloonIA.spawn(lendEntity.getLocation().clone().add(0, space, 0).add(lendEntity.getLocation().clone().getDirection().normalize().multiply(-1)));
                    if (isColored()) {
                        balloonIA.paintBalloon(getColor());
                    }
                }
                update(player);
                return;
            }
            if(balloonIA.getCustomEntity() == null){
                if(lendEntity.isDead()) return;
                clear();
                balloonIA.spawn(lendEntity.getLocation().clone().add(0, space, 0).add(lendEntity.getLocation().clone().getDirection().normalize().multiply(-1)));
                leashed = balloonIA.spawnLeash(balloonIA.getLeashBone().getLocation());
                balloonIA.detectPlayers(leashed, player);
                if (isColored()) {
                    balloonIA.paintBalloon(getColor());
                }
            }
            balloonIA.detectPlayers(leashed, player);
            update(player);
            return;
        }
        if(balloonEngine != null){
            if(invisibleLeash) {
                if(balloonEngine.getBalloonUniqueId() == null){
                    if(lendEntity.isDead()) return;
                    clear();

                    armorStand = balloonEngine.spawnModel(lendEntity.getLocation().clone().add(0, space, 0).add(lendEntity.getLocation().clone().getDirection().normalize().multiply(-1)));
                    if (isColored()) {
                        balloonEngine.tintModel(getColor());
                    }
                }
                update(player);
                return;
            }
            if(balloonEngine.getBalloonUniqueId() == null){
                if(lendEntity.isDead()) return;
                clear();
                armorStand = balloonEngine.spawnModel(lendEntity.getLocation().clone().add(0, space, 0).add(lendEntity.getLocation().clone().getDirection().normalize().multiply(-1)));
                balloonEngine.spawnLeash(lendEntity);
                if (isColored()) {
                    balloonEngine.tintModel(getColor());
                }
            }
            update(player);
            return;
        }
        if(playerBalloon == null){
            if(lendEntity.isDead()) return;

            clear();
            playerBalloon = MagicCosmetics.getInstance().getVersion().createPlayerBalloon(player, space, distance, bigHead, invisibleLeash);
            playerBalloon.spawn(false);
        }
        playerBalloon.setItem(getItemColor(player));
        playerBalloon.rotate(rotation, rotationType, (float) MagicCosmetics.getInstance().balloonRotation);
        playerBalloon.update(instantFollow);
        playerBalloon.spawn(true);
    }

    @Override
    public void hide(Player player) {
        if(balloonIA != null){
            return;
        }
        if(balloonEngine != null) {
            balloonEngine.hideModel(player);
            return;
        }
        if(playerBalloon != null) {
            playerBalloon.addHideViewer(player);
        }
    }

    @Override
    public void show(Player player) {
        if(balloonIA != null){
            return;
        }
        if(balloonEngine != null) {
            balloonEngine.showModel(player);
            return;
        }
        if(playerBalloon != null) {
            playerBalloon.removeHideViewer(player);
        }
    }

    @Override
    public void active() {
        if(isHideCosmetic()) {
            clear();
            return;
        }
        if(!removedLendEntity && player.isInvisible() || !removedLendEntity && player.isGliding() || !removedLendEntity && player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
            clear();
            return;
        }
        if(player == null) {
            return;
        }
        if(balloonIA != null) {
            if(invisibleLeash) {
                if(balloonIA.getCustomEntity() == null) {
                    if(player.isDead()) return;
                    if(player.getGameMode() == GameMode.SPECTATOR) return;
                    clear();
                    balloonIA.spawn(player.getLocation().clone().add(0, space, 0).add(player.getLocation().clone().getDirection().normalize().multiply(-1)));
                    if (isColored()) {
                        balloonIA.paintBalloon(getColor());
                    }
                }
                update(player);
                return;
            }
            if(balloonIA.getCustomEntity() == null){
                if(player.isDead() || !player.isValid()) return;
                if(player.getGameMode() == GameMode.SPECTATOR) return;
                clear();
                balloonIA.spawn(player.getLocation().clone().add(0, space, 0).add(player.getLocation().clone().getDirection().normalize().multiply(-1)));
                leashed = balloonIA.spawnLeash(balloonIA.getLeashBone().getLocation());
                balloonIA.detectPlayers(leashed, player);
                if (isColored()) {
                    balloonIA.paintBalloon(getColor());
                }
            }
            balloonIA.detectPlayers(leashed, player);
            update(player);
            return;
        }
        if(balloonEngine != null){
            if(invisibleLeash) {
                if(balloonEngine.getBalloonUniqueId() == null){
                    if(player.isDead()) return;
                    clear();

                    armorStand = balloonEngine.spawnModel(player.getLocation().clone().add(0, space, 0).add(player.getLocation().clone().getDirection().normalize().multiply(-1)));
                    if (isColored()) {
                        balloonEngine.tintModel(getColor());
                    }
                }
                update(player);
                return;
            }
            if(balloonEngine.getBalloonUniqueId() == null){
                if(player.isDead()) return;
                clear();
                armorStand = balloonEngine.spawnModel(player.getLocation().clone().add(0, space, 0).add(player.getLocation().clone().getDirection().normalize().multiply(-1)));
                balloonEngine.spawnLeash(player);
                if (isColored()) {
                    balloonEngine.tintModel(getColor());
                }
            }
            update(player);
            return;
        }
        if(playerBalloon == null){
            if(player.isDead()) return;
            if(player.getGameMode() == GameMode.SPECTATOR) return;

            clear();
            playerBalloon = MagicCosmetics.getInstance().getVersion().createPlayerBalloon(player, space, distance, bigHead, invisibleLeash);
            playerBalloon.spawn(false);
        }
        if(removedLendEntity && !player.isInvisible())
            removedLendEntity = false;
        playerBalloon.setItem(getItemColor(player));
        playerBalloon.rotate(rotation, rotationType, (float) MagicCosmetics.getInstance().balloonRotation);
        playerBalloon.update(instantFollow);
        playerBalloon.spawn(true);
    }

    @Override
    public void clear() {
        if(balloonEngine != null){
            balloonEngine.remove(armorStand);
        }
        if(balloonIA != null){
            balloonIA.remove(leashed);
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

    @Override
    public void clearClose() {
        if(balloonEngine != null){
            balloonEngine.remove(armorStand);
        }
        if(balloonIA != null){
            balloonIA.remove(leashed);
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
            Location playerLoc = entity.getLocation().clone().add(0, space, 0);
            Location stand = balloonIA.getModelLocation();
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
                balloonIA.getCustomEntity().teleport(normal.clone());
                balloonIA.updateTeleport(leashed);
                //balloonIA.getCustomEntity().teleport(standTo.clone().subtract(0, 1.3, 0));
            }else {
                if (!standDir.equals(new Vector())) {
                    standDir.normalize();
                }
                Location standToLoc = stand.clone().setDirection(standDir.setY(0));
                Location normal = standToLoc.clone().setDirection(standToLoc.getDirection().multiply(0.01));
                balloonIA.getCustomEntity().teleport(normal.clone());
                balloonIA.updateTeleport(leashed);
                //balloonIA.getCustomEntity().teleport(standToLoc.clone().subtract(0, 1.3, 0));
            }
            if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
                balloonIA.setState(1);
            }else{
                balloonIA.setState(0);
            }
            if(distance1.distanceSquared(distance2) > SQUARED_DISTANCE){
                CATCH_UP_INCREMENTS_DISTANCE += 0.01;
            }else{
                CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS;
            }
            return;
        }
        if(balloonEngine != null && armorStand != null){
            Location playerLoc = entity.getLocation().clone().add(0, space, 0);
            Location stand = armorStand.getLocation();
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
            balloonEngine.updateTeleport(armorStand, standToLoc);
            balloonEngine.setStatePlayOn(0);
        }
    }

    public void update(Entity entity){
        if(instantFollow){
            instantUpdate(entity);
            return;
        }
        if(balloonIA != null){
            Location playerLoc = entity.getLocation().clone().add(0, space, 0);
            Location stand = balloonIA.getModelLocation();
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
                balloonIA.getCustomEntity().teleport(normal);
                balloonIA.updateTeleport(leashed);
                //balloonIA.getCustomEntity().teleport(standTo.clone().subtract(0, 1.3, 0));
            }else {
                Vector lineBetween = playerLoc.clone().subtract(stand).toVector();
                if (!standDir.equals(new Vector())) {
                    standDir.normalize();
                }
                Vector distVec = lineBetween.clone().normalize().multiply(CATCH_UP_INCREMENTS_DISTANCE);
                double y = distVec.getY();
                if(entity instanceof Player && ((Player)entity).isSneaking()){
                    y -= 0.13;
                }
                Location standToLoc = stand.clone().setDirection(standDir.setY(0)).add(0, y, 0);
                Location normal = standToLoc.clone().setDirection(standToLoc.getDirection().multiply(0.01));
                balloonIA.getCustomEntity().teleport(normal);
                balloonIA.updateTeleport(leashed);
                //balloonIA.getCustomEntity().teleport(standToLoc.clone().subtract(0, 1.3, 0));
            }
            if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
                balloonIA.setState(1);
            }else{
                balloonIA.setState(0);
            }
            if(distance1.distanceSquared(distance2) > SQUARED_DISTANCE){
                CATCH_UP_INCREMENTS_DISTANCE += 0.01;
            }else{
                CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS;
            }
            return;
        }
        if(balloonEngine != null && armorStand != null){
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
                balloonEngine.updateTeleport(armorStand, normal);
            }else {
                Vector lineBetween = playerLoc.clone().subtract(stand).toVector();
                if (!standDir.equals(new Vector())) {
                    standDir.normalize();
                }
                Vector distVec = lineBetween.clone().normalize().multiply(CATCH_UP_INCREMENTS_DISTANCE);
                double y = distVec.getY();
                if(entity instanceof Player && ((Player)entity).isSneaking()){
                    y -= 0.13;
                }
                Location standToLoc = stand.clone().setDirection(standDir.setY(0)).add(0, y, 0);
                Location normal = standToLoc.clone().setDirection(standToLoc.getDirection().multiply(0.01));
                balloonEngine.updateTeleport(armorStand, normal);
            }

            if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
                balloonEngine.setState(1);
            }else{
                balloonEngine.setState(0);
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

    @Override
    public void setLendEntity(LivingEntity lendEntity) {
        super.setLendEntity(lendEntity);
        if(playerBalloon == null) return;
        playerBalloon.setLendEntity(lendEntity);
    }
}
