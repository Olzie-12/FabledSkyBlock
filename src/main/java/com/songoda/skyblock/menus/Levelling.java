package com.songoda.skyblock.menus;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.MajorServerVersion;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.core.utils.SkullItemCreator;
import com.songoda.third_party.com.cryptomorin.xseries.XItemFlag;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.core.utils.ItemUtils;
import com.songoda.core.utils.NumberUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.cooldown.Cooldown;
import com.songoda.skyblock.cooldown.CooldownManager;
import com.songoda.skyblock.cooldown.CooldownPlayer;
import com.songoda.skyblock.cooldown.CooldownType;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.levelling.IslandLevelManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.placeholder.Placeholder;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.item.nInventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class Levelling {
    // Static cache that persists for the lifetime of the plugin
    private static final Map<String, Boolean> MATERIAL_VALIDITY_CACHE = new HashMap<>();

    // We are going to filter these items out. They are not valid for levelling.
    private static final Set<String> NON_ITEM_MATERIALS = new HashSet<>(Arrays.asList(
            "ATTACHED_MELON_STEM", "ATTACHED_PUMPKIN_STEM", "BAMBOO_SAPLING", "BEETROOTS",
            "BIRCH_WALL_SIGN", "BLACK_WALL_BANNER", "BLUE_WALL_BANNER", "BRAIN_CORAL_WALL_FAN",
            "BROWN_WALL_BANNER", "BUBBLE_COLUMN", "BUBBLE_CORAL_WALL_FAN", "CARROTS",
            "CAVE_AIR", "COCOA", "CREEPER_WALL_HEAD", "CRIMSON_WALL_SIGN",
            "CYAN_WALL_BANNER", "DARK_OAK_WALL_SIGN", "DEAD_BRAIN_CORAL_WALL_FAN",
            "DEAD_BUBBLE_CORAL_WALL_FAN", "DEAD_FIRE_CORAL_WALL_FAN", "DEAD_HORN_CORAL_WALL_FAN",
            "DEAD_TUBE_CORAL_WALL_FAN", "DRAGON_WALL_HEAD", "END_GATEWAY", "END_PORTAL",
            "FIRE", "FIRE_CORAL_WALL_FAN", "FROSTED_ICE", "GRAY_WALL_BANNER",
            "GREEN_WALL_BANNER", "HORN_CORAL_WALL_FAN", "JUNGLE_WALL_SIGN", "KELP_PLANT",
            "LIGHT_BLUE_WALL_BANNER", "LIGHT_GRAY_WALL_BANNER", "LIME_WALL_BANNER",
            "MAGENTA_WALL_BANNER", "MELON_STEM", "MOVING_PISTON", "NETHER_PORTAL",
            "OAK_WALL_SIGN", "ORANGE_WALL_BANNER", "PINK_WALL_BANNER", "PISTON_HEAD",
            "PLAYER_WALL_HEAD", "POTATOES", "POTTED_ACACIA_SAPLING", "POTTED_ALLIUM",
            "POTTED_AZURE_BLUET", "POTTED_BAMBOO", "POTTED_BIRCH_SAPLING", "POTTED_BLUE_ORCHID",
            "POTTED_BROWN_MUSHROOM", "POTTED_CACTUS", "POTTED_CORNFLOWER", "POTTED_DANDELION",
            "POTTED_DARK_OAK_SAPLING", "POTTED_DEAD_BUSH", "POTTED_FERN", "POTTED_JUNGLE_SAPLING",
            "POTTED_LILY_OF_THE_VALLEY", "POTTED_OAK_SAPLING", "POTTED_ORANGE_TULIP",
            "POTTED_OXEYE_DAISY", "POTTED_PINK_TULIP", "POTTED_POPPY", "POTTED_RED_MUSHROOM",
            "POTTED_RED_TULIP", "POTTED_SPRUCE_SAPLING", "POTTED_WHITE_TULIP", "POTTED_WITHER_ROSE",
            "PUMPKIN_STEM", "PURPLE_WALL_BANNER", "REDSTONE_WALL_TORCH", "REDSTONE_WIRE",
            "RED_WALL_BANNER", "SKELETON_WALL_SKULL", "SOUL_WALL_TORCH", "SPRUCE_WALL_SIGN",
            "SWEET_BERRY_BUSH", "TALL_SEAGRASS", "TRIPWIRE", "TUBE_CORAL_WALL_FAN",
            "TWISTING_VINES_PLANT", "VOID_AIR", "WALL_TORCH", "WARPED_WALL_SIGN",
            "WEEPING_VINES_PLANT", "WHITE_WALL_BANNER", "WITHER_SKELETON_WALL_SKULL",
            "YELLOW_WALL_BANNER", "ZOMBIE_WALL_HEAD"
    ));

    private static Levelling instance;

    public static Levelling getInstance() {
        if (instance == null) {
            instance = new Levelling();
        }

        return instance;
    }

    public void open(Player player) {
        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);

        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        IslandLevelManager levellingManager = plugin.getLevellingManager();
        CooldownManager cooldownManager = plugin.getCooldownManager();
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        SoundManager soundManager = plugin.getSoundManager();

        if (!playerDataManager.hasPlayerData(player)) {
            return;
        }

        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        FileConfiguration configLoad = plugin.getLanguage();

        nInventoryUtil nInv = new nInventoryUtil(player, event -> {
            if (islandManager.getIsland(player) == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Owner.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                player.closeInventory();
                return;
            }

            if (!playerDataManager.hasPlayerData(player)) {
                return;
            }

            ItemStack is = event.getItem();

            if ((XMaterial.BLACK_STAINED_GLASS_PANE.isSimilar(is)) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Barrier.Displayname"))))) {
                soundManager.playSound(player, XSound.BLOCK_GLASS_BREAK);

                event.setWillClose(false);
                event.setWillDestroy(false);
            } else if ((XMaterial.OAK_FENCE_GATE.isSimilar(is)) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Exit.Displayname"))))) {
                soundManager.playSound(player, XSound.BLOCK_CHEST_CLOSE);
            } else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Statistics.Displayname"))))) {
                soundManager.playSound(player, XSound.ENTITY_VILLAGER_YES);

                event.setWillClose(false);
                event.setWillDestroy(false);
            } else if ((is.getType() == Material.BARRIER) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Nothing.Displayname"))))) {
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                event.setWillClose(false);
                event.setWillDestroy(false);
            } else if ((XMaterial.FIREWORK_STAR.isSimilar(is)) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Rescan.Displayname"))))) {
                Island island = islandManager.getIsland(player);
                OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID());

                if (cooldownManager.hasPlayer(CooldownType.LEVELLING, offlinePlayer) && !player.hasPermission("fabledskyblock.bypass.cooldown")) {
                    CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.LEVELLING, offlinePlayer);
                    Cooldown cooldown = cooldownPlayer.getCooldown();

                    long[] durationTime = NumberUtil.getDuration(cooldown.getTime());

                    if (cooldown.getTime() >= 3600) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Level.Cooldown.Message").replace("%time",
                                        durationTime[1] + " " + configLoad.getString("Command.Island.Level.Cooldown.Word.Minute") + " " + durationTime[2] + " "
                                                + configLoad.getString("Command.Island.Level.Cooldown.Word.Minute") + " " + durationTime[3] + " "
                                                + configLoad.getString("Command.Island.Level.Cooldown.Word.Second")));
                    } else if (cooldown.getTime() >= 60) {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Cooldown.Message").replace("%time", durationTime[2] + " "
                                + configLoad.getString("Command.Island.Level.Cooldown.Word.Minute") + " " + durationTime[3] + " " + configLoad.getString("Command.Island.Level.Cooldown.Word.Second")));
                    } else {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Cooldown.Message").replace("%time",
                                cooldown.getTime() + " " + configLoad.getString("Command.Island.Level.Cooldown.Word.Second")));
                    }

                    soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);

                    event.setWillClose(false);
                    event.setWillDestroy(false);

                    return;
                }

                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Processing.Message"));
                    soundManager.playSound(player, XSound.ENTITY_VILLAGER_YES);

                    cooldownManager.createPlayer(CooldownType.LEVELLING, Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID()));
                    levellingManager.startScan(player, island);
                });
            } else if ((XMaterial.PLAYER_HEAD.isSimilar(is)) && (is.hasItemMeta())) {
                PlayerData playerData1 = plugin.getPlayerDataManager().getPlayerData(player);

                if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Previous.Displayname")))) {
                    playerData1.setPage(MenuType.LEVELLING, playerData1.getPage(MenuType.LEVELLING) - 1);
                    soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Next.Displayname")))) {
                    playerData1.setPage(MenuType.LEVELLING, playerData1.getPage(MenuType.LEVELLING) + 1);
                    soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                } else {
                    soundManager.playSound(player, XSound.ENTITY_CHICKEN_EGG);

                    event.setWillClose(false);
                    event.setWillDestroy(false);
                }
            } else {
                soundManager.playSound(player, XSound.ENTITY_CHICKEN_EGG);

                event.setWillClose(false);
                event.setWillDestroy(false);
            }
        });

        Island island = islandManager.getIsland(player);
        IslandLevel level = island.getLevel();

        Map<String, Long> testIslandMaterials = level.getMaterials();
        List<String> testIslandMaterialKeysOrdered = testIslandMaterials.keySet().stream().sorted().collect(Collectors.toList());
        LinkedHashMap<String, Long> islandMaterials = new LinkedHashMap<>();

        // Filter out ItemStacks that can't be displayed in the inventory
        Inventory testInventory = Bukkit.createInventory(null, 9);

        for (String materialName : testIslandMaterialKeysOrdered) {
            if (plugin.getLevelling().getString("Materials." + materialName + ".Points") == null ||
                    !plugin.getConfiguration().getBoolean("Island.Levelling.IncludeEmptyPointsInList") &&
                            plugin.getLevelling().getInt("Materials." + materialName + ".Points") <= 0) {
                continue;
            }

            long value = testIslandMaterials.get(materialName);

            // Check material validity from cache first
            if (!isValidItemMaterial(materialName)) {
                continue;
            }

            Optional<XMaterial> materials = CompatibleMaterial.getMaterial(materialName);
            if (!materials.isPresent()) {
                continue;
            }

            ItemStack is = materials.get().parseItem();

            if (is == null || is.getItemMeta() == null) {
                continue;
            }

            is.setAmount(Math.min(Math.toIntExact(value), 64));
            is.setType(CompatibleMaterial.getMaterial(is.getType()).get().parseMaterial());

            testInventory.clear();
            testInventory.setItem(0, is);

            if (testInventory.getItem(0) != null) {
                islandMaterials.put(materialName, value);
            }
        }

        int playerMenuPage = playerData.getPage(MenuType.LEVELLING), nextEndIndex = islandMaterials.size() - playerMenuPage * 36;

        nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Levelling.Item.Exit.Displayname"), null, null, null, null), 0, 8);
        if (player.hasPermission("fabledskyblock.island.level.rescan")) {
            nInv.addItem(nInv.createItem(XMaterial.FIREWORK_STAR.parseItem(), configLoad.getString("Menu.Levelling.Item.Rescan.Displayname"), configLoad.getStringList("Menu.Levelling.Item.Rescan.Lore"), null, null,
                    new ItemFlag[]{XItemFlag.HIDE_ADDITIONAL_TOOLTIP.get()}), 3, 5);
        }
        nInv.addItem(
                nInv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Levelling.Item.Statistics.Displayname"), configLoad.getStringList("Menu.Levelling.Item.Statistics.Lore"),
                        new Placeholder[]{new Placeholder("%level_points", NumberUtils.formatNumber(level.getPoints())), new Placeholder("%level", NumberUtils.formatNumber(level.getLevel()))}, null, null),
                4);
        nInv.addItem(nInv.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(), configLoad.getString("Menu.Levelling.Item.Barrier.Displayname"), null, null, null, null), 9, 10, 11, 12, 13, 14, 15, 16, 17);

        if (playerMenuPage != 1) {
            ItemStack Lhead = SkullItemCreator.byTextureUrlHash("3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
            nInv.addItem(nInv.createItem(Lhead,
                    configLoad.getString("Menu.Levelling.Item.Previous.Displayname"), null, null, null, null), 1);
        }

        if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
            ItemStack Rhead = SkullItemCreator.byTextureUrlHash("1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b");
            nInv.addItem(nInv.createItem(Rhead,
                    configLoad.getString("Menu.Levelling.Item.Next.Displayname"), null, null, null, null), 7);
        }

        if (islandMaterials.isEmpty()) {
            nInv.addItem(nInv.createItem(XMaterial.BARRIER.parseItem(), configLoad.getString("Menu.Levelling.Item.Nothing.Displayname"), null, null, null, null), 31);
        } else {
            int index = playerMenuPage * 36 - 36, endIndex = index >= islandMaterials.size() ? islandMaterials.size() - 1 : index + 36, inventorySlot = 17;

            for (; index < endIndex; index++) {
                if (islandMaterials.size() <= index) {
                    break;
                }

                String material = (String) islandMaterials.keySet().toArray()[index];
                Optional<XMaterial> materials = CompatibleMaterial.getMaterial(material);

                if (!materials.isPresent()) {
                    break;
                }

                long materialAmount = islandMaterials.get(material);

                if (plugin.getLevelling().getString("Materials." + material + ".Points") == null) {
                    break;
                }

                double pointsMultiplier = plugin.getLevelling().getDouble("Materials." + material + ".Points");

                if (!plugin.getConfiguration().getBoolean("Island.Levelling.IncludeEmptyPointsInList") && pointsMultiplier == 0) {
                    return;
                }

                inventorySlot++;

                long materialLimit = plugin.getLevelling().getLong("Materials." + material + ".Limit", -1);
                long materialAmountCounted = Math.min(materialLimit, materialAmount);

                if (materialLimit == -1) {
                    materialAmountCounted = materialAmount;
                }

                double pointsEarned = materialAmountCounted * pointsMultiplier;


                String name = plugin.getLocalizationManager().getLocalizationFor(XMaterial.class).getLocale(materials.get());

                if (materials.get() == XMaterial.FARMLAND && MajorServerVersion.isServerVersionBelow(MajorServerVersion.V1_9)) {
                    materials = Optional.of(XMaterial.DIRT);
                }

                ItemStack is = materials.get().parseItem();
                is.setAmount(Math.min(Math.toIntExact(materialAmount), 64));
                is.setType(CompatibleMaterial.getMaterial(is.getType()).get().parseMaterial());

                long finalMaterialAmountCounted = materialAmountCounted;
                List<String> lore = configLoad.getStringList("Menu.Levelling.Item.Material.Lore");
                lore.replaceAll(x -> x.replace("%points", NumberUtils.formatNumber(pointsEarned)).replace("%blocks", NumberUtils.formatNumber(materialAmount))
                        .replace("%material", name).replace("%counted", NumberUtils.formatNumber(finalMaterialAmountCounted)));

                nInv.addItem(nInv.createItem(is, configLoad.getString("Menu.Levelling.Item.Material.Displayname").replace("%points", NumberUtils.formatNumber(pointsEarned))
                                .replace("%blocks", NumberUtils.formatNumber(materialAmount)).replace("%material", name).replace("%counted", NumberUtils.formatNumber(finalMaterialAmountCounted))
                        , lore, null, null, null), inventorySlot);

            }
        }

        nInv.setTitle(plugin.formatText(configLoad.getString("Menu.Levelling.Title")));
        nInv.setRows(6);

        Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
    }

    private boolean isValidItemMaterial(String materialName) {
        // Check cache first
        if (MATERIAL_VALIDITY_CACHE.containsKey(materialName)) {
            return MATERIAL_VALIDITY_CACHE.get(materialName);
        }
        // If not in cache, perform the check
        boolean isValid = true;
        if (NON_ITEM_MATERIALS.contains(materialName)) {
            isValid = false;
        } else {
            Optional<XMaterial> materials = CompatibleMaterial.getMaterial(materialName);
            if (!materials.isPresent()) {
                isValid = false;
            } else {
                Material bukkitMaterial = materials.get().parseMaterial();
                if (bukkitMaterial == null || !bukkitMaterial.isItem()) {
                    isValid = false;
                }
            }
        }
        // Cache
        MATERIAL_VALIDITY_CACHE.put(materialName, isValid);
        return isValid;
    }
}
