package com.francobm.customcosmetics.nms;

import com.francobm.customcosmetics.cache.Sound;
import com.francobm.customcosmetics.nms.NPC.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Version {

    public abstract void setSpectator(Player player);

    public abstract void createNPC(Player player);

    public abstract void createNPC(Player player, Location location);

    public abstract NPC getNPC(Player player);

    public abstract void removeNPC(Player player);

    public abstract NPC getNPC();

    public abstract void sendSound(Player player, Sound sound);
}
