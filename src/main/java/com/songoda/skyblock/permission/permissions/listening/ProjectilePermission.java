package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionPriority;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ProjectilePermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public ProjectilePermission(SkyBlock plugin) {
        super("Projectile", XMaterial.ARROW, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_AIR) {
            return;
        }

        Player player = event.getPlayer();
        if (event.getItem() != null && XMaterial.EGG.isSimilar(event.getItem())) {
            cancelAndMessage(event, player, this.plugin, this.messageManager);
        }
    }

    @PermissionHandler(priority = PermissionPriority.LAST)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            cancelAndMessage(event, (Player) event.getEntity().getShooter(), this.plugin, this.messageManager);
        }
    }
}
