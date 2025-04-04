package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class FurnacePermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public FurnacePermission(SkyBlock plugin) {
        super("Furnace", XMaterial.FURNACE, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block != null && block.getState() instanceof Furnace) {
            cancelAndMessage(event, player, this.plugin, this.messageManager);
        }
    }
}
