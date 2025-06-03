package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.MajorServerVersion;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class TradingPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public TradingPermission(SkyBlock plugin) {
        super("Trading", XMaterial.EMERALD, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (event.getRightClicked().getType() == EntityType.VILLAGER
                || MajorServerVersion.isServerVersionAtLeast(MajorServerVersion.V1_14)
                && event.getRightClicked().getType() == EntityType.WANDERING_TRADER) {
            cancelAndMessage(event, player, this.plugin, this.messageManager);
        }
    }
}
