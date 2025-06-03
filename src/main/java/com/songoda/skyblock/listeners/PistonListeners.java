package com.songoda.skyblock.listeners;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.world.WorldManager;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

import java.util.Optional;

public class PistonListeners implements Listener {
    private final SkyBlock plugin;

    public PistonListeners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    // Prevent point farming dragon eggs.
    @EventHandler
    public void onPistonMove(BlockPistonExtendEvent event) {
        Block block = event.getBlock().getRelative(event.getDirection());

        IslandManager islandManager = this.plugin.getIslandManager();
        WorldManager worldManager = this.plugin.getWorldManager();
        if (!worldManager.isIslandWorld(block.getWorld())) {
            return;
        }

        Island island = islandManager.getIslandAtLocation(block.getLocation());
        if (island == null || CompatibleMaterial.getMaterial(block.getType()).get() != XMaterial.DRAGON_EGG) {
            return;
        }

        FileConfiguration configLoad = this.plugin.getConfiguration();
        if (!configLoad.getBoolean("Island.Block.Level.Enable")) {
            return;
        }

        Optional<XMaterial> material = CompatibleMaterial.getMaterial(block.getType());
        if (!material.isPresent()) {
            return;
        }

        IslandLevel level = island.getLevel();
        if (!level.hasMaterial(material.get().name())) {
            return;
        }

        long materialAmount = level.getMaterialAmount(material.get().name());
        if (materialAmount <= 1) {
            level.removeMaterial(material.get().name());
        } else {
            level.setMaterialAmount(material.get().name(), materialAmount - 1);
        }
    }
}
