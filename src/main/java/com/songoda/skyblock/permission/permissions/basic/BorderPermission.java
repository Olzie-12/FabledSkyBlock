package com.songoda.skyblock.permission.permissions.basic;

import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class BorderPermission extends BasicPermission {
    public BorderPermission() {
        super("Border", XMaterial.BEACON, PermissionType.OPERATOR);
    }
}
