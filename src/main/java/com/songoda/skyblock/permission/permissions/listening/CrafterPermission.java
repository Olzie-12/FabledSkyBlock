package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CrafterPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public CrafterPermission(SkyBlock plugin) {
        super("Crafter", XMaterial.CRAFTER, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        XMaterial material = CompatibleMaterial.getMaterial(event.getClickedBlock().getType()).orElse(null);
        if (material == XMaterial.CRAFTER) {
            cancelAndMessage(event, event.getPlayer(), this.plugin, this.messageManager);
        }
    }
}
