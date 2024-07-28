package com.francobm.magicosmetics.managers;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.PlayerData;
import com.francobm.magicosmetics.cache.Zone;
import com.francobm.magicosmetics.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import static com.francobm.magicosmetics.utils.Utils.sendMessage;

public class ZonesManager {

    private final MagicCosmetics plugin = MagicCosmetics.getInstance();

    public void saveZone(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        if(zone.getNpc() == null){
            sendMessage(player, plugin.prefix + "§cSet the NPC Location!");
            return;
        }
        if(zone.getBalloon() == null){
            sendMessage(player, plugin.prefix + "§cSet the NPC's Balloon Location!");
            return;
        }
        if(zone.getEnter() == null){
            sendMessage(player, plugin.prefix + "§cSet the Enter Location!");
            return;
        }
        if(zone.getExit() == null){
            sendMessage(player, plugin.prefix + "§cSet the Exit Location!");
            return;
        }
        if(zone.getCorn1() == null){
            sendMessage(player, plugin.prefix + "§cSet the Corn1 Location!");
            return;
        }
        if(zone.getCorn2() == null){
            sendMessage(player, plugin.prefix + "§cSet the Corn2 Location!");
            return;
        }
        if(zone.getSprayLoc() == null){
            sendMessage(player, plugin.prefix + "§cSet the Spray Location!");
            return;
        }
        Zone.saveZone(name);
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-save").replace("%name%", name));
    }

    public void addZone(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        if(Zone.getZone(name) != null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-exist"));
            return;
        }
        Zone.addZone(name);
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-created").replace("%name%", name));
        giveCorn(player, name);
    }

    public void removeZone(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        if(Zone.getZone(name) == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        Zone.removeZone(name);
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-removed").replace("%name%", name));
    }

    public void giveCorn(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.giveCorns(player);
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("give-corns"));
    }

    public void setSpray(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        Location location = player.getEyeLocation();
        RayTraceResult result = location.getWorld().rayTrace(location, location.getDirection(), 10, FluidCollisionMode.ALWAYS, false, 1, (entity) -> false);
        if(result == null) return;
        if(result.getHitEntity() != null && result.getHitEntity().getType() == EntityType.ITEM_FRAME) return;
        final int rotation;
        if(result.getHitBlockFace() == BlockFace.UP || result.getHitBlockFace() == BlockFace.DOWN) {
            rotation = Utils.getRotation(player.getLocation().getYaw(), false) * 45;
        } else {
            rotation = 0;
        }
        Location loc = result.getHitBlock().getRelative(result.getHitBlockFace()).getLocation();
        zone.setSprayLoc(loc);
        zone.setSprayFace(result.getHitBlockFace());
        zone.setRotation(rotation);
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("set-spray").replace("%name%", zone.getName()));
    }

    public void setBalloonNPC(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.setBalloon(player.getLocation());
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("set-balloon").replace("%name%", zone.getName()));
    }

    public void setZoneNPC(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.setNpc(player.getLocation());
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("set-npc").replace("%name%", zone.getName()));
    }

    public void setZoneEnter(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.setEnter(player.getLocation().clone());
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("set-enter").replace("%name%", zone.getName()));
    }

    public void setZoneExit(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.setExit(player.getLocation());
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("set-exit").replace("%name%", zone.getName()));
    }

    public void disableZone(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.setActive(false);
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-disable").replace("%name%", name));
    }

    public void enableZone(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        if(zone.getNpc() == null){
            sendMessage(player, plugin.prefix + "§cSet the NPC Location!");
            return;
        }
        if(zone.getBalloon() == null){
            sendMessage(player, plugin.prefix + "§cSet the NPC's Balloon Location!");
            return;
        }
        if(zone.getEnter() == null){
            sendMessage(player, plugin.prefix + "§cSet the Enter Location!");
            return;
        }
        if(zone.getExit() == null){
            sendMessage(player, plugin.prefix + "§cSet the Exit Location!");
            return;
        }
        if(zone.getCorn1() == null){
            sendMessage(player, plugin.prefix + "§cSet the Corn1 Location!");
            return;
        }
        if(zone.getCorn2() == null){
            sendMessage(player, plugin.prefix + "§cSet the Corn2 Location!");
            return;
        }
        if(zone.getSprayLoc() == null){
            sendMessage(player, plugin.prefix + "§cSet the Spray Location!");
            return;
        }
        if(plugin.getUser() == null) return;
        zone.setActive(true);
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-enable").replace("%name%", name));
    }

    public void exitZone(Player player){
        PlayerData playerData = PlayerData.getPlayer(player);
        playerData.exitZone();
    }
}
