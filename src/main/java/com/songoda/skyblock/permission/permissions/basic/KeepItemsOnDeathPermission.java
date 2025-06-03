package com.songoda.skyblock.permission.permissions.basic;

import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class KeepItemsOnDeathPermission extends BasicPermission {
    public KeepItemsOnDeathPermission() {
        super("KeepItemsOnDeath", XMaterial.ITEM_FRAME, PermissionType.ISLAND);
    }
}
