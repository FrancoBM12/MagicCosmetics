package com.francobm.magicosmetics.provider;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;

import java.util.UUID;

public class LuckPerms {
    private final net.luckperms.api.LuckPerms luckPermsAPI;

    public LuckPerms() {
        this.luckPermsAPI = LuckPermsProvider.get();
    }

    public void addPermission(UUID uniqueId, String permission) {
        User user = luckPermsAPI.getUserManager().getUser(uniqueId);
        if(user == null) return;
        user.data().add(PermissionNode.builder(permission).build());
        luckPermsAPI.getUserManager().saveUser(user);
        /*luckPermsAPI.getUserManager().modifyUser(uniqueId, user -> {
            user.data().add(Node.builder(permission).build());
            //user.data().add(PermissionNode.builder(permission).build());
        });*/
    }

    public void removePermission(UUID uniqueId, String permission) {
        luckPermsAPI.getUserManager().modifyUser(uniqueId, user -> {
            PermissionNode permissionNode = PermissionNode.builder(permission).build();
            user.data().remove(permissionNode);
        });
    }
}
