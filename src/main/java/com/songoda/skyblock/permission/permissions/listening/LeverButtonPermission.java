package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class LeverButtonPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public LeverButtonPermission(SkyBlock plugin) {
        super("LeverButton", XMaterial.LEVER, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        XMaterial material = CompatibleMaterial.getMaterial(event.getClickedBlock().getType()).orElse(null);
        Player player = event.getPlayer();

        if (material == XMaterial.STONE_BUTTON || material == XMaterial.OAK_BUTTON
                || material == XMaterial.SPRUCE_BUTTON || material == XMaterial.BIRCH_BUTTON
                || material == XMaterial.JUNGLE_BUTTON || material == XMaterial.ACACIA_BUTTON
                || material == XMaterial.DARK_OAK_BUTTON || material == XMaterial.LEVER) {
            cancelAndMessage(event, player, this.plugin, this.messageManager);
        }
    }
}
