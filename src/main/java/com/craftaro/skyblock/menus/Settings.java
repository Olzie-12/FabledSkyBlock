package com.craftaro.skyblock.menus;

import com.craftaro.core.gui.AnvilGui;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandMessage;
import com.craftaro.skyblock.island.IslandPermission;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.island.IslandStatus;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.PermissionManager;
import com.craftaro.skyblock.placeholder.Placeholder;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.item.nInventoryUtil;
import com.craftaro.skyblock.visit.Visit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Settings {
    private static Settings instance;

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }

        return instance;
    }

    public void open(Player player, Settings.Type menuType, IslandRole role, Settings.Panel panel) {
        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);

        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        PermissionManager permissionManager = plugin.getPermissionManager();
        SoundManager soundManager = plugin.getSoundManager();
        FileManager fileManager = plugin.getFileManager();

        if (playerDataManager.hasPlayerData(player)) {
            Island island = islandManager.getIsland(player);

            FileConfiguration mainConfig = plugin.getConfiguration();
            FileConfiguration configLoad = plugin.getLanguage();

            if (menuType == Settings.Type.CATEGORIES) {
                nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                    if (playerDataManager.hasPlayerData(player)) {
                        Island island13 = islandManager.getIsland(player);

                        if (island13 == null) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Settings.Owner.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            return;
                        } else if (!(island13.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                                || island13.hasRole(IslandRole.OWNER, player.getUniqueId()))) {
                            messageManager.sendMessage(player, configLoad.getString("Command.Island.Role.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            return;
                        }

                        ItemStack is = event.getItem();

                        if ((XMaterial.OAK_FENCE_GATE.isSimilar(is)) && (is.hasItemMeta())
                                && (is.getItemMeta().getDisplayName().equals(plugin.formatText(
                                configLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"))))) {
                            soundManager.playSound(player, XSound.BLOCK_CHEST_CLOSE);
                        } else if ((is.getType() == Material.NAME_TAG) && (is.hasItemMeta())
                                && (is.getItemMeta().getDisplayName().equals(plugin.formatText(
                                configLoad.getString("Menu.Settings.Categories.Item.Coop.Displayname"))))) {
                            if (!fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"))
                                    .getFileConfiguration().getBoolean("Island.Coop.Enable")) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Command.Island.Coop.Disabled.Message"));
                                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                return;
                            }

                            if (island13.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                                    && !permissionManager.hasPermission(island13, "Coop", IslandRole.OPERATOR)) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Command.Island.Settings.Permission.Access.Message"));
                                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);

                                event.setWillClose(false);
                                event.setWillDestroy(false);

                                return;
                            }

                            soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, Type.ROLE, IslandRole.COOP, null), 1L);
                        } else if ((is.hasItemMeta()) && (is.getItemMeta().getDisplayName()
                                .equals(plugin.formatText(configLoad
                                        .getString("Menu.Settings.Categories.Item.Visitor.Displayname"))))) {
                            if (island13.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                                    && !permissionManager.hasPermission(island13, "Visitor", IslandRole.OPERATOR)) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Command.Island.Settings.Permission.Access.Message"));
                                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);

                                event.setWillClose(false);
                                event.setWillDestroy(false);

                                return;
                            }

                            soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, Type.ROLE, IslandRole.VISITOR, null), 1L);
                        } else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta())
                                && (is.getItemMeta().getDisplayName()
                                .equals(plugin.formatText(configLoad
                                        .getString("Menu.Settings.Categories.Item.Member.Displayname"))))) {
                            if (island13.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                                    && !permissionManager.hasPermission(island13, "Member", IslandRole.OPERATOR)) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Command.Island.Settings.Permission.Access.Message"));
                                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);

                                event.setWillClose(false);
                                event.setWillDestroy(false);

                                return;
                            }

                            soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, Type.ROLE, IslandRole.MEMBER, null), 1L);
                        } else if ((is.getType() == Material.ITEM_FRAME) && (is.hasItemMeta()) && (is.getItemMeta()
                                .getDisplayName().equals(plugin.formatText(configLoad
                                        .getString("Menu.Settings.Categories.Item.Operator.Displayname"))))) {
                            if (island13.hasRole(IslandRole.OPERATOR, player.getUniqueId())) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Command.Island.Settings.Permission.Access.Message"));
                                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);

                                event.setWillClose(false);
                                event.setWillDestroy(false);

                                return;
                            }

                            soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, Type.ROLE, IslandRole.OPERATOR, null), 1L);
                        } else if ((XMaterial.OAK_SAPLING.isSimilar(is)) && (is.hasItemMeta())
                                && (is.getItemMeta().getDisplayName()
                                .equals(plugin.formatText(configLoad
                                        .getString("Menu.Settings.Categories.Item.Owner.Displayname"))))) {
                            if (island13.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                                    && !permissionManager.hasPermission(island13, "Island", IslandRole.OPERATOR)) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Command.Island.Settings.Permission.Access.Message"));
                                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);

                                event.setWillClose(false);
                                event.setWillDestroy(false);

                                return;
                            }

                            soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, Type.ROLE, IslandRole.OWNER, null), 1L);
                        }
                    }
                });

                nInv.addItem(nInv.createItem(new ItemStack(XMaterial.OAK_SIGN.parseMaterial()),
                        configLoad.getString("Menu.Settings.Categories.Item.Visitor.Displayname"),
                        configLoad.getStringList("Menu.Settings.Categories.Item.Visitor.Lore"), null, null, null), 2);
                nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
                        configLoad.getString("Menu.Settings.Categories.Item.Member.Displayname"),
                        configLoad.getStringList("Menu.Settings.Categories.Item.Member.Lore"), null, null, null), 3);
                nInv.addItem(nInv.createItem(new ItemStack(Material.ITEM_FRAME),
                        configLoad.getString("Menu.Settings.Categories.Item.Operator.Displayname"),
                        configLoad.getStringList("Menu.Settings.Categories.Item.Operator.Lore"), null, null, null), 4);

                if (plugin.getConfiguration()
                        .getBoolean("Island.Coop.Enable")) {
                    nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                            configLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"), null, null, null,
                            null), 0);
                    nInv.addItem(nInv.createItem(new ItemStack(Material.NAME_TAG),
                            configLoad.getString("Menu.Settings.Categories.Item.Coop.Displayname"),
                            configLoad.getStringList("Menu.Settings.Categories.Item.Coop.Lore"), null, null, null), 6);
                    nInv.addItem(nInv.createItem(XMaterial.OAK_SAPLING.parseItem(),
                            configLoad.getString("Menu.Settings.Categories.Item.Owner.Displayname"),
                            configLoad.getStringList("Menu.Settings.Categories.Item.Owner.Lore"), null, null, null), 7);
                } else {
                    nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                            configLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"), null, null, null,
                            null), 0, 8);
                    nInv.addItem(nInv.createItem(XMaterial.OAK_SAPLING.parseItem(),
                            configLoad.getString("Menu.Settings.Categories.Item.Owner.Displayname"),
                            configLoad.getStringList("Menu.Settings.Categories.Item.Owner.Lore"), null, null, null), 6);
                }

                nInv.setTitle(plugin.formatText(configLoad.getString("Menu.Settings.Categories.Title")));
                nInv.setRows(1);

                Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
            } else if (menuType == Settings.Type.ROLE && role != null) {
                nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                    if (playerDataManager.hasPlayerData(player)) {
                        Island island14 = islandManager.getIsland(player);

                        if (island14 == null) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Settings.Owner.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            return;
                        } else if (!(island14.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                                || island14.hasRole(IslandRole.OWNER, player.getUniqueId()))) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Settings.Role.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            return;
                        } else if (island14.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                                && !permissionManager.hasPermission(island14, role.getFriendlyName(), IslandRole.OPERATOR)) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Settings.Permission.Access.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            return;
                        } else if (role == IslandRole.COOP) {
                            if (!fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"))
                                    .getFileConfiguration().getBoolean("Island.Coop.Enable")) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Command.Island.Coop.Disabled.Message"));
                                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                return;
                            }
                        }

                        ItemStack is = event.getItem();

                        if ((XMaterial.OAK_FENCE_GATE.isSimilar(is)) && (is.hasItemMeta())
                                && (is.getItemMeta().getDisplayName()
                                .equals(plugin.formatText(configLoad.getString("Menu.Settings." + role.getFriendlyName() + ".Item.Return.Displayname"))))) {
                            soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, Type.CATEGORIES, null, null), 1L);
                        } else if ((is.getType() == Material.PAPER) && (is.hasItemMeta())
                                && (is.getItemMeta().getDisplayName()
                                .equals(plugin.formatText(configLoad.getString("Menu.Settings.Visitor.Item.Signature.Displayname"))))) {
                            soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, Type.PANEL, null, Panel.SIGNATURE), 1L);
                        } else if ((is.hasItemMeta()) && (is.getItemMeta().getDisplayName()
                                .equals(plugin.formatText(configLoad.getString("Menu.Settings.Visitor.Item.Welcome.Displayname"))))) {
                            soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, Type.PANEL, null, Panel.WELCOME), 1L);
                        } else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta()) && (is.getItemMeta()
                                .getDisplayName().equals(plugin.formatText(configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"))))) {
                            switch (island14.getStatus()) {
                                case OPEN:
                                    islandManager.whitelistIsland(island14);
                                    soundManager.playSound(player, XSound.BLOCK_WOODEN_DOOR_CLOSE);
                                    break;
                                case CLOSED:
                                    island14.setStatus(IslandStatus.OPEN);
                                    soundManager.playSound(player, XSound.BLOCK_WOODEN_DOOR_OPEN);
                                    break;
                                case WHITELISTED:
                                    islandManager.closeIsland(island14);
                                    soundManager.playSound(player, XSound.BLOCK_WOODEN_DOOR_CLOSE);
                                    break;
                            }

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, Type.ROLE, IslandRole.VISITOR, null), 1L);
                        } else if (is.hasItemMeta()) {
                            String roleName = getRoleName(role);

                            for (IslandPermission settingList : island14.getSettings(role)) {
                                if (is.getItemMeta().getDisplayName()
                                        .equals(plugin.formatText(
                                                configLoad.getString("Menu.Settings." + roleName + ".Item.Setting."
                                                        + settingList.getPermission().getName() + ".Displayname")))) {
                                    if (!hasPermission(island14, player, role)) {
                                        messageManager.sendMessage(player, configLoad
                                                .getString("Command.Island.Settings.Permission.Change.Message"));
                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                        return;
                                    }

                                    settingList.setStatus(!settingList.getStatus());

                                    if (settingList.getPermission().getName().equals("KeepItemsOnDeath")
                                            || settingList.getPermission().getName().equals("PvP")
                                            || settingList.getPermission().getName().equals("Damage")) {
                                        island14.getVisit()
                                                .setSafeLevel(islandManager.getIslandSafeLevel(island14));
                                    }

                                    break;
                                }
                            }

                            soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, Type.ROLE, role, null), 1L);
                        }
                    }
                });

                if (role == IslandRole.VISITOR || role == IslandRole.MEMBER || role == IslandRole.COOP) {
                    if (role == IslandRole.VISITOR) {
                        FileManager.Config config = plugin.getFileManager()
                                .getConfig(new File(plugin.getDataFolder(), "config.yml"));
                        Visit visit = island.getVisit();

                        if (config.getFileConfiguration().getBoolean("Island.Visitor.Signature.Enable")) {
                            nInv.addItem(nInv.createItem(new ItemStack(Material.PAPER),
                                    configLoad.getString("Menu.Settings.Visitor.Item.Signature.Displayname"),
                                    configLoad.getStringList("Menu.Settings.Visitor.Item.Signature.Lore"), null, null,
                                    null), 3);
                        }

                        if (config.getFileConfiguration().getBoolean("Island.Visitor.Vote")) {
                            switch (visit.getStatus()) {
                                case OPEN:
                                    nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
                                            configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"),
                                            configLoad.getStringList(
                                                    "Menu.Settings.Visitor.Item.Statistics.Vote.Enabled.Open.Lore"),
                                            new Placeholder[]{new Placeholder("%visits", "" + visit.getVisitors().size()),
                                                    new Placeholder("%votes", "" + visit.getVoters().size()),
                                                    new Placeholder("%visitors",
                                                            "" + islandManager.getVisitorsAtIsland(island).size())},
                                            null, null), 4);
                                    break;
                                case CLOSED:
                                    nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
                                            configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"),
                                            configLoad.getStringList(
                                                    "Menu.Settings.Visitor.Item.Statistics.Vote.Enabled.Closed.Lore"),
                                            new Placeholder[]{new Placeholder("%visits", "" + visit.getVisitors().size()),
                                                    new Placeholder("%votes", "" + visit.getVoters().size()),
                                                    new Placeholder("%visitors",
                                                            "" + islandManager.getVisitorsAtIsland(island).size())},
                                            null, null), 4);
                                    break;
                                case WHITELISTED:
                                    nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
                                            configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"),
                                            configLoad.getStringList(
                                                    "Menu.Settings.Visitor.Item.Statistics.Vote.Enabled.Whitelisted.Lore"),
                                            new Placeholder[]{new Placeholder("%visits", "" + visit.getVisitors().size()),
                                                    new Placeholder("%votes", "" + visit.getVoters().size()),
                                                    new Placeholder("%visitors",
                                                            "" + islandManager.getVisitorsAtIsland(island).size())},
                                            null, null), 4);
                                    break;
                            }
                        } else {
                            switch (visit.getStatus()) {
                                case OPEN:
                                    nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
                                            configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"),
                                            configLoad.getStringList(
                                                    "Menu.Settings.Visitor.Item.Statistics.Vote.Disabled.Open.Lore"),
                                            new Placeholder[]{new Placeholder("%visits", "" + visit.getVisitors().size()),
                                                    new Placeholder("%visitors",
                                                            "" + islandManager.getVisitorsAtIsland(island).size())},
                                            null, null), 4);
                                    break;
                                case CLOSED:
                                    nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
                                            configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"),
                                            configLoad.getStringList(
                                                    "Menu.Settings.Visitor.Item.Statistics.Vote.Disabled.Closed.Lore"),
                                            new Placeholder[]{new Placeholder("%visits", "" + visit.getVisitors().size()),
                                                    new Placeholder("%visitors",
                                                            "" + islandManager.getVisitorsAtIsland(island).size())},
                                            null, null), 4);
                                    break;
                                case WHITELISTED:
                                    nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
                                            configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"),
                                            configLoad.getStringList(
                                                    "Menu.Settings.Visitor.Item.Statistics.Vote.Whitelisted.Closed.Lore"),
                                            new Placeholder[]{new Placeholder("%visits", "" + visit.getVisitors().size()),
                                                    new Placeholder("%visitors",
                                                            "" + islandManager.getVisitorsAtIsland(island).size())},
                                            null, null), 4);
                                    break;
                            }
                        }

                        if (config.getFileConfiguration().getBoolean("Island.Visitor.Welcome.Enable")) {
                            nInv.addItem(nInv.createItem(XMaterial.MAP.parseItem(),
                                    configLoad.getString("Menu.Settings.Visitor.Item.Welcome.Displayname"),
                                    configLoad.getStringList("Menu.Settings.Visitor.Item.Welcome.Lore"), null, null,
                                    null), 5);
                        }
                    }

                    nInv.addItemStack(createItem(island, role, "Destroy", new ItemStack(Material.DIAMOND_PICKAXE)), 9);
                    nInv.addItemStack(createItem(island, role, "Place", new ItemStack(XMaterial.GRASS_BLOCK.parseItem())), 10);
                    nInv.addItemStack(createItem(island, role, "Anvil", new ItemStack(Material.ANVIL)), 11);
                    nInv.addItemStack(createItem(island, role, "ArmorStandUse", new ItemStack(Material.ARMOR_STAND)),
                            12);
                    nInv.addItemStack(createItem(island, role, "Beacon", new ItemStack(Material.BEACON)), 13);
                    nInv.addItemStack(createItem(island, role, "Bed", XMaterial.WHITE_BED.parseItem()), 14);
                    nInv.addItemStack(createItem(island, role, "AnimalBreeding", new ItemStack(Material.WHEAT)), 15);
                    nInv.addItemStack(createItem(island, role, "Brewing",
                            new ItemStack(XMaterial.BREWING_STAND.parseMaterial())), 16);
                    nInv.addItemStack(createItem(island, role, "Bucket", new ItemStack(Material.BUCKET)), 17);
                    nInv.addItemStack(createItem(island, role, "WaterCollection", new ItemStack(Material.POTION)), 18);
                    nInv.addItemStack(createItem(island, role, "Storage", new ItemStack(Material.CHEST)), 19);
                    nInv.addItemStack(createItem(island, role, "Workbench", XMaterial.CRAFTING_TABLE.parseItem()), 20);
                    nInv.addItemStack(createItem(island, role, "Crop", XMaterial.WHEAT_SEEDS.parseItem()), 21);
                    nInv.addItemStack(createItem(island, role, "Door", XMaterial.OAK_DOOR.parseItem()), 22);
                    nInv.addItemStack(createItem(island, role, "Gate", XMaterial.OAK_FENCE_GATE.parseItem()), 23);
                    nInv.addItemStack(createItem(island, role, "Projectile", new ItemStack(Material.ARROW)), 24);
                    nInv.addItemStack(createItem(island, role, "Enchant", XMaterial.ENCHANTING_TABLE.parseItem()), 25);
                    nInv.addItemStack(createItem(island, role, "Fire", new ItemStack(Material.FLINT_AND_STEEL)), 26);
                    nInv.addItemStack(createItem(island, role, "Furnace", new ItemStack(Material.FURNACE)), 27);
                    nInv.addItemStack(createItem(island, role, "HorseInventory", XMaterial.CHEST_MINECART.parseItem()),
                            28);
                    nInv.addItemStack(createItem(island, role, "MobRiding", new ItemStack(Material.SADDLE)), 29);
                    nInv.addItemStack(createItem(island, role, "MonsterHurting", XMaterial.BONE.parseItem()), 30);
                    nInv.addItemStack(createItem(island, role, "MobHurting", XMaterial.WOODEN_SWORD.parseItem()), 31);
                    nInv.addItemStack(createItem(island, role, "MobTaming", XMaterial.POPPY.parseItem()), 32);
                    nInv.addItemStack(createItem(island, role, "Leash", XMaterial.LEAD.parseItem()), 33);
                    nInv.addItemStack(createItem(island, role, "LeverButton", new ItemStack(Material.LEVER)), 34);
                    nInv.addItemStack(createItem(island, role, "Milking", new ItemStack(Material.MILK_BUCKET)), 35);
                    nInv.addItemStack(createItem(island, role, "Jukebox", new ItemStack(Material.JUKEBOX)), 36);
                    nInv.addItemStack(createItem(island, role, "PressurePlate", XMaterial.OAK_PRESSURE_PLATE.parseItem()), 37);
                    nInv.addItemStack(createItem(island, role, "Redstone", new ItemStack(Material.REDSTONE)), 38);
                    nInv.addItemStack(createItem(island, role, "Shearing", new ItemStack(Material.SHEARS)), 39);
                    nInv.addItemStack(createItem(island, role, "Trading", new ItemStack(Material.EMERALD)), 40);
                    nInv.addItemStack(createItem(island, role, "ItemDrop", new ItemStack(Material.PUMPKIN_SEEDS)), 41);
                    nInv.addItemStack(createItem(island, role, "ItemPickup", new ItemStack(Material.MELON_SEEDS)), 42);
                    nInv.addItemStack(createItem(island, role, "Fishing", new ItemStack(Material.FISHING_ROD)), 43);
                    nInv.addItemStack(createItem(island, role, "DropperDispenser", new ItemStack(Material.DISPENSER)), 44);
                    nInv.addItemStack(createItem(island, role, "SpawnEgg", new ItemStack(Material.EGG)), 45);
                    nInv.addItemStack(createItem(island, role, "HangingDestroy", new ItemStack(Material.ITEM_FRAME)), 46);
                    nInv.addItemStack(createItem(island, role, "Cake", new ItemStack(Material.CAKE)), 47);
                    nInv.addItemStack(createItem(island, role, "DragonEggUse", new ItemStack(Material.DRAGON_EGG)), 48);
                    nInv.addItemStack(createItem(island, role, "MinecartBoat", new ItemStack(Material.MINECART)), 49);
                    nInv.addItemStack(createItem(island, role, "Portal", new ItemStack(Material.ENDER_PEARL)), 50);
                    nInv.addItemStack(createItem(island, role, "Hopper", new ItemStack(Material.HOPPER)), 51);
                    nInv.addItemStack(createItem(island, role, "EntityPlacement", new ItemStack(Material.ARMOR_STAND)), 52);
                    nInv.addItemStack(createItem(island, role, "ExperienceOrbPickup", XMaterial.EXPERIENCE_BOTTLE.parseItem()), 53);

                    nInv.setTitle(plugin.formatText(
                            configLoad.getString("Menu.Settings." + role.getFriendlyName() + ".Title")));
                    nInv.setRows(6);
                } else if (role == IslandRole.OPERATOR) {
                    if (mainConfig.getBoolean("Island.Visitor.Banning")) {
                        if (mainConfig.getBoolean("Island.Coop.Enable")) {
                            if (mainConfig.getBoolean("Island.WorldBorder.Enable")) {
                                nInv.addItemStack(
                                        createItem(island, role, "Invite", XMaterial.WRITABLE_BOOK.parseItem()), 9);
                                nInv.addItemStack(createItem(island, role, "Kick", new ItemStack(Material.IRON_DOOR)),
                                        10);
                                nInv.addItemStack(createItem(island, role, "Ban", new ItemStack(Material.IRON_AXE)),
                                        11);
                                nInv.addItemStack(createItem(island, role, "Unban", XMaterial.RED_DYE.parseItem()),
                                        12);
                                nInv.addItemStack(createItem(island, role, "Visitor", XMaterial.OAK_SIGN.parseItem()),
                                        13);
                                nInv.addItemStack(createItem(island, role, "Member", XMaterial.PAINTING.parseItem()),
                                        14);
                                nInv.addItemStack(createItem(island, role, "Island", XMaterial.OAK_SAPLING.parseItem()),
                                        15);
                                nInv.addItemStack(createItem(island, role, "Coop", new ItemStack(Material.NAME_TAG)),
                                        16);
                                nInv.addItemStack(createItem(island, role, "CoopPlayers", new ItemStack(Material.BOOK)),
                                        17);
                                nInv.addItemStack(
                                        createItem(island, role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
                                nInv.addItemStack(
                                        createItem(island, role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
                                        21);
                                nInv.addItemStack(createItem(island, role, "Border", new ItemStack(Material.BEACON)),
                                        22);
                                nInv.addItemStack(createItem(island, role, "Biome", new ItemStack(Material.MAP)), 23);
                                nInv.addItemStack(createItem(island, role, "Weather", XMaterial.CLOCK.parseItem()), 24);
                            } else {
                                nInv.addItemStack(
                                        createItem(island, role, "Invite", XMaterial.WRITABLE_BOOK.parseItem()), 9);
                                nInv.addItemStack(createItem(island, role, "Kick", new ItemStack(Material.IRON_DOOR)),
                                        10);
                                nInv.addItemStack(createItem(island, role, "Ban", new ItemStack(Material.IRON_AXE)),
                                        11);
                                nInv.addItemStack(createItem(island, role, "Unban", XMaterial.RED_DYE.parseItem()),
                                        12);
                                nInv.addItemStack(createItem(island, role, "Visitor", new ItemStack(XMaterial.OAK_SIGN.parseMaterial())),
                                        13);
                                nInv.addItemStack(createItem(island, role, "Member", new ItemStack(Material.PAINTING)),
                                        14);
                                nInv.addItemStack(createItem(island, role, "Island", XMaterial.OAK_SAPLING.parseItem()),
                                        15);
                                nInv.addItemStack(createItem(island, role, "Coop", new ItemStack(Material.NAME_TAG)),
                                        16);
                                nInv.addItemStack(createItem(island, role, "CoopPlayers", new ItemStack(Material.BOOK)),
                                        17);
                                nInv.addItemStack(
                                        createItem(island, role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
                                nInv.addItemStack(
                                        createItem(island, role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
                                        21);
                                nInv.addItemStack(createItem(island, role, "Biome", new ItemStack(Material.MAP)), 23);
                                nInv.addItemStack(createItem(island, role, "Weather", XMaterial.CLOCK.parseItem()), 24);
                            }
                        } else {
                            if (mainConfig.getBoolean("Island.WorldBorder.Enable")) {
                                nInv.addItemStack(
                                        createItem(island, role, "Invite", XMaterial.WRITABLE_BOOK.parseItem()), 10);
                                nInv.addItemStack(createItem(island, role, "Kick", new ItemStack(Material.IRON_DOOR)),
                                        11);
                                nInv.addItemStack(createItem(island, role, "Ban", new ItemStack(Material.IRON_AXE)),
                                        12);
                                nInv.addItemStack(createItem(island, role, "Unban", XMaterial.RED_DYE.parseItem()),
                                        13);
                                nInv.addItemStack(createItem(island, role, "Visitor", new ItemStack(XMaterial.OAK_SIGN.parseMaterial())),
                                        14);
                                nInv.addItemStack(createItem(island, role, "Member", new ItemStack(Material.PAINTING)),
                                        15);
                                nInv.addItemStack(createItem(island, role, "Island", XMaterial.OAK_SAPLING.parseItem()),
                                        16);
                                nInv.addItemStack(
                                        createItem(island, role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
                                nInv.addItemStack(
                                        createItem(island, role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
                                        21);
                                nInv.addItemStack(createItem(island, role, "Border", new ItemStack(Material.BEACON)),
                                        22);
                                nInv.addItemStack(createItem(island, role, "Biome", new ItemStack(Material.MAP)), 23);
                                nInv.addItemStack(createItem(island, role, "Weather", XMaterial.CLOCK.parseItem()), 24);
                            } else {
                                nInv.addItemStack(
                                        createItem(island, role, "Invite", XMaterial.WRITABLE_BOOK.parseItem()), 10);
                                nInv.addItemStack(createItem(island, role, "Kick", new ItemStack(Material.IRON_DOOR)),
                                        11);
                                nInv.addItemStack(createItem(island, role, "Ban", new ItemStack(Material.IRON_AXE)),
                                        12);
                                nInv.addItemStack(createItem(island, role, "Unban", XMaterial.RED_DYE.parseItem()),
                                        13);
                                nInv.addItemStack(createItem(island, role, "Visitor", new ItemStack(XMaterial.OAK_SIGN.parseMaterial())),
                                        14);
                                nInv.addItemStack(createItem(island, role, "Member", new ItemStack(Material.PAINTING)),
                                        15);
                                nInv.addItemStack(createItem(island, role, "Island", XMaterial.OAK_SAPLING.parseItem()),
                                        16);
                                nInv.addItemStack(
                                        createItem(island, role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
                                nInv.addItemStack(
                                        createItem(island, role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
                                        21);
                                nInv.addItemStack(createItem(island, role, "Biome", new ItemStack(Material.MAP)), 23);
                                nInv.addItemStack(createItem(island, role, "Weather", XMaterial.CLOCK.parseItem()), 24);
                            }
                        }

                        nInv.setRows(3);
                    } else {
                        if (mainConfig.getBoolean("Island.Coop.Enable")) {
                            if (mainConfig.getBoolean("Island.WorldBorder.Enable")) {
                                nInv.addItemStack(
                                        createItem(island, role, "Invite", XMaterial.WRITABLE_BOOK.parseItem()), 10);
                                nInv.addItemStack(createItem(island, role, "Kick", new ItemStack(Material.IRON_DOOR)),
                                        11);
                                nInv.addItemStack(createItem(island, role, "Visitor", new ItemStack(XMaterial.OAK_SIGN.parseMaterial())),
                                        12);
                                nInv.addItemStack(createItem(island, role, "Member", new ItemStack(Material.PAINTING)),
                                        13);
                                nInv.addItemStack(createItem(island, role, "Island", XMaterial.OAK_SAPLING.parseItem()),
                                        14);
                                nInv.addItemStack(createItem(island, role, "Coop", new ItemStack(Material.NAME_TAG)),
                                        15);
                                nInv.addItemStack(createItem(island, role, "CoopPlayers", new ItemStack(Material.BOOK)),
                                        16);
                                nInv.addItemStack(
                                        createItem(island, role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
                                nInv.addItemStack(
                                        createItem(island, role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
                                        21);
                                nInv.addItemStack(createItem(island, role, "Border", new ItemStack(Material.BEACON)),
                                        22);
                                nInv.addItemStack(createItem(island, role, "Biome", new ItemStack(Material.MAP)), 23);
                                nInv.addItemStack(createItem(island, role, "Weather", XMaterial.CLOCK.parseItem()), 24);
                            } else {
                                nInv.addItemStack(
                                        createItem(island, role, "Invite", XMaterial.WRITABLE_BOOK.parseItem()), 10);
                                nInv.addItemStack(createItem(island, role, "Kick", new ItemStack(Material.IRON_DOOR)),
                                        11);
                                nInv.addItemStack(createItem(island, role, "Visitor", new ItemStack(XMaterial.OAK_SIGN.parseMaterial())),
                                        12);
                                nInv.addItemStack(createItem(island, role, "Member", new ItemStack(Material.PAINTING)),
                                        13);
                                nInv.addItemStack(createItem(island, role, "Island", XMaterial.OAK_SAPLING.parseItem()),
                                        14);
                                nInv.addItemStack(createItem(island, role, "Coop", new ItemStack(Material.NAME_TAG)),
                                        15);
                                nInv.addItemStack(createItem(island, role, "CoopPlayers", new ItemStack(Material.BOOK)),
                                        16);
                                nInv.addItemStack(
                                        createItem(island, role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
                                nInv.addItemStack(
                                        createItem(island, role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
                                        21);
                                nInv.addItemStack(createItem(island, role, "Biome", new ItemStack(Material.MAP)), 23);
                                nInv.addItemStack(createItem(island, role, "Weather", XMaterial.CLOCK.parseItem()), 24);
                            }

                            nInv.setRows(3);
                        } else {
                            if (mainConfig.getBoolean("Island.WorldBorder.Enable")) {
                                nInv.addItemStack(
                                        createItem(island, role, "Invite", XMaterial.WRITABLE_BOOK.parseItem()), 10);
                                nInv.addItemStack(createItem(island, role, "Kick", new ItemStack(Material.IRON_DOOR)),
                                        11);
                                nInv.addItemStack(createItem(island, role, "Visitor", new ItemStack(XMaterial.OAK_SIGN.parseMaterial())),
                                        12);
                                nInv.addItemStack(createItem(island, role, "Member", new ItemStack(Material.PAINTING)),
                                        13);
                                nInv.addItemStack(createItem(island, role, "Island", XMaterial.OAK_SAPLING.parseItem()),
                                        14);
                                nInv.addItemStack(
                                        createItem(island, role, "MainSpawn", new ItemStack(Material.EMERALD)), 15);
                                nInv.addItemStack(
                                        createItem(island, role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
                                        16);
                                nInv.addItemStack(createItem(island, role, "Border", new ItemStack(Material.BEACON)),
                                        21);
                                nInv.addItemStack(createItem(island, role, "Biome", new ItemStack(Material.MAP)), 22);
                                nInv.addItemStack(createItem(island, role, "Weather", XMaterial.CLOCK.parseItem()), 23);

                                nInv.setRows(3);
                            } else {
                                nInv.addItemStack(
                                        createItem(island, role, "Invite", XMaterial.WRITABLE_BOOK.parseItem()), 9);
                                nInv.addItemStack(createItem(island, role, "Kick", new ItemStack(Material.IRON_DOOR)),
                                        10);
                                nInv.addItemStack(createItem(island, role, "Visitor", new ItemStack(XMaterial.OAK_SIGN.parseMaterial())),
                                        11);
                                nInv.addItemStack(createItem(island, role, "Member", new ItemStack(Material.PAINTING)),
                                        12);
                                nInv.addItemStack(createItem(island, role, "Island", XMaterial.OAK_SAPLING.parseItem()),
                                        13);
                                nInv.addItemStack(
                                        createItem(island, role, "MainSpawn", new ItemStack(Material.EMERALD)), 14);
                                nInv.addItemStack(
                                        createItem(island, role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
                                        15);
                                nInv.addItemStack(createItem(island, role, "Biome", new ItemStack(Material.MAP)), 16);
                                nInv.addItemStack(createItem(island, role, "Weather", XMaterial.CLOCK.parseItem()), 17);

                                nInv.setRows(2);
                            }
                        }
                    }

                    nInv.setTitle(plugin.formatText(
                            configLoad.getString("Menu.Settings." + role.getFriendlyName() + ".Title")));
                } else if (role == IslandRole.OWNER) {
                    if (mainConfig.getBoolean("Island.Settings.PvP.Enable")) {
                        if (mainConfig.getBoolean("Island.Settings.KeepItemsOnDeath.Enable")) {
                            if (mainConfig.getBoolean("Island.Settings.Damage.Enable")) {
                                if (mainConfig.getBoolean("Island.Settings.Hunger.Enable")) {
                                    nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
                                            XMaterial.PIG_SPAWN_EGG.parseItem()), 9);
                                    nInv.addItemStack(
                                            createItem(island, role, "MobGriefing", XMaterial.IRON_SHOVEL.parseItem()),
                                            10);
                                    nInv.addItemStack(
                                            createItem(island, role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 11);
                                    nInv.addItemStack(
                                            createItem(island, role, "Explosions", XMaterial.GUNPOWDER.parseItem()),
                                            12);
                                    nInv.addItemStack(createItem(island, role, "FireSpread",
                                            new ItemStack(Material.FLINT_AND_STEEL)), 13);
                                    nInv.addItemStack(
                                            createItem(island, role, "LeafDecay", XMaterial.OAK_LEAVES.parseItem()),
                                            14);
                                    nInv.addItemStack(createItem(island, role, "KeepItemsOnDeath",
                                            new ItemStack(Material.ITEM_FRAME)), 15);
                                    nInv.addItemStack(
                                            createItem(island, role, "Damage", XMaterial.RED_DYE.parseItem()), 16);
                                    nInv.addItemStack(
                                            createItem(island, role, "Hunger", new ItemStack(Material.COOKED_BEEF)),
                                            17);
                                } else {
                                    nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
                                            XMaterial.PIG_SPAWN_EGG.parseItem()), 9);
                                    nInv.addItemStack(
                                            createItem(island, role, "MobGriefing", XMaterial.IRON_SHOVEL.parseItem()),
                                            10);
                                    nInv.addItemStack(
                                            createItem(island, role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 11);
                                    nInv.addItemStack(
                                            createItem(island, role, "Explosions", XMaterial.GUNPOWDER.parseItem()),
                                            12);
                                    nInv.addItemStack(createItem(island, role, "FireSpread",
                                            new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                    nInv.addItemStack(
                                            createItem(island, role, "LeafDecay", XMaterial.OAK_LEAVES.parseItem()),
                                            15);
                                    nInv.addItemStack(createItem(island, role, "KeepItemsOnDeath",
                                            new ItemStack(Material.ITEM_FRAME)), 16);
                                    nInv.addItemStack(
                                            createItem(island, role, "Damage", XMaterial.RED_DYE.parseItem()), 17);
                                }
                            } else {
                                if (mainConfig.getBoolean("Island.Settings.Hunger.Enable")) {
                                    nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
                                            XMaterial.PIG_SPAWN_EGG.parseItem()), 9);
                                    nInv.addItemStack(
                                            createItem(island, role, "MobGriefing", XMaterial.IRON_SHOVEL.parseItem()),
                                            10);
                                    nInv.addItemStack(
                                            createItem(island, role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 11);
                                    nInv.addItemStack(
                                            createItem(island, role, "Explosions", XMaterial.GUNPOWDER.parseItem()),
                                            12);
                                    nInv.addItemStack(createItem(island, role, "FireSpread",
                                            new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                    nInv.addItemStack(
                                            createItem(island, role, "LeafDecay", XMaterial.OAK_LEAVES.parseItem()),
                                            15);
                                    nInv.addItemStack(createItem(island, role, "KeepItemsOnDeath",
                                            new ItemStack(Material.ITEM_FRAME)), 16);
                                    nInv.addItemStack(
                                            createItem(island, role, "Hunger", new ItemStack(Material.COOKED_BEEF)),
                                            17);
                                } else {
                                    nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
                                            XMaterial.PIG_SPAWN_EGG.parseItem()), 10);
                                    nInv.addItemStack(
                                            createItem(island, role, "MobGriefing", XMaterial.IRON_SHOVEL.parseItem()),
                                            11);
                                    nInv.addItemStack(
                                            createItem(island, role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 12);
                                    nInv.addItemStack(
                                            createItem(island, role, "Explosions", XMaterial.GUNPOWDER.parseItem()),
                                            13);
                                    nInv.addItemStack(createItem(island, role, "FireSpread",
                                            new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                    nInv.addItemStack(
                                            createItem(island, role, "LeafDecay", XMaterial.OAK_LEAVES.parseItem()),
                                            15);
                                    nInv.addItemStack(createItem(island, role, "KeepItemsOnDeath",
                                            new ItemStack(Material.ITEM_FRAME)), 16);
                                }
                            }
                        } else {
                            if (mainConfig.getBoolean("Island.Settings.Damage.Enable")) {
                                if (mainConfig.getBoolean("Island.Settings.Hunger.Enable")) {
                                    nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
                                            XMaterial.PIG_SPAWN_EGG.parseItem()), 9);
                                    nInv.addItemStack(
                                            createItem(island, role, "MobGriefing", XMaterial.IRON_SHOVEL.parseItem()),
                                            10);
                                    nInv.addItemStack(
                                            createItem(island, role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 11);
                                    nInv.addItemStack(
                                            createItem(island, role, "Explosions", XMaterial.GUNPOWDER.parseItem()),
                                            12);
                                    nInv.addItemStack(createItem(island, role, "FireSpread",
                                            new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                    nInv.addItemStack(
                                            createItem(island, role, "LeafDecay", XMaterial.OAK_LEAVES.parseItem()),
                                            15);
                                    nInv.addItemStack(
                                            createItem(island, role, "Damage", XMaterial.RED_DYE.parseItem()), 16);
                                    nInv.addItemStack(
                                            createItem(island, role, "Hunger", new ItemStack(Material.COOKED_BEEF)),
                                            17);
                                } else {
                                    nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
                                            XMaterial.PIG_SPAWN_EGG.parseItem()), 10);
                                    nInv.addItemStack(
                                            createItem(island, role, "MobGriefing", XMaterial.IRON_SHOVEL.parseItem()),
                                            11);
                                    nInv.addItemStack(
                                            createItem(island, role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 12);
                                    nInv.addItemStack(
                                            createItem(island, role, "Explosions", XMaterial.GUNPOWDER.parseItem()),
                                            13);
                                    nInv.addItemStack(createItem(island, role, "FireSpread",
                                            new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                    nInv.addItemStack(
                                            createItem(island, role, "LeafDecay", XMaterial.OAK_LEAVES.parseItem()),
                                            15);
                                    nInv.addItemStack(
                                            createItem(island, role, "Damage", XMaterial.RED_DYE.parseItem()), 16);
                                }
                            } else {
                                if (mainConfig.getBoolean("Island.Settings.Hunger.Enable")) {
                                    nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
                                            XMaterial.PIG_SPAWN_EGG.parseItem()), 10);
                                    nInv.addItemStack(
                                            createItem(island, role, "MobGriefing", XMaterial.IRON_SHOVEL.parseItem()),
                                            11);
                                    nInv.addItemStack(
                                            createItem(island, role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 12);
                                    nInv.addItemStack(
                                            createItem(island, role, "Explosions", XMaterial.GUNPOWDER.parseItem()),
                                            13);
                                    nInv.addItemStack(createItem(island, role, "FireSpread",
                                            new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                    nInv.addItemStack(
                                            createItem(island, role, "LeafDecay", XMaterial.OAK_LEAVES.parseItem()),
                                            15);
                                    nInv.addItemStack(
                                            createItem(island, role, "Hunger", new ItemStack(Material.COOKED_BEEF)),
                                            16);
                                } else {
                                    nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
                                            XMaterial.PIG_SPAWN_EGG.parseItem()), 10);
                                    nInv.addItemStack(
                                            createItem(island, role, "MobGriefing", XMaterial.IRON_SHOVEL.parseItem()),
                                            11);
                                    nInv.addItemStack(
                                            createItem(island, role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 12);
                                    nInv.addItemStack(
                                            createItem(island, role, "Explosions", XMaterial.GUNPOWDER.parseItem()),
                                            14);
                                    nInv.addItemStack(createItem(island, role, "FireSpread",
                                            new ItemStack(Material.FLINT_AND_STEEL)), 15);
                                    nInv.addItemStack(
                                            createItem(island, role, "LeafDecay", XMaterial.OAK_LEAVES.parseItem()),
                                            16);
                                }
                            }
                        }
                    } else {
                        if (mainConfig.getBoolean("Island.Settings.KeepItemsOnDeath.Enable")) {
                            if (mainConfig.getBoolean("Island.Settings.Damage.Enable")) {
                                if (mainConfig.getBoolean("Island.Settings.Hunger.Enable")) {
                                    nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
                                            XMaterial.PIG_SPAWN_EGG.parseItem()), 9);
                                    nInv.addItemStack(
                                            createItem(island, role, "MobGriefing", XMaterial.IRON_SHOVEL.parseItem()),
                                            10);
                                    nInv.addItemStack(
                                            createItem(island, role, "Explosions", XMaterial.GUNPOWDER.parseItem()),
                                            11);
                                    nInv.addItemStack(createItem(island, role, "FireSpread",
                                            new ItemStack(Material.FLINT_AND_STEEL)), 12);
                                    nInv.addItemStack(
                                            createItem(island, role, "LeafDecay", XMaterial.OAK_LEAVES.parseItem()),
                                            14);
                                    nInv.addItemStack(createItem(island, role, "KeepItemsOnDeath",
                                            new ItemStack(Material.ITEM_FRAME)), 15);
                                    nInv.addItemStack(
                                            createItem(island, role, "Damage", XMaterial.RED_DYE.parseItem()), 16);
                                    nInv.addItemStack(
                                            createItem(island, role, "Hunger", new ItemStack(Material.COOKED_BEEF)),
                                            17);
                                } else {
                                    nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
                                            XMaterial.PIG_SPAWN_EGG.parseItem()), 10);
                                    nInv.addItemStack(
                                            createItem(island, role, "MobGriefing", XMaterial.IRON_SHOVEL.parseItem()),
                                            11);
                                    nInv.addItemStack(
                                            createItem(island, role, "Explosions", XMaterial.GUNPOWDER.parseItem()),
                                            12);
                                    nInv.addItemStack(createItem(island, role, "FireSpread",
                                            new ItemStack(Material.FLINT_AND_STEEL)), 13);
                                    nInv.addItemStack(
                                            createItem(island, role, "LeafDecay", XMaterial.OAK_LEAVES.parseItem()),
                                            14);
                                    nInv.addItemStack(createItem(island, role, "KeepItemsOnDeath",
                                            new ItemStack(Material.ITEM_FRAME)), 15);
                                    nInv.addItemStack(
                                            createItem(island, role, "Damage", XMaterial.RED_DYE.parseItem()), 16);
                                }
                            } else {
                                if (mainConfig.getBoolean("Island.Settings.Hunger.Enable")) {
                                    nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
                                            XMaterial.PIG_SPAWN_EGG.parseItem()), 10);
                                    nInv.addItemStack(
                                            createItem(island, role, "MobGriefing", XMaterial.IRON_SHOVEL.parseItem()),
                                            11);
                                    nInv.addItemStack(
                                            createItem(island, role, "Explosions", XMaterial.GUNPOWDER.parseItem()),
                                            12);
                                    nInv.addItemStack(createItem(island, role, "FireSpread",
                                            new ItemStack(Material.FLINT_AND_STEEL)), 13);
                                    nInv.addItemStack(
                                            createItem(island, role, "LeafDecay", XMaterial.OAK_LEAVES.parseItem()),
                                            14);
                                    nInv.addItemStack(createItem(island, role, "KeepItemsOnDeath",
                                            new ItemStack(Material.ITEM_FRAME)), 15);
                                    nInv.addItemStack(
                                            createItem(island, role, "Hunger", new ItemStack(Material.COOKED_BEEF)),
                                            16);
                                } else {
                                    nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
                                            XMaterial.PIG_SPAWN_EGG.parseItem()), 10);
                                    nInv.addItemStack(
                                            createItem(island, role, "MobGriefing", XMaterial.IRON_SHOVEL.parseItem()),
                                            11);
                                    nInv.addItemStack(
                                            createItem(island, role, "Explosions", XMaterial.GUNPOWDER.parseItem()),
                                            12);
                                    nInv.addItemStack(createItem(island, role, "FireSpread",
                                            new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                    nInv.addItemStack(
                                            createItem(island, role, "LeafDecay", XMaterial.OAK_LEAVES.parseItem()),
                                            15);
                                    nInv.addItemStack(createItem(island, role, "KeepItemsOnDeath",
                                            new ItemStack(Material.ITEM_FRAME)), 16);
                                }
                            }
                        } else {
                            if (mainConfig.getBoolean("Island.Settings.Damage.Enable")) {
                                if (mainConfig.getBoolean("Island.Settings.Hunger.Enable")) {
                                    nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
                                            XMaterial.PIG_SPAWN_EGG.parseItem()), 10);
                                    nInv.addItemStack(
                                            createItem(island, role, "MobGriefing", XMaterial.IRON_SHOVEL.parseItem()),
                                            11);
                                    nInv.addItemStack(
                                            createItem(island, role, "Explosions", XMaterial.GUNPOWDER.parseItem()),
                                            12);
                                    nInv.addItemStack(createItem(island, role, "FireSpread",
                                            new ItemStack(Material.FLINT_AND_STEEL)), 13);
                                    nInv.addItemStack(
                                            createItem(island, role, "LeafDecay", XMaterial.OAK_LEAVES.parseItem()),
                                            14);
                                    nInv.addItemStack(
                                            createItem(island, role, "Damage", XMaterial.RED_DYE.parseItem()), 15);
                                    nInv.addItemStack(
                                            createItem(island, role, "Hunger", new ItemStack(Material.COOKED_BEEF)),
                                            16);
                                } else {
                                    nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
                                            XMaterial.PIG_SPAWN_EGG.parseItem()), 10);
                                    nInv.addItemStack(
                                            createItem(island, role, "MobGriefing", XMaterial.IRON_SHOVEL.parseItem()),
                                            11);
                                    nInv.addItemStack(
                                            createItem(island, role, "Explosions", XMaterial.GUNPOWDER.parseItem()),
                                            12);
                                    nInv.addItemStack(createItem(island, role, "FireSpread",
                                            new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                    nInv.addItemStack(
                                            createItem(island, role, "LeafDecay", XMaterial.OAK_LEAVES.parseItem()),
                                            15);
                                    nInv.addItemStack(
                                            createItem(island, role, "Damage", XMaterial.RED_DYE.parseItem()), 16);
                                }
                            } else {
                                if (mainConfig.getBoolean("Island.Settings.Hunger.Enable")) {
                                    nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
                                            XMaterial.PIG_SPAWN_EGG.parseItem()), 10);
                                    nInv.addItemStack(
                                            createItem(island, role, "MobGriefing", XMaterial.IRON_SHOVEL.parseItem()),
                                            11);
                                    nInv.addItemStack(
                                            createItem(island, role, "Explosions", XMaterial.GUNPOWDER.parseItem()),
                                            12);
                                    nInv.addItemStack(createItem(island, role, "FireSpread",
                                            new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                    nInv.addItemStack(
                                            createItem(island, role, "LeafDecay", XMaterial.OAK_LEAVES.parseItem()),
                                            15);
                                    nInv.addItemStack(
                                            createItem(island, role, "Hunger", new ItemStack(Material.COOKED_BEEF)),
                                            16);
                                } else {
                                    nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
                                            XMaterial.PIG_SPAWN_EGG.parseItem()), 11);
                                    nInv.addItemStack(
                                            createItem(island, role, "MobGriefing", XMaterial.IRON_SHOVEL.parseItem()),
                                            12);
                                    nInv.addItemStack(
                                            createItem(island, role, "Explosions", XMaterial.GUNPOWDER.parseItem()),
                                            13);
                                    nInv.addItemStack(createItem(island, role, "FireSpread",
                                            new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                    nInv.addItemStack(
                                            createItem(island, role, "LeafDecay", XMaterial.OAK_LEAVES.parseItem()),
                                            15);
                                }
                            }
                        }
                    }

                    nInv.setTitle(plugin.formatText(
                            configLoad.getString("Menu.Settings." + role.getFriendlyName() + ".Title")));
                    nInv.setRows(2);
                }

                nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                        configLoad.getString("Menu.Settings." + role.getFriendlyName() + ".Item.Return.Displayname"), null, null,
                        null, null), 0, 8);

                Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
            } else if (menuType == Settings.Type.PANEL) {
                if (panel == Settings.Panel.WELCOME) {
                    nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                        if (playerDataManager.hasPlayerData(player)) {
                            Island island15 = islandManager.getIsland(player);

                            if (island15 == null) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Command.Island.Settings.Owner.Message"));
                                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                return;
                            } else if (!(island15.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                                    || island15.hasRole(IslandRole.OWNER, player.getUniqueId()))) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Command.Island.Role.Message"));
                                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                return;
                            }

                            if (!plugin.getFileManager()
                                    .getConfig(new File(plugin.getDataFolder(), "config.yml"))
                                    .getFileConfiguration().getBoolean("Island.Visitor.Welcome.Enable")) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Settings.Visitor.Welcome.Disabled.Message"));
                                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                return;
                            }

                            ItemStack is = event.getItem();

                            if ((is.getType() == XMaterial.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    plugin.formatText(configLoad.getString(
                                            "Menu.Settings.Visitor.Panel.Welcome.Item.Return.Displayname"))))) {
                                soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                                Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                        () -> open(player, Type.ROLE, IslandRole.VISITOR, null), 1L);
                            } else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    plugin.formatText(configLoad.getString(
                                            "Menu.Settings.Visitor.Item.Statistics.Displayname"))))) {
                                switch (island15.getStatus()) {
                                    case OPEN:
                                        islandManager.whitelistIsland(island15);
                                        soundManager.playSound(player, XSound.BLOCK_WOODEN_DOOR_CLOSE);
                                        break;
                                    case CLOSED:
                                        island15.setStatus(IslandStatus.OPEN);
                                        soundManager.playSound(player, XSound.BLOCK_WOODEN_DOOR_OPEN);
                                        break;
                                    case WHITELISTED:
                                        islandManager.closeIsland(island15);
                                        soundManager.playSound(player, XSound.BLOCK_WOODEN_DOOR_CLOSE);
                                        break;
                                }

                                Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                        () -> open(player, Type.ROLE, IslandRole.VISITOR, null), 1L);
                            } else if ((is.hasItemMeta()) && (is.getItemMeta().getDisplayName()
                                    .equals(plugin.formatText(configLoad.getString(
                                            "Menu.Settings.Visitor.Panel.Welcome.Item.Message.Displayname"))))) {
                                soundManager.playSound(player, XSound.ENTITY_CHICKEN_EGG);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            } else if ((is.getType() == Material.ARROW) && (is.hasItemMeta()) && (is.getItemMeta()
                                    .getDisplayName()
                                    .equals(plugin.formatText(configLoad.getString(
                                            "Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Displayname"))))) {
                                if (island15.getMessage(IslandMessage.WELCOME).size() >= plugin.getFileManager()
                                        .getConfig(new File(plugin.getDataFolder(), "config.yml"))
                                        .getFileConfiguration().getInt("Island.Visitor.Welcome.Lines")) {
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                    event.setWillClose(false);
                                    event.setWillDestroy(false);
                                } else {
                                    soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> {
                                                AnvilGui gui = new AnvilGui(player);
                                                gui.setAction(event1 -> {

                                                    Island island1 = islandManager.getIsland(player);

                                                    if (island1 == null) {
                                                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Owner.Message"));
                                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                                        player.closeInventory();

                                                        return;
                                                    } else if (!(island1.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                                                            || island1.hasRole(IslandRole.OWNER, player.getUniqueId()))) {
                                                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Role.Message"));
                                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                                        player.closeInventory();

                                                        return;
                                                    } else if (!plugin.getFileManager()
                                                            .getConfig(new File(plugin.getDataFolder(), "config.yml"))
                                                            .getFileConfiguration()
                                                            .getBoolean("Island.Visitor.Welcome.Enable")) {
                                                        messageManager.sendMessage(player,
                                                                configLoad.getString("Island.Settings.Visitor.Welcome.Disabled.Message"));
                                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                                        player.closeInventory();

                                                        return;
                                                    }

                                                    FileManager.Config config1 = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
                                                    FileConfiguration configLoad1 = config1.getFileConfiguration();

                                                    if (island1.getMessage(IslandMessage.WELCOME).size() > configLoad1.getInt("Island.Visitor.Welcome.Lines")
                                                            || gui.getInputText().length() > configLoad1.getInt("Island.Visitor.Welcome.Length")) {
                                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                                    } else {
                                                        List<String> welcomeMessage = island1.getMessage(IslandMessage.WELCOME);
                                                        welcomeMessage.add(gui.getInputText());
                                                        island1.setMessage(IslandMessage.WELCOME, player.getName(), welcomeMessage);
                                                        soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);
                                                    }

                                                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, Type.PANEL, null, Panel.WELCOME), 1L);

                                                    player.closeInventory();
                                                });

                                                ItemStack is1 = new ItemStack(Material.NAME_TAG);
                                                ItemMeta im = is1.getItemMeta();
                                                im.setDisplayName(configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Word.Enter"));
                                                is1.setItemMeta(im);

                                                gui.setInput(is1);
                                                plugin.getGuiManager().showGUI(player, gui);
                                            }, 1L);
                                }
                            } else if ((is.getType() == Material.ARROW) && (is.hasItemMeta()) && (is.getItemMeta()
                                    .getDisplayName()
                                    .equals(plugin.formatText(configLoad.getString(
                                            "Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Displayname"))))) {
                                List<String> welcomeMessage = island15.getMessage(IslandMessage.WELCOME);

                                if (welcomeMessage.isEmpty()) {
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                    event.setWillClose(false);
                                    event.setWillDestroy(false);
                                } else {
                                    welcomeMessage.remove(welcomeMessage.size() - 1);
                                    island15.setMessage(IslandMessage.WELCOME,
                                            island15.getMessageAuthor(IslandMessage.WELCOME), welcomeMessage);
                                    soundManager.playSound(player, XSound.ENTITY_GENERIC_EXPLODE);

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player, Type.PANEL, null, Panel.WELCOME), 1L);
                                }
                            }
                        }
                    });

                    List<String> welcomeMessage = island.getMessage(IslandMessage.WELCOME);

                    if (welcomeMessage.size() == mainConfig
                            .getInt("Island.Visitor.Welcome.Lines")) {
                        nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW),
                                configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Displayname"),
                                configLoad
                                        .getStringList("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Limit.Lore"),
                                null, null, null), 1);
                    } else {
                        nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW),
                                configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Displayname"),
                                configLoad.getStringList("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.More.Lore"),
                                null, null, null), 1);
                    }

                    if (welcomeMessage.isEmpty()) {
                        List<String> itemLore = new ArrayList<>();
                        itemLore.add(
                                configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Message.Word.Empty"));
                        nInv.addItem(nInv.createItem(new ItemStack(XMaterial.OAK_SIGN.parseMaterial()),
                                configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Message.Displayname"),
                                itemLore, null, null, null), 2);
                        nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW),
                                configLoad
                                        .getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Displayname"),
                                configLoad.getStringList(
                                        "Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.None.Lore"),
                                null, null, null), 3);
                    } else {
                        nInv.addItem(nInv.createItem(new ItemStack(XMaterial.OAK_SIGN.parseMaterial(), welcomeMessage.size()),
                                configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Message.Displayname"),
                                welcomeMessage, null, null, null), 2);
                        nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW),
                                configLoad
                                        .getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Displayname"),
                                configLoad.getStringList(
                                        "Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Lines.Lore"),
                                null, null, null), 3);
                    }

                    nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                            configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Return.Displayname"), null,
                            null, null, null), 0, 4);

                    nInv.setTitle(plugin.formatText(configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Title")));
                    nInv.setType(InventoryType.HOPPER);

                    Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
                } else if (panel == Settings.Panel.SIGNATURE) {
                    nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                        if (playerDataManager.hasPlayerData(player)) {
                            Island island12 = islandManager.getIsland(player);

                            if (island12 == null) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Command.Island.Settings.Owner.Message"));
                                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                return;
                            } else if (!(island12.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                                    || island12.hasRole(IslandRole.OWNER, player.getUniqueId()))) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Command.Island.Role.Message"));
                                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                return;
                            }

                            if (!plugin.getFileManager()
                                    .getConfig(new File(plugin.getDataFolder(), "config.yml"))
                                    .getFileConfiguration().getBoolean("Island.Visitor.Signature.Enable")) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Settings.Visitor.Signature.Disabled.Message"));
                                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                return;
                            }

                            ItemStack is = event.getItem();

                            if ((is.getType() == XMaterial.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    plugin.formatText(configLoad.getString(
                                            "Menu.Settings.Visitor.Panel.Signature.Item.Return.Displayname"))))) {
                                soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                                Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                        () -> open(player, Type.ROLE, IslandRole.VISITOR, null), 1L);
                            } else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    plugin.formatText(configLoad.getString(
                                            "Menu.Settings.Visitor.Item.Statistics.Displayname"))))) {
                                switch (island12.getStatus()) {
                                    case OPEN:
                                        islandManager.whitelistIsland(island12);
                                        soundManager.playSound(player, XSound.BLOCK_WOODEN_DOOR_CLOSE);
                                        break;
                                    case CLOSED:
                                        island12.setStatus(IslandStatus.OPEN);
                                        soundManager.playSound(player, XSound.BLOCK_WOODEN_DOOR_OPEN);
                                        break;
                                    case WHITELISTED:
                                        islandManager.closeIsland(island12);
                                        soundManager.playSound(player, XSound.BLOCK_WOODEN_DOOR_CLOSE);
                                        break;
                                }

                                Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                        () -> open(player, Type.ROLE, IslandRole.VISITOR, null), 1L);
                            } else if ((is.hasItemMeta()) && (is.getItemMeta().getDisplayName()
                                    .equals(plugin.formatText(configLoad.getString(
                                            "Menu.Settings.Visitor.Panel.Signature.Item.Message.Displayname"))))) {
                                soundManager.playSound(player, XSound.ENTITY_CHICKEN_EGG);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            } else if ((is.getType() == Material.ARROW) && (is.hasItemMeta()) && (is.getItemMeta()
                                    .getDisplayName()
                                    .equals(plugin.formatText(configLoad.getString(
                                            "Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Displayname"))))) {
                                if (island12.getMessage(IslandMessage.SIGNATURE).size() >= plugin.getFileManager()
                                        .getConfig(new File(plugin.getDataFolder(), "config.yml"))
                                        .getFileConfiguration().getInt("Island.Visitor.Signature.Lines")) {
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                    event.setWillClose(false);
                                    event.setWillDestroy(false);
                                } else {
                                    soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> {
                                                AnvilGui gui = new AnvilGui(player);
                                                gui.setAction(event1 -> {

                                                    Island island1 = islandManager.getIsland(player);

                                                    if (island1 == null) {
                                                        messageManager.sendMessage(player,
                                                                configLoad.getString(
                                                                        "Command.Island.Settings.Owner.Message"));
                                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                                        player.closeInventory();
                                                        return;
                                                    } else if (!(island1.hasRole(IslandRole.OPERATOR,
                                                            player.getUniqueId())
                                                            || island1.hasRole(IslandRole.OWNER,
                                                            player.getUniqueId()))) {
                                                        messageManager.sendMessage(player, configLoad
                                                                .getString("Command.Island.Role.Message"));
                                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                                        player.closeInventory();
                                                        return;
                                                    } else if (!plugin.getFileManager()
                                                            .getConfig(new File(plugin.getDataFolder(), "config.yml"))
                                                            .getFileConfiguration().getBoolean("Island.Visitor.Signature.Enable")) {
                                                        messageManager.sendMessage(player, configLoad.getString("Island.Settings.Visitor.Signature.Disabled.Message"));
                                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                                        player.closeInventory();

                                                        return;
                                                    }

                                                    FileManager.Config config1 = plugin.getFileManager()
                                                            .getConfig(new File(plugin.getDataFolder(),
                                                                    "config.yml"));
                                                    FileConfiguration configLoad1 = config1
                                                            .getFileConfiguration();

                                                    if (island1.getMessage(IslandMessage.SIGNATURE).size() > configLoad1.getInt("Island.Visitor.Signature.Lines")
                                                            || gui.getInputText().length() > configLoad1.getInt("Island.Visitor.Signature.Length")) {
                                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                                    } else {
                                                        List<String> signatureMessage = island1.getMessage(IslandMessage.SIGNATURE);
                                                        signatureMessage.add(gui.getInputText());
                                                        island1.setMessage(IslandMessage.SIGNATURE, player.getName(), signatureMessage);
                                                        soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);
                                                    }

                                                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, Type.PANEL, null, Panel.SIGNATURE), 1L);

                                                    player.closeInventory();
                                                });

                                                ItemStack is12 = new ItemStack(Material.NAME_TAG);
                                                ItemMeta im = is12.getItemMeta();
                                                im.setDisplayName(configLoad.getString(
                                                        "Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Word.Enter"));
                                                is12.setItemMeta(im);

                                                gui.setInput(is12);
                                                plugin.getGuiManager().showGUI(player, gui);

                                            }, 1L);
                                }
                            } else if ((is.getType() == Material.ARROW) && (is.hasItemMeta()) && (is.getItemMeta()
                                    .getDisplayName()
                                    .equals(plugin.formatText(configLoad.getString(
                                            "Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.Displayname"))))) {
                                List<String> signatureMessage = island12.getMessage(IslandMessage.SIGNATURE);

                                if (signatureMessage.size() == 0) {
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                    event.setWillClose(false);
                                    event.setWillDestroy(false);
                                } else {
                                    signatureMessage.remove(signatureMessage.size() - 1);
                                    island12.setMessage(IslandMessage.SIGNATURE,
                                            island12.getMessageAuthor(IslandMessage.SIGNATURE), signatureMessage);
                                    soundManager.playSound(player, XSound.ENTITY_GENERIC_EXPLODE);

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, Type.PANEL, null, Panel.SIGNATURE), 1L);
                                }
                            }
                        }
                    });

                    List<String> signatureMessage = island.getMessage(IslandMessage.SIGNATURE);

                    if (signatureMessage.size() == mainConfig
                            .getInt("Island.Visitor.Signature.Lines")) {
                        nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW),
                                configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Displayname"),
                                configLoad.getStringList(
                                        "Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Limit.Lore"),
                                null, null, null), 1);
                    } else {
                        nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW),
                                configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Displayname"),
                                configLoad
                                        .getStringList("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.More.Lore"),
                                null, null, null), 1);
                    }

                    if (signatureMessage.isEmpty()) {
                        List<String> itemLore = new ArrayList<>();
                        itemLore.add(
                                configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Message.Word.Empty"));
                        nInv.addItem(nInv.createItem(new ItemStack(XMaterial.OAK_SIGN.parseMaterial()),
                                configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Message.Displayname"),
                                itemLore, null, null, null), 2);
                        nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW),
                                configLoad.getString(
                                        "Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.Displayname"),
                                configLoad.getStringList(
                                        "Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.None.Lore"),
                                null, null, null), 3);
                    } else {
                        nInv.addItem(nInv.createItem(new ItemStack(XMaterial.OAK_SIGN.parseMaterial(), signatureMessage.size()),
                                configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Message.Displayname"),
                                signatureMessage, null, null, null), 2);
                        nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW),
                                configLoad.getString(
                                        "Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.Displayname"),
                                configLoad.getStringList(
                                        "Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.Lines.Lore"),
                                null, null, null), 3);
                    }

                    nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                            configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Return.Displayname"), null,
                            null, null, null), 0, 4);

                    nInv.setTitle(plugin.formatText(
                            configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Title")));
                    nInv.setType(InventoryType.HOPPER);

                    Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
                }
            }
        }
    }

    private ItemStack createItem(Island island, IslandRole role, String setting, ItemStack is) {
        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);
        PermissionManager permissionManager = plugin.getPermissionManager();

        FileManager.Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        List<String> itemLore = new ArrayList<>();

        ItemMeta im = is.getItemMeta();

        String roleName = role.getFriendlyName();

        if (role == IslandRole.VISITOR || role == IslandRole.MEMBER || role == IslandRole.COOP) {
            roleName = "Default";
        }

        im.setDisplayName(plugin.formatText(
                configLoad.getString("Menu.Settings." + roleName + ".Item.Setting." + setting + ".Displayname")));

        if (island.hasPermission(role, permissionManager.getPermission(setting))) {
            for (String itemLoreList : configLoad
                    .getStringList("Menu.Settings." + roleName + ".Item.Setting.Status.Enabled.Lore")) {
                itemLore.add(plugin.formatText(itemLoreList));
            }
        } else {
            for (String itemLoreList : configLoad
                    .getStringList("Menu.Settings." + roleName + ".Item.Setting.Status.Disabled.Lore")) {
                itemLore.add(plugin.formatText(itemLoreList));
            }
        }

        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        im.setLore(itemLore);
        is.setItemMeta(im);

        return is;
    }

    private String getRoleName(IslandRole role) {
        if (role == IslandRole.VISITOR || role == IslandRole.MEMBER || role == IslandRole.COOP) {
            return "Default";
        }

        return role.getFriendlyName();
    }

    private boolean hasPermission(Island island, Player player, IslandRole role) {
        PermissionManager permissionManager = SkyBlock.getPlugin(SkyBlock.class).getPermissionManager();
        if (role == IslandRole.VISITOR || role == IslandRole.MEMBER || role == IslandRole.COOP
                || role == IslandRole.OWNER) {
            String roleName = role.getFriendlyName();

            if (role == IslandRole.OWNER) {
                roleName = "Island";
            }

            return !island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                    || permissionManager.hasPermission(island, roleName, IslandRole.OPERATOR);
        } else if (role == IslandRole.OPERATOR) {
            return island.hasRole(IslandRole.OWNER, player.getUniqueId());
        }

        return true;
    }

    public enum Panel {
        WELCOME, SIGNATURE
    }

    public enum Type {
        CATEGORIES, PANEL, ROLE
    }
}
