package com.songoda.skyblock.permission.permissions.basic;

import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class IslandPermission extends BasicPermission {
    public IslandPermission() {
        super("Island", XMaterial.OAK_SAPLING, PermissionType.OPERATOR);
    }
}
