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

import java.util.Optional;

public class TrapdoorPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public TrapdoorPermission(SkyBlock plugin) {
        super("Trapdoor", XMaterial.OAK_TRAPDOOR, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        Optional<XMaterial> material = CompatibleMaterial.getMaterial(event.getClickedBlock().getType());
        if (!material.isPresent()) {
            return;
        }

        switch (material.get()) {
            case OAK_TRAPDOOR:
            case BIRCH_TRAPDOOR:
            case ACACIA_TRAPDOOR:
            case JUNGLE_TRAPDOOR:
            case SPRUCE_TRAPDOOR:
            case WARPED_TRAPDOOR:
            case CRIMSON_TRAPDOOR:
            case DARK_OAK_TRAPDOOR:
                cancelAndMessage(event, player, this.plugin, this.messageManager);
                break;
        }
    }
}
