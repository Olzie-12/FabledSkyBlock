package com.songoda.skyblock.gui;

import com.songoda.core.gui.AnvilGui;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandMessage;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GuiWelcomeEditor extends Gui {
    private final SkyBlock plugin;
    private final FileConfiguration configLoad;
    private final Gui returnGui;
    private final Island island;
    private final FileManager.Config mainConfig;
    private final MessageManager messageManager;
    private final IslandManager islandManager;

    public GuiWelcomeEditor(SkyBlock plugin, Gui returnGui, Island island) {
        super(1);
        this.plugin = plugin;
        this.returnGui = returnGui;
        this.island = island;
        this.configLoad = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();
        this.mainConfig = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
        this.messageManager = plugin.getMessageManager();
        this.islandManager = plugin.getIslandManager();
        setDefaultItem(null);
        setTitle(TextUtils.formatText(this.configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Title")));
        paint();
    }

    public void paint() {
        List<String> welcomeMessage = this.island.getMessage(IslandMessage.WELCOME);
        setButton(2, GuiUtils.createButtonItem(XMaterial.OAK_FENCE_GATE,
                        TextUtils.formatText(this.configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Return.Displayname"))),
                (event) -> this.guiManager.showGUI(event.player, this.returnGui));
        setButton(6, GuiUtils.createButtonItem(XMaterial.OAK_FENCE_GATE,
                        TextUtils.formatText(this.configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Return.Displayname"))),
                (event) -> this.guiManager.showGUI(event.player, this.returnGui));

        setButton(3, GuiUtils.createButtonItem(XMaterial.ARROW,
                        TextUtils.formatText(this.configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Displayname")),
                        TextUtils.formatText(this.configLoad.getStringList(
                                welcomeMessage.size() == this.mainConfig.getFileConfiguration().getInt("Island.Visitor.Welcome.Lines")
                                        ? "Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Limit.Lore"
                                        : "Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.More.Lore"))),
                (event -> {
                    AnvilGui gui = new AnvilGui(event.player, this);
                    gui.setAction((e -> {
                        if (!hasPermission(e.player)) {
                            return;
                        }
                        if (this.island.getMessage(IslandMessage.WELCOME)
                                .size() > this.mainConfig.getFileConfiguration().getInt(
                                "Island.Visitor.Welcome.Lines")
                                || gui.getInputText().length() > this.mainConfig.getFileConfiguration()
                                .getInt("Island.Visitor.Welcome.Length")) {
                            this.plugin.getSoundManager().playSound(e.player, XSound.BLOCK_ANVIL_LAND);
                        } else {
                            welcomeMessage.add(gui.getInputText().trim());
                            this.island.setMessage(IslandMessage.WELCOME, e.player.getName(), welcomeMessage);
                            this.plugin.getSoundManager().playSound(e.player, XSound.BLOCK_NOTE_BLOCK_PLING);
                        }
                        e.player.closeInventory();
                        paint();
                    }));
                    gui.setTitle(this.configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Word.Enter"));
                    this.guiManager.showGUI(event.player, gui);
                }));

        List<String> itemLore = new ArrayList<>();
        itemLore.add(this.configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Message.Word.Empty"));
        setItem(4, GuiUtils.createButtonItem(XMaterial.OAK_SIGN,
                TextUtils.formatText(this.configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Message.Displayname")),
                TextUtils.formatText(welcomeMessage.isEmpty() ? itemLore : welcomeMessage)));

        setButton(5, GuiUtils.createButtonItem(XMaterial.ARROW,
                        TextUtils.formatText(this.configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Displayname")),
                        TextUtils.formatText(this.configLoad.getStringList(
                                welcomeMessage.isEmpty()
                                        ? "Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.None.Lore"
                                        : "Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Lines.Lore"))),
                (event -> {
                    welcomeMessage.remove(welcomeMessage.size() - 1);
                    this.island.setMessage(IslandMessage.WELCOME, event.player.getName(), welcomeMessage);
                    paint();
                }));
    }

    private boolean hasPermission(Player player) {
        Island island1 = this.islandManager.getIsland(player);

        if (island1 == null) {
            this.messageManager.sendMessage(player,
                    this.configLoad.getString(
                            "Command.Island.Settings.Owner.Message"));
            this.plugin.getSoundManager().playSound(player, XSound.BLOCK_ANVIL_LAND);
            player.closeInventory();
            return false;
        } else if (!(island1.hasRole(IslandRole.OPERATOR,
                player.getUniqueId())
                || island1.hasRole(IslandRole.OWNER,
                player.getUniqueId()))) {
            this.messageManager.sendMessage(player, this.configLoad
                    .getString("Command.Island.Role.Message"));
            this.plugin.getSoundManager().playSound(player, XSound.BLOCK_ANVIL_LAND);
            player.closeInventory();
            return false;
        } else if (!this.plugin.getFileManager()
                .getConfig(new File(this.plugin.getDataFolder(),
                        "config.yml"))
                .getFileConfiguration().getBoolean(
                        "Island.Visitor.Welcome.Enable")) {
            this.messageManager.sendMessage(player,
                    this.configLoad.getString(
                            "Island.Settings.Visitor.Welcome.Disabled.Message"));
            this.plugin.getSoundManager().playSound(player, XSound.BLOCK_ANVIL_LAND);
            return false;
        }
        return true;
    }
}
