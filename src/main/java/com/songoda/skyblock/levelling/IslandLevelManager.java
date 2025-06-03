package com.songoda.skyblock.levelling;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.MajorServerVersion;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.blockscanner.BlockInfo;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.levelling.amount.AmountMaterialPair;
import com.songoda.skyblock.levelling.calculator.Calculator;
import com.songoda.skyblock.levelling.calculator.CalculatorRegistry;
import com.songoda.skyblock.levelling.calculator.impl.EpicSpawnerCalculator;
import com.songoda.skyblock.levelling.calculator.impl.UltimateStackerCalculator;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.stackable.StackableManager;
import com.songoda.skyblock.utils.MaterialUtils;
import com.songoda.skyblock.utils.version.CompatibleSpawners;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public final class IslandLevelManager {
    private final Map<Island, QueuedIslandScan> inScan;
    private final Map<XMaterial, Double> worth;
    private final Map<XMaterial, AmountMaterialPair> cachedPairs;
    private final SkyBlock plugin;

    public IslandLevelManager(SkyBlock plugin) {
        this.plugin = plugin;
        this.inScan = new HashMap<>();
        this.worth = new EnumMap<>(XMaterial.class);
        this.cachedPairs = new EnumMap<>(XMaterial.class);
        registerCalculators();
        reloadWorth();
    }

    public void startScan(Player attemptScanner, Island island) {
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(this.plugin, () -> startScan(attemptScanner, island));
            return;
        }

        if (island == null) {
            throw new IllegalArgumentException("island cannot be null");
        }

        Configuration config = this.plugin.getLanguage();
        MessageManager messageManager = this.plugin.getMessageManager();

        if (this.inScan.containsKey(island)) {
            if (attemptScanner != null) {
                messageManager.sendMessage(attemptScanner, config.getString("Command.Island.Level.Scanning.InScan.Message"));
            }
            return;
        }

        if (attemptScanner != null) {
            if (this.plugin.getIslandManager().getIslandPlayerAt(attemptScanner) != island) {
                messageManager.sendMessage(attemptScanner, config.getString("Command.Island.Level.Scanning.NotOnIsland.Message"));
                return;
            }

            messageManager.sendMessage(attemptScanner, config.getString("Command.Island.Level.Scanning.Started.Message"));
        }

        QueuedIslandScan queuedIslandScan = new QueuedIslandScan(this.plugin, island);

        queuedIslandScan.addToScan(IslandWorld.NORMAL);
        if (island.isRegionUnlocked(null, IslandWorld.NETHER)) {
            queuedIslandScan.addToScan(IslandWorld.NETHER);
        }
        if (island.isRegionUnlocked(null, IslandWorld.END)) {
            queuedIslandScan.addToScan(IslandWorld.END);
        }

        queuedIslandScan.scan();

        this.inScan.put(island, queuedIslandScan);
    }

    public boolean isScanning(Island island) {
        return this.inScan.containsKey(island);
    }

    void stopScan(Island island) {

        final QueuedIslandScan queuedIslandScan = this.inScan.get(island);

        if (queuedIslandScan == null) {
            return;
        }

        if (!queuedIslandScan.scan()) {
            this.inScan.remove(island);
        }
    }

    public void reloadWorth() {
        this.worth.clear();

        final Configuration config = this.plugin.getLevelling();
        final ConfigurationSection materialSection = config.getConfigurationSection("Materials");

        if (materialSection == null) {
            return;
        }

        for (String key : materialSection.getKeys(false)) {

            final ConfigurationSection current = materialSection.getConfigurationSection(key);

            final Optional<XMaterial> material = CompatibleMaterial.getMaterial(key);

            if (!material.isPresent()) {
                continue;
            }

            this.worth.put(material.get(), current.getDouble("Points", 0.0));
        }
    }

    public void addWorth(XMaterial material, double points) {
        this.worth.put(material, points);
    }

    public void removeWorth(XMaterial material) {
        this.worth.remove(material);
    }

    public List<LevellingMaterial> getWorthsAsLevelingMaterials() {
        final List<LevellingMaterial> materials = new ArrayList<>(this.worth.size());

        for (Entry<XMaterial, Double> entry : this.worth.entrySet()) {
            materials.add(new LevellingMaterial(entry.getKey(), entry.getValue()));
        }

        return materials;
    }

    public Map<XMaterial, Double> getWorths() {
        return this.worth;
    }

    public double getWorth(XMaterial material) {
        return this.worth.getOrDefault(material, 0d);
    }

    public boolean hasWorth(XMaterial material) {
        return this.worth.containsKey(material);
    }

    private void registerCalculators() {
        final XMaterial spawner = XMaterial.SPAWNER;
        final PluginManager pm = Bukkit.getPluginManager();

        if (pm.isPluginEnabled("EpicSpawners")) {
            CalculatorRegistry.registerCalculator(new EpicSpawnerCalculator(), spawner);
        }
        if (pm.isPluginEnabled("UltimateStacker")) {
            CalculatorRegistry.registerCalculator(new UltimateStackerCalculator(), spawner);
        }
    }

    private static final AmountMaterialPair EMPTY = new AmountMaterialPair(null, 0);

    AmountMaterialPair getAmountAndType(IslandScan scan, BlockInfo info) {

        Block block = info.getWorld().getBlockAt(info.getX(), info.getY(), info.getZ());
        Optional<XMaterial> blockType = CompatibleMaterial.getMaterial(block.getType());

        if (!blockType.isPresent() || CompatibleMaterial.isAir(blockType.get())) {
            return EMPTY;
        }

        Optional<XMaterial> compMaterial = CompatibleMaterial.getMaterial(block.getType());

        if (!compMaterial.isPresent()) {
            return EMPTY;
        }

        final Location blockLocation = block.getLocation();

        if (scan.getDoubleBlocks().contains(blockLocation)) {
            return EMPTY;
        }

        if (MaterialUtils.isTall(compMaterial.orElse(XMaterial.STONE))) {
            final Block belowBlock = block.getRelative(BlockFace.DOWN);
            final XMaterial belowMaterial = CompatibleMaterial.getMaterial(belowBlock.getType()).get();

            if (MaterialUtils.isTall(belowMaterial)) {
                block = belowBlock;
                blockType = Optional.of(belowMaterial);
                scan.getDoubleBlocks().add(belowBlock.getLocation());
            } else {
                scan.getDoubleBlocks().add(block.getRelative(BlockFace.UP).getLocation());
            }
        }

        final List<Calculator> calculators = CalculatorRegistry.getCalculators(blockType.get());
        final StackableManager stackableManager = this.plugin.getStackableManager();

        final long stackSize = stackableManager == null ? 0 : stackableManager.getStackSizeOf(blockLocation, compMaterial.get());

        if (calculators == null) {

            if (stackSize > 1) {
                return new AmountMaterialPair(compMaterial.get(), stackSize);
            }

            AmountMaterialPair cachedPair = this.cachedPairs.get(compMaterial);

            if (cachedPair != null) {
                return cachedPair;
            }

            cachedPair = new AmountMaterialPair(compMaterial.get(), 1);
            this.cachedPairs.put(compMaterial.get(), cachedPair);

            return cachedPair;
        }

        long amount = 0;

        for (Calculator calc : calculators) {
            amount += calc.getAmount(block);
        }

        if (amount == 0) {
            amount = 1;
        }

        return new AmountMaterialPair(compMaterial.get(), amount + stackSize);
    }

    public void updateLevel(Island island, Location location) {
        // Fix a bug in Paper 1.8.8 when using ViaVersion on a 1.12.2 client.
        // BUG: Player can infinitely increase their level by placing a block at their
        // feet.
        // It doesn't take the block away but still increments the level.
        // This doesn't happen in Spigot, but does happen in PaperSpigot due to a
        // BlockPlaceEvent being incorrectly fired.
        // The solution is to wait a tick to make sure that the block was actually
        // placed.
        // This shouldn't cause any issues besides the task number being increased
        // insanely fast.
        if (MajorServerVersion.isServerVersion(MajorServerVersion.V1_8)) {
            Bukkit.getScheduler().runTask(this.plugin, () -> {
                updateLevelLocation(island, location);
            });
        } else {
            updateLevelLocation(island, location);
        }
    }

    private void updateLevelLocation(Island island, Location location) {
        Block block = location.getBlock();
        XMaterial material = null;
        if (MajorServerVersion.isServerVersion(MajorServerVersion.V1_8)) {
            switch (block.getType().toString().toUpperCase()) {
                case "DIODE_BLOCK_OFF":
                case "DIODE_BLOCK_ON":
                    material = XMaterial.REPEATER;
                    break;
            }
        }
        if (material == null) {
            material = CompatibleMaterial.getMaterial(block.getType()).orElse(null);
        }

        if (material == null || material == XMaterial.AIR) {
            return;
        }

        if (material == XMaterial.SPAWNER) {
            if (Bukkit.getPluginManager().isPluginEnabled("EpicSpawners") ||
                    Bukkit.getPluginManager().isPluginEnabled("UltimateStacker") ||
                    Bukkit.getPluginManager().isPluginEnabled("WildStacker")) {
                return;
            }

            CompatibleSpawners spawner = CompatibleSpawners.getSpawner(((CreatureSpawner) block.getState()).getSpawnedType());

            if (spawner != null) {
                material = CompatibleMaterial.getMaterial(spawner.getMaterial()).orElse(null);
            }
        }

        long materialAmount = 0;
        IslandLevel level = island.getLevel();

        if (level.hasMaterial(material.name())) {
            materialAmount = level.getMaterialAmount(material.name());
        }

        level.setMaterialAmount(material.name(), materialAmount + 1);
    }
}
