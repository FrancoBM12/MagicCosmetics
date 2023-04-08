package com.francobm.magicosmetics.cache;

import com.francobm.magicosmetics.files.FileCreator;
import com.francobm.magicosmetics.utils.Cuboid;
import com.francobm.magicosmetics.utils.Utils;
import com.francobm.magicosmetics.utils.XMaterial;
import com.francobm.magicosmetics.MagicCosmetics;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Zone {
    public static Map<String, Zone> zones = new HashMap<>();
    private final String id;
    private final String name;
    private Cuboid cuboid;
    private Location corn1;
    private Location corn2;
    private Location npc;
    private Location enter;
    private Location exit;
    private Location balloon;
    private Location sprayLoc;
    private BlockFace sprayFace;
    private boolean active;
    private int rotation;

    public Zone(String id, String name){
        this.id = id;
        this.name = name;
        this.cuboid = null;
        this.corn1 = null;
        this.corn2 = null;
        this.npc = null;
        this.enter = null;
        this.exit = null;
        this.balloon = null;
        this.sprayLoc = null;
        this.sprayFace = null;
        this.active = false;
    }

    public Zone(String name){
        this.id = String.valueOf(zones.size()+1);
        this.name = name;
        this.cuboid = null;
        this.corn1 = null;
        this.corn2 = null;
        this.npc = null;
        this.enter = null;
        this.exit = null;
        this.balloon = null;
        this.sprayLoc = null;
        this.sprayFace = null;
        this.active = false;
    }

    public Zone(String id, String name, Location corn1, Location corn2, Location npc, Location enter, Location exit, Location balloon, Location sprayLoc, BlockFace sprayFace){
        this.id = id;
        this.name = name;
        this.cuboid = new Cuboid(corn1, corn2);
        this.corn1 = corn1;
        this.corn2 = corn2;
        this.npc = npc;
        this.enter = enter;
        this.exit = exit;
        this.balloon = balloon;
        this.sprayLoc = sprayLoc;
        this.sprayFace = sprayFace;
        this.active = false;
        //loadSpec();
    }

    public static Zone getZone(String name){
        return zones.get(name);
    }

    public static Zone addZone(String name){
        return zones.put(name, new Zone(String.valueOf(zones.size()+1), name));
    }

    public static void loadZones(){
        zones.clear();
        FileCreator zone = MagicCosmetics.getInstance().getZones();
        if(!zone.contains("zones")) return;
        for(String key : zone.getConfigurationSection("zones").getKeys(false)){
            String name = "";
            Location corn1 = null;
            Location corn2 = null;
            Location npc = null;
            Location enter = null;
            Location exit = null;
            Location balloon = null;
            Location sprayLoc = null;
            BlockFace sprayFace = null;
            boolean active = false;
            if(zone.contains("zones." + key + ".name")){
                name = zone.getString("zones." + key + ".name");
            }
            if(zone.contains("zones." + key + ".corn1")){
                String c1 = zone.getString("zones." + key + ".corn1");
                if(c1.equalsIgnoreCase("Location is Null!!")){
                    MagicCosmetics.getInstance().getLogger().info("Location of Corn1 is Null!");
                }else{
                    corn1 = Utils.convertStringToLocation(c1);
                }
            }
            if(zone.contains("zones." + key + ".corn2")){
                String c2 = zone.getString("zones." + key + ".corn2");
                if(c2.equalsIgnoreCase("Location is Null!!")){
                    MagicCosmetics.getInstance().getLogger().info("Location of Corn2 is Null!");
                }else{
                    corn2 = Utils.convertStringToLocation(c2);
                }
            }
            if(zone.contains("zones." + key + ".npc")){
                String npcString = zone.getString("zones." + key + ".npc");
                if(npcString.equalsIgnoreCase("Location is Null!!")){
                    MagicCosmetics.getInstance().getLogger().info("Location of NPC is Null!");
                }else{
                    npc = Utils.convertStringToLocation(npcString);
                }
            }
            if(zone.contains("zones." + key + ".enter")){
                String enterString = zone.getString("zones." + key + ".enter");
                if(enterString.equalsIgnoreCase("Location is Null!!")){
                    MagicCosmetics.getInstance().getLogger().info("Location of Enter is Null!");
                }else{
                    enter = Utils.convertStringToLocation(enterString);
                }
            }
            if(zone.contains("zones." + key + ".exit")){
                String exitString = zone.getString("zones." + key + ".exit");
                if(exitString.equalsIgnoreCase("Location is Null!!")){
                    MagicCosmetics.getInstance().getLogger().info("Location of Exit is Null!");
                }else{
                    exit = Utils.convertStringToLocation(exitString);
                }
            }
            if(zone.contains("zones." + key + ".balloon")){
                String balloonString = zone.getString("zones." + key + ".balloon");
                if(balloonString.equalsIgnoreCase("Location is Null!!")){
                   MagicCosmetics.getInstance().getLogger().info("Location of Balloon is Null!");
                }else{
                    balloon = Utils.convertStringToLocation(balloonString);
                }
            }
            if(zone.contains("zones." + key + ".spray")){
                String sprayLocString = zone.getString("zones." + key + ".spray.loc");
                String sprayFaceString = zone.getString("zones." + key + ".spray.face");
                if(sprayLocString.equalsIgnoreCase("Location is Null!!")){
                    MagicCosmetics.getInstance().getLogger().info("Location of Spray is Null!");
                }else{
                    sprayLoc = Utils.convertStringToLocation(sprayLocString);
                    try{
                        sprayFace = BlockFace.valueOf(sprayFaceString.toUpperCase());
                    }catch(IllegalArgumentException e){
                        MagicCosmetics.getInstance().getLogger().info("Face of Spray is Null!");
                    }
                }
            }
            if(zone.contains("zones." + key + ".enabled")){
                active = zone.getBoolean("zones." + key + ".enabled");
            }
            Zone z = new Zone(key, name, corn1, corn2, npc, enter, exit, balloon, sprayLoc, sprayFace);
            z.setActive(active);
            zones.put(name, z);
        }
    }

    public static void removeZone(String id){
        Zone zone = getZone(id);
        if(zone == null) return;
        zone.setCuboid(null);
        zones.remove(zone.getName());
        FileCreator zoneConf = MagicCosmetics.getInstance().getZones();
        zoneConf.set("zones", null);
        for(Zone z : zones.values()){
            String zId = z.getId();
            String name = z.getName();
            String corn1 = Utils.convertLocationToString(z.getCorn1(), true);
            String corn2 = Utils.convertLocationToString(z.getCorn2(), true);
            String npc = Utils.convertLocationToString(z.getNpc(), false);
            String enter = Utils.convertLocationToString(z.getEnter(), false);
            String exit = Utils.convertLocationToString(z.getExit(), false);
            String balloon = Utils.convertLocationToString(z.getBalloon(), false);
            String sprayLoc = Utils.convertLocationToString(z.getSprayLoc(), true);
            String sprayFace = z.getSprayFace().name();
            boolean enabled = z.isActive();
            zoneConf.set("zones." + zId + ".name", name);
            zoneConf.set("zones." + zId + ".corn1", corn1);
            zoneConf.set("zones." + zId + ".corn2", corn2);
            zoneConf.set("zones." + zId + ".npc", npc);
            zoneConf.set("zones." + zId + ".enter", enter);
            zoneConf.set("zones." + zId + ".exit", exit);
            zoneConf.set("zones." + zId + ".balloon", balloon);
            zoneConf.set("zones." + zId + ".spray.loc", sprayLoc);
            zoneConf.set("zones." + zId + ".spray.face", sprayFace);
            zoneConf.set("zones." + zId + ".enabled", enabled);
        }
        zoneConf.save();
    }

    public static void saveZone(String id){
        FileCreator zone = MagicCosmetics.getInstance().getZones();
        Zone z = getZone(id);
        if(z == null) return;
        String name = z.getName();
        String corn1 = Utils.convertLocationToString(z.getCorn1(), true);
        String corn2 = Utils.convertLocationToString(z.getCorn2(), true);
        String npc = Utils.convertLocationToString(z.getNpc(), false);
        String enter = Utils.convertLocationToString(z.getEnter(), false);
        String exit = Utils.convertLocationToString(z.getExit(), false);
        String balloon = Utils.convertLocationToString(z.getBalloon(), false);
        String sprayLoc = Utils.convertLocationToString(z.getSprayLoc(), true);
        String sprayFace = z.getSprayFace().name();
        boolean enabled = z.isActive();
        zone.set("zones." + id + ".name", name);
        zone.set("zones." + id + ".corn1", corn1);
        zone.set("zones." + id + ".corn2", corn2);
        zone.set("zones." + id + ".npc", npc);
        zone.set("zones." + id + ".enter", enter);
        zone.set("zones." + id + ".exit", exit);
        zone.set("zones." + id + ".balloon", balloon);
        zone.set("zones." + id + ".spray.loc", sprayLoc);
        zone.set("zones." + id + ".spray.face", sprayFace);
        zone.set("zones." + id + ".enabled", enabled);
        zone.save();
        if(z.getCuboid() == null) {
            if(z.getCorn1() != null && z.getCorn2() != null) {
                z.setCuboid(new Cuboid(z.getCorn1(), z.getCorn2()));
            }
        }
    }

    public static void saveZones(){
        FileCreator zone = MagicCosmetics.getInstance().getZones();
        zone.set("zones", null);
        for(Zone z : zones.values()){
            String id = z.getId();
            String name = z.getName();
            String corn1 = Utils.convertLocationToString(z.getCorn1(), true);
            String corn2 = Utils.convertLocationToString(z.getCorn2(), true);
            String npc = Utils.convertLocationToString(z.getNpc(), false);
            String enter = Utils.convertLocationToString(z.getEnter(), false);
            String exit = Utils.convertLocationToString(z.getExit(), false);
            String balloon = Utils.convertLocationToString(z.getBalloon(), false);
            boolean enabled = z.isActive();
            zone.set("zones." + id + ".name", name);
            zone.set("zones." + id + ".corn1", corn1);
            zone.set("zones." + id + ".corn2", corn2);
            zone.set("zones." + id + ".npc", npc);
            zone.set("zones." + id + ".enter", enter);
            zone.set("zones." + id + ".exit", exit);
            zone.set("zones." + id + ".balloon", balloon);
            zone.set("zones." + id + ".enabled", enabled);
        }
        zone.save();
    }

    public void giveCorns(Player player){
        player.getInventory().addItem(getCorn());
        player.updateInventory();
    }

    public ItemStack getCorn(){
        ItemStack itemStack = XMaterial.BLAZE_ROD.parseItem();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§bSet the points of the area " + getName());
        List<String> lore = new ArrayList<>();
        lore.add("§eLeft click to set the first position");
        lore.add("§eRight click to set second position");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return MagicCosmetics.getInstance().getVersion().setNBTCosmetic(itemStack, "wand" + getName());
    }

    public boolean isInZone(Block block){
        if(!active){
            return false;
        }
        return detectBlock(block, cuboid);
    }

    public Location getBalloon() {
        return balloon;
    }

    public void setBalloon(Location balloon) {
        this.balloon = balloon;
    }

    private boolean detectBlock(Block blockLocation, Cuboid cuboid){
        return cuboid.contains(blockLocation);
    }

    public String getName() {
        return name;
    }

    public Location getNpc() {
        return npc;
    }

    public Location getExit() {
        return exit;
    }

    public Location getEnter() {
        return enter;
    }

    public void setEnter(Location enter) {
        this.enter = enter;
    }

    public void setNpc(Location npc) {
        this.npc = npc;
    }

    public void setExit(Location exit) {
        this.exit = exit;
    }

    public Location getCorn1() {
        return corn1;
    }

    public Location getCorn2() {
        return corn2;
    }

    public void setCorn1(Location corn1) {
        this.corn1 = corn1;
    }

    public void setCorn2(Location corn2) {
        this.corn2 = corn2;
    }

    public String getId() {
        return id;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public void setCuboid(Cuboid cuboid) {
        this.cuboid = cuboid;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        if(getCuboid() == null) {
            if(getCorn1() != null && getCorn2() != null) {
                setCuboid(new Cuboid(getCorn1(), getCorn2()));
            }
        }
    }

    public void setSprayFace(BlockFace sprayFace) {
        this.sprayFace = sprayFace;
    }

    public BlockFace getSprayFace() {
        return sprayFace;
    }

    public void setSprayLoc(Location sprayLoc) {
        this.sprayLoc = sprayLoc;
    }

    public Location getSprayLoc() {
        return sprayLoc;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int getRotation() {
        return rotation;
    }
}
