package com.songoda.skyblock.localization.type.impl;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;

public class MaterialsToMaterialLocalization extends EnumLocalization<XMaterial> {
    public MaterialsToMaterialLocalization(String keysPath) {
        super(keysPath, XMaterial.class);
    }

    @Override
    public XMaterial parseEnum(String input) {
        return CompatibleMaterial.getMaterial(input).get();
    }
}
