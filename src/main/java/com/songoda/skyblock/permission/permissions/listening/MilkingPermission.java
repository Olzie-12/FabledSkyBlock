package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class MilkingPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public MilkingPermission(SkyBlock plugin) {
        super("Milking", XMaterial.MILK_BUCKET, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {


        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity.getType() == EntityType.COW) {
            if (XMaterial.BUCKET.isSimilar(player.getItemInHand())) {
                cancelAndMessage(event, player, this.plugin, this.messageManager);
            }
        } else if (entity.getType().toString().equals("MUSHROOM_COW") || entity.getType().toString().equals("MOOSHROOM")) {
            if (XMaterial.BUCKET.isSimilar(player.getItemInHand())
                    || XMaterial.BOWL.isSimilar(player.getItemInHand())) {
                cancelAndMessage(event, player, this.plugin, this.messageManager);
            }
        }
    }
}
