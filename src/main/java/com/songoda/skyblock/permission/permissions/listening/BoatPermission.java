package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class BoatPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public BoatPermission(SkyBlock plugin) {
        super("Boat", XMaterial.OAK_BOAT, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (event.getRightClicked().getType().toString().contains("BOAT")) {
            cancelAndMessage(event, player, this.plugin, this.messageManager);
        }
    }
}
