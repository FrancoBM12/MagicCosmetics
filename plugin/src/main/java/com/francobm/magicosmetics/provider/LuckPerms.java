package com.francobm.magicosmetics.provider;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.cache.PlayerData;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.DefaultContextKeys;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LuckPerms {
    private final net.luckperms.api.LuckPerms luckPermsAPI;
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();

    public LuckPerms() {
        this.luckPermsAPI = LuckPermsProvider.get();
        luckPermsAPI.getEventBus().subscribe(plugin, NodeRemoveEvent.class, this::onNodeRemove);
    }

    public void addPermission(UUID uniqueId, String permission) {
        User user = luckPermsAPI.getUserManager().getUser(uniqueId);
        if(user == null) return;
        if(plugin.getLuckPermsServer() == null || plugin.getLuckPermsServer().isEmpty()){
            user.data().add(PermissionNode.builder(permission).build());
            luckPermsAPI.getUserManager().saveUser(user);
            return;
        }
        user.data().add(PermissionNode.builder(permission).withContext(DefaultContextKeys.SERVER_KEY, plugin.getLuckPermsServer()).build());
        luckPermsAPI.getUserManager().saveUser(user);
    }

    public void removePermission(UUID uniqueId, String permission) {
        luckPermsAPI.getUserManager().modifyUser(uniqueId, user -> {
            if(plugin.getLuckPermsServer() == null || plugin.getLuckPermsServer().isEmpty()){
                PermissionNode permissionNode = PermissionNode.builder(permission).build();
                user.data().remove(permissionNode);
                return;
            }
            PermissionNode permissionNode = PermissionNode.builder(permission).withContext(DefaultContextKeys.SERVER_KEY, plugin.getLuckPermsServer()).build();
            user.data().remove(permissionNode);
        });
    }

    public boolean isExpirePermission(UUID uniqueId, String permission) {
        User user = luckPermsAPI.getUserManager().getUser(uniqueId);
        if(user == null) return false;
        boolean hasPermission = user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
        if(hasPermission) return false;
        removePermission(uniqueId, permission);
        return true;
    }

    private void onNodeRemove(NodeRemoveEvent event) {
        if(!event.isUser()) return;
        Node node = event.getNode();
        if(!(node instanceof PermissionNode)) return;
        PermissionNode permissionNode = (PermissionNode) node;
        User user = (User) event.getTarget();
        Player player = Bukkit.getPlayer(user.getUniqueId());
        if(player == null) return;
        PlayerData playerData = PlayerData.getPlayer(player);
        for(Cosmetic cosmetic : playerData.cosmeticsInUse()) {
            if(!permissionNode.getPermission().equalsIgnoreCase(cosmetic.getPermission())) continue;
            plugin.getServer().getScheduler().runTask(plugin, () -> playerData.removeCosmetic(cosmetic.getId()));
        }
    }
}
